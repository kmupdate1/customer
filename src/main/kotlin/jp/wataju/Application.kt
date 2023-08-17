package jp.wataju

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import jp.wataju.configuration.configuration
import jp.wataju.routing.authenticationService
import jp.wataju.routing.indexService
import jp.wataju.routing.redirectService
import jp.wataju.routing.settingService

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {

    configuration()

    redirectService()
    authenticationService()
    indexService()
    settingService()
}
