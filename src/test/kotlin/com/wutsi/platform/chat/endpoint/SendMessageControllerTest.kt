package com.wutsi.platform.chat.endpoint

import com.nhaarman.mockitokotlin2.verify
import com.wutsi.platform.chat.dao.MessageRepository
import com.wutsi.platform.chat.dto.SendMessageRequest
import com.wutsi.platform.chat.dto.SendMessageResponse
import com.wutsi.platform.chat.event.EventURN
import com.wutsi.platform.chat.event.MessageEventPayload
import com.wutsi.platform.core.stream.EventStream
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SendMessageControllerTest : AbstractSecuredController() {
    @LocalServerPort
    public val port: Int = 0

    @Autowired
    private lateinit var dao: MessageRepository

    private lateinit var url: String

    @MockBean
    private lateinit var eventStream: EventStream

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/v1/messages"
    }

    @Test
    public fun invoke() {
        // WHEN
        val request = SendMessageRequest(
            recipientId = 555,
            text = "Hello world",
            timestamp = 32932090
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
        assertEquals("1d2bfaf1c4bd7b5ea43f5c873d967c57", msg.conversationId)

        val payload = MessageEventPayload(
            messageId = msg.id ?: -1,
            conversationId = msg.conversationId
        )
        verify(eventStream).enqueue(EventURN.MESSAGE_SENT.urn, payload)
        verify(eventStream).publish(EventURN.MESSAGE_SENT.urn, payload)
    }
}
