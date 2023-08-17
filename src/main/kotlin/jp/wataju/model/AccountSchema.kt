package jp.wataju.model

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.*
import kotlin.collections.ArrayList

data class Account(
    val id: UUID,
    val identifier: String,
    val password: String,
    val administrator: Boolean,
    val createDate: String,
    val updateDate: String,
    val createAccount: UUID,
    val updateAccount: UUID
)

class AccountService(
    database: Database
): Schema(database, Accounts) {

    object Accounts: Table("accounts") {
        val id = uuid("id")
        val identifier = varchar("identifier", length = 50)
        val password = varchar("password", length = 100)
        val administrator = bool("administrator")
        val createDate = varchar("create_date", length = 50)
        val updateDate = varchar("update_date", length = 50)
        val createAccount = uuid("create_account") references id
        val updateAccount = uuid("update_account") references id

        override val primaryKey = PrimaryKey(id)
    }

    suspend fun create(account: Account): UUID = dbQuery {
        Accounts.insert {
            it[id] = account.id
            it[identifier] = account.identifier
            it[password] = account.password
            it[administrator] = account.administrator
            it[createDate] = account.createDate
            it[updateDate] = account.updateDate
            it[createAccount] = account.createAccount
            it[updateAccount] = account.updateAccount
        }[Accounts.id]
    }

    suspend fun read(): MutableList<Account?> {
        val accounts = mutableListOf<Account?>()
        dbQuery {
            Accounts.selectAll().orderBy(Accounts.identifier)
                .forEach {
                    accounts.add(
                        Account(
                            it[Accounts.id],
                            it[Accounts.identifier],
                            it[Accounts.password],
                            it[Accounts.administrator],
                            it[Accounts.createDate],
                            it[Accounts.updateDate],
                            it[Accounts.createAccount],
                            it[Accounts.updateAccount]
                        )
                    )
                }
        }
        return accounts
    }

    suspend fun read(id: UUID): Account? {
        return dbQuery {
            Accounts.select { Accounts.id eq id }
                .map {
                    Account(
                        it[Accounts.id],
                        it[Accounts.identifier],
                        it[Accounts.password],
                        it[Accounts.administrator],
                        it[Accounts.createDate],
                        it[Accounts.updateDate],
                        it[Accounts.updateAccount],
                        it[Accounts.createAccount]
                    )
                }
                .singleOrNull()
        }
    }

    suspend fun read(identifier: String): Account? {
        return dbQuery {
            Accounts.select { Accounts.identifier eq identifier }
                .map {
                    Account(
                        it[Accounts.id],
                        it[Accounts.identifier],
                        it[Accounts.password],
                        it[Accounts.administrator],
                        it[Accounts.createDate],
                        it[Accounts.updateDate],
                        it[Accounts.updateAccount],
                        it[Accounts.createAccount]
                    )
                }
                .singleOrNull()
        }
    }

    suspend fun update(id: UUID, account: Account) {
        dbQuery {
            Accounts.update({ Accounts.id eq id }) {
                it[identifier] = account.identifier
                it[password] = account.password
                it[administrator] = account.administrator
                it[updateDate] = account.updateDate
                it[updateAccount] = account.updateAccount
            }
        }
    }

    suspend fun delete(id: UUID) {
        dbQuery {
            Accounts.deleteWhere { Accounts.id eq id }
        }
    }

}