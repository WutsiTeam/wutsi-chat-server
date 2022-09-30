package com.wutsi.platform.chat.dto

import kotlin.String

public data class Conversation(
    public val id: String = "",
    public val lastMessage: Message = Message()
)
