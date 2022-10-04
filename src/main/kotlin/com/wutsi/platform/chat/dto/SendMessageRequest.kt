package com.wutsi.platform.chat.dto

import javax.validation.constraints.NotBlank
import kotlin.Long
import kotlin.String

public data class SendMessageRequest(
    @get:NotBlank
    public val conversationId: String = "",
    @get:NotBlank
    public val referenceId: String = "",
    public val recipientId: Long = 0,
    @get:NotBlank
    public val text: String = "",
    public val timestamp: Long = 0
)
