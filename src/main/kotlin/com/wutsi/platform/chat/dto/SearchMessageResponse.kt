package com.wutsi.platform.chat.dto

import kotlin.collections.List

public data class SearchMessageResponse(
    public val messages: List<Message> = emptyList()
)
