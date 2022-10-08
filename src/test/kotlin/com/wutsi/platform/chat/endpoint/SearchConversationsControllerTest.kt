package com.wutsi.platform.chat.endpoint

import com.wutsi.platform.chat.dto.SearchConversationResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/SearchConversationsController.sql"])
public class SearchConversationsControllerTest : AbstractSecuredController() {
    @LocalServerPort
    public val port: Int = 0

    private lateinit var url: String

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/v1/conversations/search"
    }

    @Test
    public fun invoke() {
        // GIVEN
        val request = SearchConversationResponse()
        val response = rest.postForEntity(url, request, SearchConversationResponse::class.java)

        // THEN
        assertEquals(200, response.statusCodeValue)

        val conversations = response.body!!.conversations
        assertEquals(3, conversations.size)

        assertEquals("100,400", conversations[0].id)
        assertEquals(400L, conversations[0].lastMessage.id)

        assertEquals("100,200", conversations[1].id)
        assertEquals(200L, conversations[1].lastMessage.id)

        assertEquals("100,101", conversations[2].id)
        assertEquals(102, conversations[2].lastMessage.id)
    }
}
