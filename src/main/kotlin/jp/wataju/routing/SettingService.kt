package jp.wataju.routing

import io.ktor.server.application.*
import io.ktor.server.mustache.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import jp.wataju.configuration.Connection
import jp.wataju.model.AccountService
import jp.wataju.model.ProductService
import jp.wataju.model.TAX_INCLUDE
import jp.wataju.session.AccountSession
import jp.wataju.util.*
import java.util.*
import kotlin.math.roundToInt

fun Application.settingService() {

    routing {

        route(WATAJU) {

            route(CUSTOMER) {

                route(SETTING) {

                    get("/index-administrator") {
                        val session = call.sessions.get() ?: AccountSession(null, null, null)

                        val accounts = AccountService(Connection.database).read()
                        val model = mapOf(
                            Strings.get(TAG, 0) to Strings.get(MESSAGE, 0),
                            Strings.get(TAG, 1) to Strings.get(MESSAGE, 9),
                            Strings.get(TAG, 2) to Strings.get(MESSAGE, 10),
                            Strings.get(TAG, 8) to session.identifier,
                            Strings.get(TAG, 7) to session.administrator
                        )
                        call.respond(MustacheContent("setting/index_administrator.hbs", model))
                    }

                    get("/account") {
                        val session = call.sessions.get() ?: AccountSession(null, null, null)

                        val accounts = AccountService(Connection.database).read()
                        val model = mapOf(
                            Strings.get(TAG, 0) to Strings.get(MESSAGE, 0),
                            Strings.get(TAG, 1) to Strings.get(MESSAGE, 9),
                            Strings.get(TAG, 2) to Strings.get(MESSAGE, 10),
                            Strings.get(TAG, 8) to session.identifier,
                            Strings.get(TAG, 7) to session.administrator,
                            Strings.get(TAG, 9) to accounts
                        )
                        call.respond(MustacheContent("setting/account.hbs", model))
                    }

                    get("/{id}") {
                        val session = call.sessions.get() ?: AccountSession(null, null, null)

                        val id = UUID.fromString(call.parameters["id"])

                        val account = AccountService(Connection.database).read(id)
                        val model = mapOf(
                            Strings.get(TAG, 0) to Strings.get(MESSAGE, 0),
                            Strings.get(TAG, 1) to Strings.get(MESSAGE, 9),
                            Strings.get(TAG, 2) to Strings.get(MESSAGE, 10),
                            Strings.get(TAG, 8) to session.identifier,
                            Strings.get(TAG, 7) to session.administrator,
                            Strings.get(TAG, 10) to account
                        )
                        call.respond(MustacheContent("setting/account_info.hbs", model))
                    }

                    get("/delete/{id}") {
                        val session = call.sessions.get() ?: AccountSession(null, null, null)

                        val id = UUID.fromString(call.parameters["id"])
                        /*
                        1.ログインアカウントの場合削除できない
                        2.ログインアカウントでない場合削除できる
                         */
                        if (id == session.id) {
                            val account = AccountService(Connection.database).read(id)
                            val model = mapOf(
                                Strings.get(TAG, 0) to Strings.get(MESSAGE, 0),
                                Strings.get(TAG, 1) to Strings.get(MESSAGE, 9),
                                Strings.get(TAG, 2) to Strings.get(MESSAGE, 12),
                                Strings.get(TAG, 8) to session.identifier,
                                Strings.get(TAG, 7) to session.administrator,
                                Strings.get(TAG, 10) to account,
                                Strings.get(TAG, 3) to Strings.get(MESSAGE, 11),
                                Strings.get(TAG, 4) to true
                            )
                            call.respond(MustacheContent("setting/account_info.hbs", model))

                        } else {
                            AccountService(Connection.database).delete(id)
                            call.respondRedirect("$WATAJU$CUSTOMER$SETTING/account")
                        }
                    }

                    get("/product") {
                        val session = call.sessions.get() ?: AccountSession(null, null, null)

                        val products = ProductService(Connection.database).read()
                        /*
                        1.商品が一つも登録されていない場合
                        2.商品が登録されている場合
                         */
                        if (products.isEmpty()) {
                            val model = mapOf(
                                Strings.get(TAG, 0) to Strings.get(MESSAGE, 0),
                                Strings.get(TAG, 1) to Strings.get(MESSAGE, 9),
                                Strings.get(TAG, 2) to Strings.get(MESSAGE, 15),
                                Strings.get(TAG, 8) to session.identifier,
                                Strings.get(TAG, 7) to session.administrator,
                                Strings.get(TAG, 3) to Strings.get(MESSAGE, 13),
                                Strings.get(TAG, 4) to true
                            )
                            call.respond(MustacheContent("setting/product.hbs", model))
                        } else {
                            val model = mapOf(
                                Strings.get(TAG, 0) to Strings.get(MESSAGE, 0),
                                Strings.get(TAG, 1) to Strings.get(MESSAGE, 9),
                                Strings.get(TAG, 2) to Strings.get(MESSAGE, 15),
                                Strings.get(TAG, 8) to session.identifier,
                                Strings.get(TAG, 7) to session.administrator,
                                Strings.get(TAG, 16) to products,
                                Strings.get(TAG, 4) to false
                            )
                            call.respond(MustacheContent("setting/product.hbs", model))
                        }

                    }

                    get("/product/edit") {
                        val session = call.sessions.get() ?: AccountSession(null, null, null)

                        val model = mapOf(
                            Strings.get(TAG, 0) to Strings.get(MESSAGE, 0),
                            Strings.get(TAG, 1) to Strings.get(MESSAGE, 9),
                            Strings.get(TAG, 2) to Strings.get(MESSAGE, 14),
                            Strings.get(TAG, 8) to session.identifier,
                            Strings.get(TAG, 7) to session.administrator,
                            Strings.get(TAG, 4) to false
                        )
                        call.respond(MustacheContent("setting/product_edit.hbs", model))

                    }

                    post("/product/confirm") {
                        val session = call.sessions.get() ?: AccountSession(null, null, null)

                        val params = call.receiveParameters()
                        val productName = params["product_name"] ?: ""
                        val productNameKana = params["product_name_kana"] ?: ""
                        val productPrice = params["price"] ?: ""
                        val productEnabled = params["enabled"] ?: ""

                        /*
                        1.一つでも空欄がある場合
                        2.全部入力された場合
                         */
                        if ((productName == "") or (productNameKana == "") or (productPrice == "")) {
                            val model = mapOf(
                                Strings.get(TAG, 0) to Strings.get(MESSAGE, 0),
                                Strings.get(TAG, 1) to Strings.get(MESSAGE, 9),
                                Strings.get(TAG, 2) to Strings.get(MESSAGE, 14),
                                Strings.get(TAG, 8) to session.identifier,
                                Strings.get(TAG, 7) to session.administrator,
                                Strings.get(TAG, 3) to Strings.get(MESSAGE, 16),
                                Strings.get(TAG, 4) to true
                            )
                            call.respond(MustacheContent("setting/product_edit.hbs", model))

                        } else {
                            var enabled = false
                            if (productEnabled == "on") enabled = true

                            RegisterPool.product = Product(
                                productName,
                                productNameKana,
                                productPrice.toInt(),
                                enabled
                            )

                            val model = mapOf(
                                Strings.get(TAG, 0) to Strings.get(MESSAGE, 0),
                                Strings.get(TAG, 1) to Strings.get(MESSAGE, 9),
                                Strings.get(TAG, 2) to Strings.get(MESSAGE, 14),
                                Strings.get(TAG, 8) to session.identifier,
                                Strings.get(TAG, 7) to session.administrator,
                                Strings.get(TAG, 11) to productName,
                                Strings.get(TAG, 12) to productNameKana,
                                Strings.get(TAG, 13) to productPrice,
                                Strings.get(TAG, 15) to enabled,
                                Strings.get(TAG, 4) to false
                            )
                            call.respond(MustacheContent("setting/product_confirm.hbs", model))
                        }

                    }

                    get("/product/register") {
                        val session = call.sessions.get() ?: AccountSession(null, null, null)

                        val id = UUID.randomUUID()
                        val date = Date()
                        ProductService(Connection.database).create(
                            jp.wataju.model.Product(
                                id,
                                RegisterPool.product.productName,
                                RegisterPool.product.productNameKana,
                                RegisterPool.product.price,
                                RegisterPool.product.enabled,
                                date.toString(),
                                date.toString(),
                                session.id!!,
                                session.id
                            )
                        )

                        call.respondRedirect("$WATAJU$CUSTOMER$SETTING/product")
                    }

                }

            }

        }

    }

}
