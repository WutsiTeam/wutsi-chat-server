package com.wutsi.platform.chat.event

data class MessageEventPayload(
    val messageId: Long = -1,
    val conversationId: String = ""
)
