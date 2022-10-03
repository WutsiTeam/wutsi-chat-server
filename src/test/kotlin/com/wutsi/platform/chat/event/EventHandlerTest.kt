package com.wutsi.platform.chat.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.platform.chat.delegate.SendMessageDelegate
import com.wutsi.platform.chat.dto.SendMessageRequest
import com.wutsi.platform.core.stream.Event
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.util.UUID

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class EventHandlerTest {
    @MockBean
    private lateinit var delegate: SendMessageDelegate

    @Autowired
    private lateinit var handler: EventHandler

    @Autowired
    private lateinit var mapper: ObjectMapper

    @Test
    fun onEvent() {
        val request = SendMessageRequest(
            recipientId = 555,
            text = "Hello world",
            timestamp = 32932090,
            referenceId = UUID.randomUUID().toString()
        )
        val event = Event(
            type = EventURN.MESSAGE_SUBMITTED.urn,
            payload = mapper.writeValueAsString(request)
        )
        handler.onEvent(event)

        verify(delegate).invoke(request)
    }
}
