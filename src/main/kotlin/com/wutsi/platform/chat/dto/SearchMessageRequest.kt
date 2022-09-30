package com.wutsi.platform.chat.dto

import kotlin.Int
import kotlin.String

public data class SearchMessageRequest(
    public val conversationId: String? = null,
    public val limit: Int = 50,
    public val offset: Int = 0
)
