package com.wutsi.platform.chat.dto

import kotlin.collections.List

public data class SearchConversationResponse(
    public val conversations: List<Conversation> = emptyList()
)
