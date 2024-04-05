import org.example.contextInfo
import org.example.decrypt
import org.example.encrypt
import org.example.register
import java.security.GeneralSecurityException
import kotlin.test.*

class MainKtTest {

    @BeforeTest
    fun init() {
        register()
    }

    @Test
    fun testEncryptionAndDecryption() {
        val plainText = "Hello World"
        val cipher = encrypt(plainText)
        assertNotNull(cipher)
        val receivedText = decrypt(cipher)

        assertNotNull(receivedText)
        assertEquals(plainText, String(receivedText))
    }

    @Test
    fun differentContextInfoWillFailDecryption() {
        val plainText = "Hello World"
        val cipher = encrypt(plainText)
        assertNotNull(cipher)
        contextInfo = "whole new context info".toByteArray()
        assertFailsWith(
            exceptionClass = GeneralSecurityException::class,
            block = { decrypt(cipher) }
        )
    }

    @Test
    fun contextInfoCanBeEmpty() {
        contextInfo = "".toByteArray()

        val plainText = "Hello World"
        val cipher = encrypt(plainText)
        assertNotNull(cipher)

        val receivedText = decrypt(cipher)

        assertNotNull(receivedText)
        assertEquals(plainText, String(receivedText))
    }


}