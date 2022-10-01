package com.wutsi.platform.chat.dto

import kotlin.Int
import kotlin.Long
import kotlin.String

public data class SearchMessageRequest(
    public val conversationId: String? = null,
    public val accountId1: Long? = null,
    public val accountId2: Long? = null,
    public val limit: Int = 50,
    public val offset: Int = 0
)
