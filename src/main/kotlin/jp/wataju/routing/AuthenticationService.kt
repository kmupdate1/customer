package jp.wataju.routing

import io.ktor.server.application.*
import io.ktor.server.mustache.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import jp.wataju.configuration.Connection
import jp.wataju.model.Account
import jp.wataju.model.AccountService
import jp.wataju.session.AccountSession
import jp.wataju.util.MESSAGE
import jp.wataju.util.Strings
import jp.wataju.util.TAG
import jp.wataju.util.RegisterPool
import jp.wataju.util.Signing
import java.util.Date
import java.util.UUID

fun Application.authenticationService() {

    routing {

        route(WATAJU) {

            route(CUSTOMER) {

                route(AUTHENTICATION) {

                    get("/login") {
                        val model = mapOf(
                            Strings.get(TAG, 0) to Strings.get(MESSAGE, 0),
                            Strings.get(TAG, 1) to Strings.get(MESSAGE, 1),
                            Strings.get(TAG, 2) to Strings.get(MESSAGE, 2),
                            Strings.get(TAG, 4) to false
                        )
                        call.respond(MustacheContent("authentication/login.hbs", model))
                    }

                    post("/login") {
                        val params = call.receiveParameters()
                        val paramsIdentifier = params["identifier"] ?: ""
                        val paramsPassword = params["password"] ?: ""

                        /*
                        1.IDまたはパスワードが未入力
                        2.IDまたはパスワードが間違っている(存在しない)
                        3.ID・パスワードが合致している
                        */
                        if ((paramsIdentifier == "") or (paramsPassword == "")) {
                            val model = mapOf(
                                Strings.get(TAG, 0) to Strings.get(MESSAGE, 0),
                                Strings.get(TAG, 1) to Strings.get(MESSAGE, 1),
                                Strings.get(TAG, 2) to Strings.get(MESSAGE, 2),
                                Strings.get(TAG, 3) to Strings.get(MESSAGE, 6),
                                Strings.get(TAG, 4) to true
                            )
                            call.respond(MustacheContent("authentication/login.hbs", model))
                        } else {
                            val account = AccountService(Connection.database).read(paramsIdentifier)
                            if ((account?.identifier == null) or ((account?.identifier != paramsIdentifier) or (account?.password != paramsPassword))) {
                                val model = mapOf(
                                    Strings.get(TAG, 0) to Strings.get(MESSAGE, 0),
                                    Strings.get(TAG, 1) to Strings.get(MESSAGE, 1),
                                    Strings.get(TAG, 2) to Strings.get(MESSAGE, 2),
                                    Strings.get(TAG, 3) to Strings.get(MESSAGE, 5),
                                    Strings.get(TAG, 4) to true
                                )
                                call.respond(MustacheContent("authentication/login.hbs", model))
                            } else {
                                call.sessions.set(
                                    AccountSession(account!!.id, account.identifier, account.administrator)
                                )
                                call.respondRedirect("$WATAJU$CUSTOMER/index")
                            }
                        }

                    }

                    get("/signing") {
                        val model = mapOf(
                            Strings.get(TAG, 0) to Strings.get(MESSAGE, 0),
                            Strings.get(TAG, 1) to Strings.get(MESSAGE, 3),
                            Strings.get(TAG, 2) to Strings.get(MESSAGE, 2)
                        )
                        call.respond(MustacheContent("authentication/signing.hbs", model))
                    }

                    post("/signing") {
                        val params = call.receiveParameters()
                        val identifier = params["identifier"] ?: ""
                        val password = params["password"] ?: ""
                        val rePassword = params["re_password"] ?: ""
                        val administrator = params["administrator"] ?: ""

                        /*
                        1.未入力
                        2.パスワード不一致
                        3.既存アカウント
                        4.登録
                        */
                        if ((identifier == "") or (password == "") or (rePassword == "")) {
                            val model = mapOf(
                                Strings.get(TAG, 0) to Strings.get(MESSAGE, 0),
                                Strings.get(TAG, 1) to Strings.get(MESSAGE, 3),
                                Strings.get(TAG, 2) to Strings.get(MESSAGE, 2),
                                Strings.get(TAG, 3) to Strings.get(MESSAGE, 6),
                                Strings.get(TAG, 4) to true
                            )
                            call.respond(MustacheContent("authentication/signing.hbs", model))

                        } else if (password != rePassword) {
                            val model = mapOf(
                                Strings.get(TAG, 0) to Strings.get(MESSAGE, 0),
                                Strings.get(TAG, 1) to Strings.get(MESSAGE, 3),
                                Strings.get(TAG, 2) to Strings.get(MESSAGE, 2),
                                Strings.get(TAG, 3) to Strings.get(MESSAGE, 7),
                                Strings.get(TAG, 4) to true
                            )
                            call.respond(MustacheContent("authentication/signing.hbs", model))

                        } else {
                            val account = AccountService(Connection.database).read(identifier)
                            if (account?.identifier != null) {
                                val model = mapOf(
                                    Strings.get(TAG, 0) to Strings.get(MESSAGE, 0),
                                    Strings.get(TAG, 1) to Strings.get(MESSAGE, 3),
                                    Strings.get(TAG, 2) to Strings.get(MESSAGE, 2),
                                    Strings.get(TAG, 3) to Strings.get(MESSAGE, 4),
                                    Strings.get(TAG, 4) to true
                                )
                                call.respond(MustacheContent("authentication/signing.hbs", model))

                            } else {
                                var isAdmin = false
                                if (administrator == "on") isAdmin = true
                                RegisterPool.signing = Signing(
                                    identifier,
                                    password,
                                    isAdmin
                                )

                                val model = mapOf(
                                    Strings.get(TAG, 0) to Strings.get(MESSAGE, 0),
                                    Strings.get(TAG, 1) to Strings.get(MESSAGE, 3),
                                    Strings.get(TAG, 2) to Strings.get(MESSAGE, 8),
                                    Strings.get(TAG, 5) to identifier,
                                    Strings.get(TAG, 6) to password,
                                )
                                call.respond(MustacheContent("authentication/confirm.hbs", model))
                            }
                        }
                    }

                    get("/confirm") {
                        val id = UUID.randomUUID()
                        val date = Date()
                        AccountService(Connection.database).create(
                            Account(
                                id,
                                RegisterPool.signing.identifier,
                                RegisterPool.signing.password,
                                RegisterPool.signing.administrator,
                                date.toString(),
                                date.toString(),
                                id,
                                id
                            )
                        )
                        call.respondRedirect("$WATAJU$CUSTOMER$AUTHENTICATION/login")
                    }

                }

            }

        }

    }

}
