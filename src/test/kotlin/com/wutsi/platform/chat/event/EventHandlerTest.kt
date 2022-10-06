package com.wutsi.platform.chat.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.platform.chat.delegate.SendMessageDelegate
import com.wutsi.platform.chat.dto.SendMessageRequest
import com.wutsi.platform.core.stream.Event
import com.wutsi.platform.rtm.event.MessageSentEventPayload
import com.wutsi.platform.rtm.model.ChatMessage
import com.wutsi.platform.rtm.model.ChatUser
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.util.UUID
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class EventHandlerTest {
    @MockBean
    private lateinit var delegate: SendMessageDelegate

    @Autowired
    private lateinit var handler: EventHandler

    @Test
    fun onEvent() {
        // GIVEN
        val senderId = 100L
        val recipientId = 101L
        val tenantId = 1L
        val payload = MessageSentEventPayload(
            serverId = UUID.randomUUID().toString(),
            sessionId = UUID.randomUUID().toString(),
            chatMessage = ChatMessage(
                id = UUID.randomUUID().toString(),
                roomId = "100,101",
                author = ChatUser(
                    id = senderId.toString()
                ),
                createdAt = 10000,
                text = "Hello world",
                metadata = mapOf(
                    "recipientId" to recipientId,
                    "tenantId" to tenantId
                )
            )
        )

        // WHEN
        val event = Event(
            type = com.wutsi.platform.rtm.event.EventURN.MESSAGE_SENT.urn,
            payload = ObjectMapper().writeValueAsString(payload)
        )
        handler.onEvent(event)

        // WHEN
        val request = argumentCaptor<SendMessageRequest>()
        verify(delegate).send(request.capture(), eq(senderId), eq(tenantId))

        assertEquals(payload.chatMessage?.roomId, request.firstValue.conversationId)
        assertEquals(payload.chatMessage?.id, request.firstValue.referenceId)
        assertEquals(recipientId, request.firstValue.recipientId)
        assertEquals(payload.chatMessage?.createdAt, request.firstValue.timestamp)
    }
}
