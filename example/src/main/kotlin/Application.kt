import io.ktor.server.application.Application
import io.ktor.server.application.serverConfig
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import plugins.configureHTTP
import plugins.configureRouting
import plugins.configureSecurity
import plugins.configureSerialization

fun main() {
    embeddedServer(
        factory = Netty,
        rootConfig = serverConfig {
            developmentMode = true
            watchPaths = listOf("classes", "resources")
            module(Application::module)
        },
        configure = {
            connector {
                host = "0.0.0.0"
                port = 12345
            }
        }
    ).start(wait = true)
}

fun Application.module() {
    configureHTTP()
    configureSerialization()
    configureSecurity()
    configureRouting()
}
