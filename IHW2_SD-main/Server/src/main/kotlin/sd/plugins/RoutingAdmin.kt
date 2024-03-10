package sd.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import restaurant.dish.ProductFeatures
import kotlin.time.Duration

// Дата классы для де и сериализации

fun Application.configureRoutingAdminSystem() {
    routing {
        post("/admin/addDishToMenu") {
            try {
                val rawData = call.receive<String>()
                val data = Json.decodeFromString<AddDishData>(rawData)
                val admin = TokenSystem.getAdminByToken(data.token)
                val id = admin.addDishToMenu(data.name, data.price, Duration.parse(data.timeProduction), data.count)
                call.respond(HttpStatusCode.OK, id.toString())
            } catch (ex: BadRequestException) {
                call.respond(HttpStatusCode.BadRequest, "Data of request is incorrect")
            } catch (ex: IllegalAccessException) {
                call.respond(HttpStatusCode.Unauthorized, ex.message.toString())
            } catch (ex: Exception) {
                call.respond(HttpStatusCode.BadRequest, ex.message.toString())
            }
        }

        post("/admin/removeDishFromMenu") {
            try {
                val rawData = call.receive<String>()
                val data = Json.decodeFromString<RemoveDishData>(rawData)
                val admin = TokenSystem.getAdminByToken(data.token)
                admin.removeDishFromMenu(data.id.toInt())
                call.respond(HttpStatusCode.OK, "OK")
            } catch (ex: BadRequestException) {
                call.respond(HttpStatusCode.BadRequest, "Data of request is incorrect")
            } catch (ex: IllegalAccessException) {
                call.respond(HttpStatusCode.Unauthorized, ex.message.toString())
            } catch (ex: Exception) {
                call.respond(HttpStatusCode.BadRequest, ex.message.toString())
            }
        }

        post("/admin/setParamToDish") {
            try {
                val rawData = call.receive<String>()
                val data = Json.decodeFromString<SetParamData>(rawData)
                val admin = TokenSystem.getAdminByToken(data.token)
                admin.setParamToDish(data.dishId.toInt(), data.params, data.type)
                call.respond(HttpStatusCode.OK, "OK")
            } catch (ex: BadRequestException) {
                call.respond(HttpStatusCode.BadRequest, "Data of request is incorrect")
            } catch (ex: IllegalAccessException) {
                call.respond(HttpStatusCode.Unauthorized, ex.message.toString())
            } catch (ex: Exception) {
                call.respond(HttpStatusCode.BadRequest, ex.message.toString())
            }
        }

        post("/admin/increaseTheNumberOfDish") {
            try {
                val rawData = call.receive<String>()
                val data = Json.decodeFromString<AddNumberToDishData>(rawData)
                val admin = TokenSystem.getAdminByToken(data.token)
                admin.increaseTheNumberOfDish(data.dishId.toInt(), data.delta)
                call.respond(HttpStatusCode.OK, "OK")
            } catch (ex: BadRequestException) {
                call.respond(HttpStatusCode.BadRequest, "Data of request is incorrect")
            } catch (ex: IllegalAccessException) {
                call.respond(HttpStatusCode.Unauthorized, ex.message.toString())
            } catch (ex: Exception) {
                call.respond(HttpStatusCode.BadRequest, ex.message.toString())
            }
        }

        post("/admin/getStatistics") {
            try {
                val rawData = call.receive<String>()
                val data = Json.decodeFromString<JustToken>(rawData)
                val admin = TokenSystem.getAdminByToken(data.token)
                call.respond(HttpStatusCode.OK, admin.getStatistic())
            } catch (ex: BadRequestException) {
                call.respond(HttpStatusCode.BadRequest, "Data of request is incorrect")
            } catch (ex: IllegalAccessException) {
                call.respond(HttpStatusCode.Unauthorized, ex.message.toString())
            } catch (ex: Exception) {
                call.respond(HttpStatusCode.BadRequest, ex.message.toString())
            }
        }

        post("/admin/exit") {
            try {
                val rawData = call.receive<String>()
                val data = Json.decodeFromString<JustToken>(rawData)
                val admin = TokenSystem.getAdminByToken(data.token)
                SystemGetter.system.exitUser(admin)
                call.respond(HttpStatusCode.OK)
            } catch (ex: BadRequestException) {
                call.respond(HttpStatusCode.BadRequest, "Data of request is incorrect")
            } catch (ex: IllegalAccessException) {
                call.respond(HttpStatusCode.Unauthorized, ex.message.toString())
            } catch (ex: Exception) {
                call.respond(HttpStatusCode.BadRequest, ex.message.toString())
            }
        }

    }
}


@Serializable
private data class JustToken(val token: String)

@Serializable
private data class AddDishData(
    val token: String,
    val name: String,
    val price: Int,
    val timeProduction: String,
    val count: Int = 1
)

@Serializable
private data class RemoveDishData(val token: String, val id: String)

@Serializable
private data class SetParamData(val token: String, val dishId: String, val params: ProductFeatures, val type: String)

@Serializable
private data class AddNumberToDishData(val token: String, val dishId: String, val delta: Int)
