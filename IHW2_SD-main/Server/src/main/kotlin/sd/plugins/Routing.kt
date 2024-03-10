package sd.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import restaurant.usersystem.TypeOfUser

// Дата классы для де и сериализации

@Serializable
private data class UserData(val login: String, val password: String)

fun Application.configureRoutingAuthSystem() {
    routing {
        post("/loginVisitor") {
            try {
                val rawData = call.receive<String>()
                val data = Json.decodeFromString<UserData>(rawData)
                val visitor = SystemGetter.system.tryAuthVisitor(data.login, data.password)
                val token = TokenSystem.createToken(visitor.id.toString(), visitor)
                call.respond(HttpStatusCode.OK, token)
            } catch (ex : BadRequestException) {
                call.respond(HttpStatusCode.BadRequest,"Data of request is incorrect")
            } catch (ex : Exception) {
                call.respond(HttpStatusCode.BadRequest, ex.message.toString())
            }
        }

        post("/loginAdmin") {
            try {
                val rawData = call.receive<String>()
                val data = Json.decodeFromString<UserData>(rawData)
                val admin = SystemGetter.system.tryAuthAdmin(data.login, data.password)
                val token = TokenSystem.createToken(admin.id.toString(), admin)
                call.respond(HttpStatusCode.OK, token)
            } catch (ex : BadRequestException) {
                call.respond(HttpStatusCode.BadRequest,"Data of request is incorrect")
            } catch (ex : Exception) {
                call.respond(HttpStatusCode.BadRequest, ex.message.toString())
            }
        }

        post("/registerNewVisitor") {
            try {
                val rawData = call.receive<String>()
                val data = Json.decodeFromString<UserData>(rawData)
                val result = SystemGetter.system.registerNewUser(data.login, data.password, TypeOfUser.Visitor)
                call.respond(HttpStatusCode.OK, result.toString())
            } catch (ex : BadRequestException) {
                call.respond(HttpStatusCode.BadRequest,"Data of request is incorrect")
            } catch (ex : Exception) {
                call.respond(HttpStatusCode.BadRequest, ex.message.toString())
            }
        }

        post("/registerNewAdmin") {
            try {
                val rawData = call.receive<String>()
                val data = Json.decodeFromString<UserData>(rawData)
                val result = SystemGetter.system.registerNewUser(data.login, data.password, TypeOfUser.Admin)
                call.respond(HttpStatusCode.OK, result.toString())
            } catch (ex : BadRequestException) {
                call.respond(HttpStatusCode.BadRequest,"Data of request is incorrect")
            } catch (ex : Exception) {
                call.respond(HttpStatusCode.BadRequest, ex.message.toString())
            }
        }


        get("/") {
            call.respond(HttpStatusCode.OK, "Welcome to our Restaurant! Please authorize")
        }
    }
}
