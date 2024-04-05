package org.example

import com.google.crypto.tink.HybridDecrypt
import com.google.crypto.tink.HybridEncrypt
import com.google.crypto.tink.InsecureSecretKeyAccess
import com.google.crypto.tink.TinkJsonProtoKeysetFormat
import com.google.crypto.tink.hybrid.HybridConfig
import kotlinx.coroutines.coroutineScope
import java.io.File
import java.nio.file.Files.readAllBytes
import java.nio.file.Paths

/**
 * In the scenario below, a sender "sends" a small/medium/big file to recipient.
 * Sender encrypts the message before sending it, using the public key, and the recipient receives the encrypted file
 * and by using the private key decrypts this message.
 */
suspend fun main() = coroutineScope {
    register()

    // you can choose between small_file.txt, medium_file.txt and big_file.txt
    val fileName = "medium_file.txt"
    val plainText = readPlainText(fileName)

    println("Sender: Sending the file..")
    val ciphertext = send(plainText)
    if (ciphertext != null) {
        receive(fileName, ciphertext)
        println("Recipient: File received!")

    }
}

// Can be empty or null, but to ensure the correct decryption of the cipher, the same context info value must be provided for decryption.
var contextInfo = "steryos special context".toByteArray()

fun register() {
    HybridConfig.register();
}

fun readPlainText(fileName:String): String {
    return String(readAllBytes(Paths.get("src/main/resources/sender_vault/$fileName")))
}

// encrypt and send the file
fun send(plainText: String): ByteArray? {
    val ciphertext = encrypt(plainText)

    // ciphertext is transmitted using a secure channel
    return ciphertext
}

fun encrypt(file: String): ByteArray? {

    // in real world scenarios, the sender does not need to store the public key of the receiver in a vault
    val pubkeyHandle =
        TinkJsonProtoKeysetFormat.parseKeyset(
            String(readAllBytes(Paths.get("src/main/resources/secure_vault/pubkey.json"))), InsecureSecretKeyAccess.get()
        )
    val encryptor: HybridEncrypt = pubkeyHandle.getPrimitive(HybridEncrypt::class.java)
    val ciphertext = encryptor.encrypt(file.toByteArray(), contextInfo)
    return ciphertext;
}

fun decrypt(cipherText: ByteArray): ByteArray? {
    // in real world scenarios, pub key should be retained by a secure vault using a secure channel
    val privateKeyHandle =
        TinkJsonProtoKeysetFormat.parseKeyset(
            String(readAllBytes(Paths.get("src/main/resources/secure_vault/keyset.json"))), InsecureSecretKeyAccess.get()
        )
    val decryptor = privateKeyHandle.getPrimitive(HybridDecrypt::class.java)

    // Use the primitive to decrypt data.
    return decryptor.decrypt(cipherText, contextInfo)
}

fun receive(file: String, cipherText: ByteArray) {
    val plaintext = decrypt(cipherText)
    if (plaintext != null) {
        File("src/main/resources/receiver_vault/$file").writeText(String(plaintext))
    }
}