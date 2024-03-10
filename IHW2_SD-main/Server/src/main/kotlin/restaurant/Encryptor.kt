package restaurant

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

internal object Encryptor {
    private var password = "ASDFGHEYVN47CB4F"

    @OptIn(ExperimentalEncodingApi::class)
    fun encryptThis(input: String): String {
        // Создаем объект шифра
        val cipher = Cipher.getInstance("AES")

        // Создает уникальный ключ и инициализируем шифр
        val keySpec = SecretKeySpec(password.toByteArray(), "AES")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)

        // Шифруем и возвращаем зашифрованное
        val encrypt = cipher.doFinal(input.toByteArray())
        return Base64.encode(encrypt)
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun decrypt(input: String): String {
        // Создаем объект шифра
        val cipher = Cipher.getInstance("AES")

        // Создает уникальный ключ и инициализируем шифр
        val keySpec = SecretKeySpec(password.toByteArray(), "AES")
        cipher.init(Cipher.DECRYPT_MODE, keySpec)

        // Расшифруем и возвращаем
        val encrypt = cipher.doFinal(Base64.decode(input))
        return String(encrypt)
    }
}