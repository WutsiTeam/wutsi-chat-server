package com.wutsi.platform.chat.endpoint

import com.wutsi.platform.chat.dto.SearchMessageRequest
import com.wutsi.platform.chat.dto.SearchMessageResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/SearchMessagesController.sql"])
public class SearchMessagesControllerTest : AbstractSecuredController() {
    @LocalServerPort
    public val port: Int = 0

    private lateinit var url: String

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/v1/messages/search"
    }

    @Test
    public fun `search by conversation`() {
        // GIVEN
        val request = SearchMessageRequest(
            conversationId = "100,101"
        )
        val response = rest.postForEntity(url, request, SearchMessageResponse::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        val messages = response.body!!.messages
        assertEquals(3, messages.size)

        assertEquals(102, messages[0].id)
        assertEquals(request.conversationId, messages[0].conversationId)
        assertEquals(100, messages[0].senderId)
        assertEquals(101, messages[0].recipientId)
        assertEquals("d2-d4", messages[0].text)
        assertEquals("iPhone100", messages[0].deviceId)
        assertEquals(1000, messages[0].timestamp)

        assertEquals(101, messages[1].id)
        assertEquals(request.conversationId, messages[1].conversationId)
        assertEquals(101, messages[1].senderId)
        assertEquals(100, messages[1].recipientId)
        assertEquals("e7-e5", messages[1].text)
        assertEquals("iPhone101", messages[1].deviceId)
        assertEquals(900, messages[1].timestamp)

        assertEquals(100, messages[2].id)
        assertEquals(request.conversationId, messages[2].conversationId)
        assertEquals(100, messages[2].senderId)
        assertEquals(101, messages[2].recipientId)
        assertEquals("e2-e4", messages[2].text)
        assertEquals("iPhone100", messages[2].deviceId)
        assertEquals(800, messages[2].timestamp)
    }

    @Test
    public fun `search by participant`() {
        // GIVEN
        val request = SearchMessageRequest(
            accountId1 = 101,
            accountId2 = 100
        )
        val response = rest.postForEntity(url, request, SearchMessageResponse::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        val messages = response.body!!.messages
        assertEquals(3, messages.size)

        assertEquals(102, messages[0].id)
        assertEquals("100,101", messages[0].conversationId)
        assertEquals(100, messages[0].senderId)
        assertEquals(101, messages[0].recipientId)
        assertEquals("d2-d4", messages[0].text)
        assertEquals("iPhone100", messages[0].deviceId)
        assertEquals(1000, messages[0].timestamp)

        assertEquals(101, messages[1].id)
        assertEquals("100,101", messages[1].conversationId)
        assertEquals(101, messages[1].senderId)
        assertEquals(100, messages[1].recipientId)
        assertEquals("e7-e5", messages[1].text)
        assertEquals("iPhone101", messages[1].deviceId)
        assertEquals(900, messages[1].timestamp)

        assertEquals(100, messages[2].id)
        assertEquals("100,101", messages[2].conversationId)
        assertEquals(100, messages[2].senderId)
        assertEquals(101, messages[2].recipientId)
        assertEquals("e2-e4", messages[2].text)
        assertEquals("iPhone100", messages[2].deviceId)
        assertEquals(800, messages[2].timestamp)
    }
}
