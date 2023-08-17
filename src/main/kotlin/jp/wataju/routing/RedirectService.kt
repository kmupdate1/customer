package jp.wataju.routing

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import jp.wataju.session.AccountSession

fun Application.redirectService() {

    routing {

        get("/") {
            call.respondRedirect("$WATAJU$CUSTOMER$AUTHENTICATION/login")
        }

        get("/logout") {
            call.sessions.set(AccountSession(null, null, null))
            call.respondRedirect("$WATAJU$CUSTOMER$AUTHENTICATION/login")
        }

    }

}
