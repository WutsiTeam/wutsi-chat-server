package com.wutsi.platform.chat.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.chat.dao.MessageRepository
import com.wutsi.platform.core.stream.Event
import com.wutsi.platform.rtm.event.MessageReceivedEventPayload
import com.wutsi.platform.rtm.event.MessageSentEventPayload
import com.wutsi.platform.rtm.model.ChatMessage
import com.wutsi.platform.rtm.model.ChatUser
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/EventHandler.sql"])
internal class EventHandlerTest {
    @Autowired
    private lateinit var handler: EventHandler

    @Autowired
    private lateinit var dao: MessageRepository

    @Test
    fun onMessageSent() {
        // GIVEN
        val senderId = 100L
        val recipientId = 101L
        val tenantId = 1L
        val deviceId = "xxxx"
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
                text = "Hellaylo world",
                metadata = mapOf(
                    "recipientId" to recipientId,
                    "tenantId" to tenantId,
                    "deviceId" to deviceId
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
        val referenceId = payload.chatMessage?.id
        val msg = dao.findByReferenceId(referenceId!!).get()

        assertEquals(payload.chatMessage?.metadata?.get("deviceId"), msg.deviceId)
        assertEquals(payload.chatMessage?.metadata?.get("tenantId"), msg.tenantId)
        assertEquals(payload.chatMessage?.metadata?.get("recipientId"), recipientId)
        assertEquals(recipientId, msg.recipientId)
        assertEquals(payload.chatMessage?.text, msg.text)
        assertEquals(payload.chatMessage?.createdAt, msg.timestamp)
        assertEquals(payload.chatMessage?.roomId, msg.conversationId)
        assertNull(msg.received)
    }

    @Test
    fun onMessageReceived() {
        // GIVEN
        val referenceId = "100"
        val payload = MessageReceivedEventPayload(
            serverId = UUID.randomUUID().toString(),
            sessionId = UUID.randomUUID().toString(),
            chatMessageId = referenceId
        )

        // WHEN
        val event = Event(
            type = com.wutsi.platform.rtm.event.EventURN.MESSAGE_RECEIVED.urn,
            payload = ObjectMapper().writeValueAsString(payload)
        )
        handler.onEvent(event)

        // WHEN
        val msg = dao.findByReferenceId(referenceId).get()
        assertNotNull(msg.received)
    }
}
