package com.wutsi.platform.chat.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.core.stream.Event
import com.wutsi.platform.rtm.event.MessageReceivedEventPayload
import com.wutsi.platform.rtm.event.MessageSentEventPayload
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class EventHandler(
    private val objectMapper: ObjectMapper,
    private val delegate: MessageEventHandler
) {
    @EventListener
    fun onEvent(event: Event) {
        if (event.type == com.wutsi.platform.rtm.event.EventURN.MESSAGE_SENT.urn) {
            val payload = objectMapper.readValue(event.payload, MessageSentEventPayload::class.java)
            delegate.onMessageSent(payload)
        } else if (event.type == com.wutsi.platform.rtm.event.EventURN.MESSAGE_RECEIVED.urn) {
            val payload = objectMapper.readValue(event.payload, MessageReceivedEventPayload::class.java)
            delegate.onMessageReceived(payload)
        }
    }
}
