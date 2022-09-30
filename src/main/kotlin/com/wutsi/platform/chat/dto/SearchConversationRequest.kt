package com.wutsi.platform.chat.dto

import kotlin.Int
import kotlin.Long

public data class SearchConversationRequest(
    public val accountId: Long? = null,
    public val limit: Int = 50,
    public val offset: Int = 0
)
