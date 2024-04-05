# A presentation of a file exchanging case

## Key creation
Keys were created by using the  [tint-key](https://developers.google.com/tink/install-tinkey "tinkey")

The key type that is used is the Hybrid Public Key Encryption ([HPKE](https://www.rfc-editor.org/rfc/rfc9180.html)). It uses a key encapsulation mechanism (KEM) to derive the shared secret,
a key derivation function (KDF) to derive the sender and receiver context, and an authenticated encryption with associated data [AEAD](https://en.wikipedia.org/wiki/Authenticated_encryption#Authenticated_encryption_with_associated_data) algorithm.


### Private key

``` tinkey create-keyset --key-template DHKEM_X25519_HKDF_SHA256_HKDF_SHA256_AES_256_GCM --out keyset.json ```


### Public key
``` tinkey create-public-keyset --in keyset.json --out pubkey.json ```


## Hybrid Encryption method
This encryption has the best of the symmetric and asymmetric worlds, by having the efficiency of the symmetric encryption and the convinience of the asymmetric.
Encyption is performed by using the public key, so everyone that has access to this key can encrypt. 
Decryption is performed though by the private key, that is being kept in a secure location. Only the holder of the private key can decrypt the messages.

### Hybrid Encryption
1. The sender generates a symmetric key to encrypt the plaintext of the text to be transmitted.
2. Ciphertext is produced.
3. That symmetric key is encapsulated with the recipient's public key. 

### Hybrid Decryption
1. The symmetric key is decapsulated by the recipient 
2. The key is used to decrypt the ciphertext to get the original message.

## Properties of this method
1. Private key is necessary to get the information about the encrypted plaintext. This adds secrecy since the private key is securely kept and the access is restricted.
2. Two messages with the same plaintext will not produce the same ciphertext.
3. Plaintext and context info can have arbitrary length, the solution covers both small and big files.
4. Secured against [Adaptive chosen-ciphertext attacks](https://en.wikipedia.org/wiki/Adaptive_chosen-ciphertext_attack)
5. The implemented approach does not provide authenticity. In order to do so, data must be digitally signed by using for example an asymmetric algorithm like ECSA where the private key is used to sign the data and the public key to verify.

