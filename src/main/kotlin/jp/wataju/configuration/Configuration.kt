package jp.wataju.configuration

import com.github.mustachejava.DefaultMustacheFactory
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.mustache.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.sessions.*
import jp.wataju.session.AccountSession
import jp.wataju.util.DATABASE
import jp.wataju.util.Strings
import org.jetbrains.exposed.sql.Database

fun Application.configuration() {

    install(Sessions) {
        cookie<AccountSession>("account_session") {
            cookie.extensions["SameSite"] = "lax"
        }
    }
    install(ContentNegotiation) {
        json()
    }
    install(Mustache) {
        mustacheFactory = DefaultMustacheFactory("templates/mustache")
    }

}

object Connection {
    val database = Database.connect(
        url = Strings.get(DATABASE,0),
        user = Strings.get(DATABASE, 1),
        driver = Strings.get(DATABASE, 2),
        password = Strings.get(DATABASE, 3)
    )
}
