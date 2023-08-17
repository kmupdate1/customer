package jp.wataju.routing

import io.ktor.server.application.*
import io.ktor.server.mustache.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import jp.wataju.session.AccountSession
import jp.wataju.util.MESSAGE
import jp.wataju.util.Strings
import jp.wataju.util.TAG

fun Application.indexService() {

    routing {

        route(WATAJU) {

            route(CUSTOMER) {

                get("/index") {
                    val session = call.sessions.get() ?: AccountSession(null, null, null)
                    val model = mapOf(
                        Strings.get(TAG, 0) to Strings.get(MESSAGE, 0),
                        Strings.get(TAG, 1) to Strings.get(MESSAGE, 9),
                        Strings.get(TAG, 2) to Strings.get(MESSAGE, 10),
                        Strings.get(TAG, 8) to session.identifier,
                        Strings.get(TAG, 7) to session.administrator,
                    )
                    call.respond(MustacheContent("index/index.hbs", model))
                }

            }

        }

    }

}
