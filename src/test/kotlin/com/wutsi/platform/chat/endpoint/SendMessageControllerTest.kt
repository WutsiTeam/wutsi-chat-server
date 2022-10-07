package com.wutsi.platform.chat.endpoint

import com.wutsi.platform.chat.dao.MessageRepository
import com.wutsi.platform.chat.dto.SendMessageRequest
import com.wutsi.platform.chat.dto.SendMessageResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.web.client.HttpClientErrorException
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SendMessageControllerTest : AbstractSecuredController() {
    @LocalServerPort
    val port: Int = 0

    @Autowired
    private lateinit var dao: MessageRepository

    private lateinit var url: String

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
            referenceId = UUID.randomUUID().toString(),
            conversationId = "1d2bfaf1c4bd7b5ea43f5c873d967c57"
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
        assertEquals(request.conversationId, msg.conversationId)
        assertNull(msg.received)
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
    }
}
