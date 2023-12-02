import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import plugins.configureHTTP
import plugins.configureRouting
import plugins.configureSecurity
import plugins.configureSerialization

fun main() {
    embeddedServer(
        factory = Netty,
        environment = applicationEngineEnvironment {
            watchPaths = listOf("classes", "resources")
            developmentMode = true
            connector {
                port = 8080
                host = "0.0.0.0"
            }
            module(Application::module)
        },
    )
        .start(wait = true)
}

fun Application.module() {
    configureHTTP()
    configureSerialization()
    configureSecurity()
    configureRouting()
}
