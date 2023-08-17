package jp.wataju.session

import java.util.*

data class AccountSession(
    val id: UUID?,
    val identifier: String?,
    val administrator: Boolean?
)
