package sd.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// Дата классы для де и сериализации

fun Application.configureRoutingVisitorSystem() {
    routing {
        post("/order/makeOrder") {
            try {
                val rawData = call.receive<String>()
                val data = Json.decodeFromString<MakeOrderData>(rawData)
                val visitor = TokenSystem.getVisitorByToken(data.token)
                val id = visitor.makeOrder(data.listOfOrder)
                call.respond(HttpStatusCode.OK, id.toString())
            } catch (ex: BadRequestException) {
                call.respond(HttpStatusCode.BadRequest, "Data of request is incorrect")
            } catch (ex: IllegalAccessException) {
                call.respond(HttpStatusCode.Unauthorized, ex.message.toString())
            } catch (ex: Exception) {
                call.respond(HttpStatusCode.BadRequest, ex.message.toString())
            }
        }

        post("/order/addToOrderOneDish") {
            try {
                val rawData = call.receive<String>()
                val data = Json.decodeFromString<AddToOrderData>(rawData)
                val visitor = TokenSystem.getVisitorByToken(data.token)
                val id = visitor.addToOrder(data.orderId, data.dishId)
                call.respond(HttpStatusCode.OK, id.toString())
            } catch (ex: BadRequestException) {
                call.respond(HttpStatusCode.BadRequest, "Data of request is incorrect")
            } catch (ex: IllegalAccessException) {
                call.respond(HttpStatusCode.Unauthorized, ex.message.toString())
            } catch (ex: Exception) {
                call.respond(HttpStatusCode.BadRequest, ex.message.toString())
            }
        }

        post("/order/addToOrderManyDish") {
            try {
                val rawData = call.receive<String>()
                val data = Json.decodeFromString<AddToOrderManyDishData>(rawData)
                val visitor = TokenSystem.getVisitorByToken(data.token)
                val id = visitor.addToOrder(data.orderId, data.dishes)
                call.respond(HttpStatusCode.OK, id.toString())
            } catch (ex: BadRequestException) {
                call.respond(HttpStatusCode.BadRequest, "Data of request is incorrect")
            } catch (ex: IllegalAccessException) {
                call.respond(HttpStatusCode.Unauthorized, ex.message.toString())
            } catch (ex: Exception) {
                call.respond(HttpStatusCode.BadRequest, ex.message.toString())
            }
        }

        post("/order/cancelOrder") {
            try {
                val rawData = call.receive<String>()
                val data = Json.decodeFromString<JustOrderData>(rawData)
                val visitor = TokenSystem.getVisitorByToken(data.token)
                val id = visitor.cancelOrder(data.orderId)
                call.respond(HttpStatusCode.OK, id.toString())
            } catch (ex: BadRequestException) {
                call.respond(HttpStatusCode.BadRequest, "Data of request is incorrect")
            } catch (ex: IllegalAccessException) {
                call.respond(HttpStatusCode.Unauthorized, ex.message.toString())
            } catch (ex: Exception) {
                call.respond(HttpStatusCode.BadRequest, ex.message.toString())
            }
        }

        post("/order/getOrderStatus") {
            try {
                val rawData = call.receive<String>()
                val data = Json.decodeFromString<JustOrderData>(rawData)
                val visitor = TokenSystem.getVisitorByToken(data.token)
                call.respond(HttpStatusCode.OK, visitor.getOrderStatus(data.orderId).toString())
            } catch (ex: BadRequestException) {
                call.respond(HttpStatusCode.BadRequest, "Data of request is incorrect")
            } catch (ex: IllegalAccessException) {
                call.respond(HttpStatusCode.Unauthorized, ex.message.toString())
            } catch (ex: Exception) {
                call.respond(HttpStatusCode.BadRequest, ex.message.toString())
            }
        }

        post("/order/payOrder") {
            try {
                val rawData = call.receive<String>()
                val data = Json.decodeFromString<JustOrderData>(rawData)
                val visitor = TokenSystem.getVisitorByToken(data.token)
                call.respond(HttpStatusCode.OK, visitor.payOrder(data.orderId))
            } catch (ex: BadRequestException) {
                call.respond(HttpStatusCode.BadRequest, "Data of request is incorrect")
            } catch (ex: IllegalAccessException) {
                call.respond(HttpStatusCode.Unauthorized, ex.message.toString())
            } catch (ex: Exception) {
                call.respond(HttpStatusCode.BadRequest, ex.message.toString())
            }
        }

        post("/order/leaveFeedbackAboutData") {
            try {
                val rawData = call.receive<String>()
                val data = Json.decodeFromString<FeedbackOrderData>(rawData)
                val visitor = TokenSystem.getVisitorByToken(data.token)
                visitor.leaveFeedbackAboutOrder(data.orderId, data.stars, data.comment)
                call.respond(HttpStatusCode.OK, "OK")
            } catch (ex: BadRequestException) {
                call.respond(HttpStatusCode.BadRequest, "Data of request is incorrect")
            } catch (ex: IllegalAccessException) {
                call.respond(HttpStatusCode.Unauthorized, ex.message.toString())
            } catch (ex: Exception) {
                call.respond(HttpStatusCode.BadRequest, ex.message.toString())
            }
        }

        post("/visitor/exit") {
            try {
                val rawData = call.receive<String>()
                val data = Json.decodeFromString<JustTokenData>(rawData)
                val visitor = TokenSystem.getVisitorByToken(data.token)
                SystemGetter.system.exitUser(visitor)
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
private data class MakeOrderData(val token: String, val listOfOrder: MutableMap<Int, Int>)

@Serializable
private data class AddToOrderData(val token: String, val orderId: Int, val dishId: Int)

@Serializable
private data class AddToOrderManyDishData(val token: String, val orderId: Int, val dishes: MutableMap<Int, Int>)

@Serializable
private data class JustOrderData(val token: String, val orderId: Int)

@Serializable
private data class FeedbackOrderData(val token: String, val orderId: Int, val stars: Int, val comment: String)

@Serializable
private data class JustTokenData(val token: String)
