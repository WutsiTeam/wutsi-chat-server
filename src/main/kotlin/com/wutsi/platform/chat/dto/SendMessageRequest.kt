package com.wutsi.platform.chat.dto

import javax.validation.constraints.NotBlank
import kotlin.Long
import kotlin.String

public data class SendMessageRequest(
    public val recipientId: Long = 0,
    @get:NotBlank
    public val text: String = ""
)
