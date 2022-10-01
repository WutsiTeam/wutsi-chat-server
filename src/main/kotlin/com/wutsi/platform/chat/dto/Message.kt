package com.wutsi.platform.chat.dto

import org.springframework.format.`annotation`.DateTimeFormat
import java.time.OffsetDateTime
import kotlin.Long
import kotlin.String

public data class Message(
    public val id: Long = 0,
    public val referenceId: String = "",
    public val conversationId: String = "",
    public val senderId: Long = 0,
    public val recipientId: Long = 0,
    public val text: String = "",
    public val timestamp: Long = 0,
    @get:DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    public val created: OffsetDateTime = OffsetDateTime.now(),
    public val deviceId: String? = null
)
