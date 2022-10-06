package com.wutsi.platform.chat.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.platform.account.dto.GetAccountResponse
import com.wutsi.platform.chat.endpoint.AbstractSecuredController
import com.wutsi.platform.chat.entity.MessageEntity
import com.wutsi.platform.core.messaging.Message
import com.wutsi.platform.core.messaging.MessagingService
import com.wutsi.platform.core.messaging.MessagingServiceProvider
import com.wutsi.platform.core.messaging.MessagingType
import com.wutsi.platform.rtm.event.EventURN
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.util.UUID
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class NotificationServiceTest : AbstractSecuredController() {
    @Autowired
    private lateinit var service: NotificationService

    @MockBean
    private lateinit var messagingServiceProvider: MessagingServiceProvider

    private lateinit var messaging: MessagingService

    private val sender = createAccount()

    @BeforeEach
    override fun setUp() {
        super.setUp()

        doReturn(GetAccountResponse(sender)).whenever(accountApi).getAccount(sender.id)

        messaging = mock()
        doReturn(messaging).whenever(messagingServiceProvider).get(MessagingType.PUSH_NOTIFICATION)
    }

    @Test
    fun onSend() {
        val recipient = createAccount(111, "Roger Milla", "123")
        doReturn(GetAccountResponse(recipient)).whenever(accountApi).getAccount(recipient.id)

        val message = MessageEntity(
            senderId = sender.id,
            recipientId = recipient.id,
            text = "Hello world",
            referenceId = UUID.randomUUID().toString(),
            id = 1111
        )
        service.onMessageSent(message, tenant, EventURN.MESSAGE_SENT)

        val msg = argumentCaptor<Message>()
        verify(messaging).send(msg.capture())

        val url = "${tenant.webappUrl}/messages?recipient-id=${sender.id}"
        assertEquals(recipient.fcmToken, msg.firstValue.recipient.deviceToken)
        assertEquals("${sender.displayName}: ${message.text}", msg.firstValue.body)
        assertEquals(EventURN.MESSAGE_SENT.urn, msg.firstValue.data["eventUrn"])
        assertEquals(message.referenceId, msg.firstValue.data["referenceId"])
        assertEquals(url, msg.firstValue.url)
        assertEquals(url, msg.firstValue.data["url"])
    }

    @Test
    fun onSendWithNoFCM() {
        val recipient = createAccount(111, "Roger Milla", null)
        doReturn(GetAccountResponse(recipient)).whenever(accountApi).getAccount(recipient.id)

        val message = MessageEntity(
            senderId = sender.id,
            recipientId = recipient.id,
            text = "Hello world",
            referenceId = UUID.randomUUID().toString(),
            id = 1111
        )
        service.onMessageSent(message, tenant, EventURN.MESSAGE_SENT)

        verify(messaging, never()).send(any())
    }
}
