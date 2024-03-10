package sd

import sd.plugins.configureRoutingVisitorSystem
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import sd.plugins.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSecurity()
    configureSerialization()
    configureRoutingAuthSystem()
    configureRoutingAdminSystem()
    configureRoutingVisitorSystem()
    configureRoutingMenu()
}
