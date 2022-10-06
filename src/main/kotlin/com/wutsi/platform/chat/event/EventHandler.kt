package com.wutsi.platform.chat.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.chat.delegate.SendMessageDelegate
import com.wutsi.platform.chat.dto.SendMessageRequest
import com.wutsi.platform.core.stream.Event
import com.wutsi.platform.rtm.event.MessageSentEventPayload
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class EventHandler(
    private val objectMapper: ObjectMapper,
    private val delegate: SendMessageDelegate
) {
    @EventListener
    fun onEvent(event: Event) {
        if (event.type == com.wutsi.platform.rtm.event.EventURN.MESSAGE_SENT.urn) {
            val payload = objectMapper.readValue(event.payload, MessageSentEventPayload::class.java)
            payload.chatMessage ?: return

            delegate.send(
                request = SendMessageRequest(
                    conversationId = payload.chatMessage?.roomId ?: "",
                    referenceId = payload.chatMessage?.id ?: "",
                    text = payload.chatMessage?.text ?: "",
                    timestamp = payload.chatMessage?.createdAt ?: -1,
                    recipientId = payload.chatMessage?.metadata?.get("recipientId")?.toString()?.toLong() ?: -1
                ),
                senderId = payload.chatMessage?.author?.id?.toLong() ?: -1,
                tenantId = payload.chatMessage?.metadata?.get("tenantId")?.toString()?.toLong() ?: -1,
                deviceId = payload.chatMessage?.metadata?.get("deviceId")?.toString()
            )
        }
    }
}
