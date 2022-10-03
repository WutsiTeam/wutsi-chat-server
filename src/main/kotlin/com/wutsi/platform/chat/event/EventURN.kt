package com.wutsi.platform.chat.event

enum class EventURN(val urn: String) {
    MESSAGE_SENT("urn:wutsi:event:chat:message-sent"),
    MESSAGE_SUBMITTED("urn:wutsi:event:chat:message-submitted")
}
