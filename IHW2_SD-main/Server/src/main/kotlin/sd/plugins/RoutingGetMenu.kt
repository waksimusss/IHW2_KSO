package sd.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRoutingMenu() {
    routing {
        post("/getMenu") {
            try {
                call.respond(HttpStatusCode.OK, SystemGetter.system.getMenu())
            } catch (ex: BadRequestException) {
                call.respond(HttpStatusCode.BadRequest,"Data of request is incorrect")
            } catch (ex: Exception) {
                call.respond(HttpStatusCode.BadRequest, ex.message.toString())
            }
        }
    }
}