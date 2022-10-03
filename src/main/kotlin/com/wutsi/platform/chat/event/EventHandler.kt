package com.wutsi.platform.chat.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.chat.delegate.SendMessageDelegate
import com.wutsi.platform.chat.dto.SendMessageRequest
import com.wutsi.platform.core.stream.Event
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class EventHandler(
    private val delegate: SendMessageDelegate,
    private val mapper: ObjectMapper
) {
    @EventListener
    fun onEvent(event: Event) {
        if (event.type == EventURN.MESSAGE_SUBMITTED.urn) {
            val request = mapper.readValue(event.payload, SendMessageRequest::class.java)
            delegate.invoke(request)
        }
    }
}
