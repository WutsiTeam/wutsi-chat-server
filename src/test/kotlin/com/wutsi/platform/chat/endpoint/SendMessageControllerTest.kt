package com.wutsi.platform.chat.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.platform.chat.dao.MessageRepository
import com.wutsi.platform.chat.dto.SendMessageRequest
import com.wutsi.platform.chat.dto.SendMessageResponse
import com.wutsi.platform.chat.entity.MessageEntity
import com.wutsi.platform.chat.event.EventURN
import com.wutsi.platform.chat.event.MessageEventPayload
import com.wutsi.platform.chat.service.NotificationService
import com.wutsi.platform.core.stream.EventStream
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.web.client.HttpClientErrorException
import java.util.UUID
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SendMessageControllerTest : AbstractSecuredController() {
    @LocalServerPort
    val port: Int = 0

    @Autowired
    private lateinit var dao: MessageRepository

    private lateinit var url: String

    @MockBean
    private lateinit var eventStream: EventStream

    @MockBean
    private lateinit var notificationService: NotificationService

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/v1/messages"
    }

    @Test
    fun invoke() {
        // WHEN
        val request = SendMessageRequest(
            recipientId = 555,
            text = "Hello world",
            timestamp = 32932090,
            referenceId = UUID.randomUUID().toString()
        )
        val response = rest.postForEntity(url, request, SendMessageResponse::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        val id = response.body!!.id
        val msg = dao.findById(id).get()
        assertEquals(DEVICE_ID, msg.deviceId)
        assertEquals(USER_ID, msg.senderId)
        assertEquals(request.recipientId, msg.recipientId)
        assertEquals(request.text, msg.text)
        assertEquals(request.timestamp, msg.timestamp)
        assertEquals(request.recipientId, msg.recipientId)
        assertEquals("1d2bfaf1c4bd7b5ea43f5c873d967c57", msg.conversationId)

        val payload = MessageEventPayload(
            messageId = msg.id ?: -1,
            conversationId = msg.conversationId
        )
        verify(eventStream).publish(EventURN.MESSAGE_SENT.urn, payload)

        val message = argumentCaptor<MessageEntity>()
        verify(notificationService).onMessageSent(message.capture(), any(), eq(EventURN.MESSAGE_SENT))
        assertEquals(id, message.firstValue.id)
    }

    @Test
    fun emptyMessage() {
        // WHEN
        val request = SendMessageRequest()
        val ex = assertThrows<HttpClientErrorException> {
            rest.postForEntity(url, request, SendMessageResponse::class.java)
        }

        // THEN
        assertEquals(400, ex.rawStatusCode)
        verify(eventStream, never()).publish(any(), any())

        verify(notificationService, never()).onMessageSent(any(), any(), any())
    }
}
