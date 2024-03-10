package restaurant

import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

internal object Serializer {
    private val lockInWrite = Any()
    val json = Json{allowStructuredMapKeys = true}

    const val dishListFile = "data/menu.ser"
    const val dishIdGetterFile = "data/last_id_menu.ser"
    const val allOrdersFile = "data/orders.ser"
    const val orderIdGetterFile = "data/last_id_order.ser"
    const val usersFile = "data/users.ser"
    const val userIdGetterFile = "data/lastid_user.ser"

    fun write(text : String, path : String) : Boolean {
        if (!Files.exists(Paths.get("./data"))) {
            Files.createDirectory(Paths.get("./data"))
        }

        return try {
            synchronized(lockInWrite) {
                File(path).bufferedWriter().use { out ->
                    out.write(Encryptor.encryptThis(text))
                }
            }
            true
        } catch (_ : Exception) {
            false
        }
    }

    fun read(nameOfFile: String): String? {
        return try {
            File(nameOfFile).bufferedReader().use { out -> Encryptor.decrypt(out.readText()) }
        } catch (ex : Exception) {
            null
        }
    }
}