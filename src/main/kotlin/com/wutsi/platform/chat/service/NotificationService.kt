package com.wutsi.platform.chat.service

import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.chat.entity.MessageEntity
import com.wutsi.platform.chat.event.EventURN
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.messaging.Message
import com.wutsi.platform.core.messaging.MessagingServiceProvider
import com.wutsi.platform.core.messaging.MessagingType
import com.wutsi.platform.core.messaging.Party
import com.wutsi.platform.tenant.dto.Tenant
import org.springframework.stereotype.Service

@Service
class NotificationService(
    private val logger: KVLogger,
    private val accountApi: WutsiAccountApi,
    private val messagingServiceProvider: MessagingServiceProvider
) {
    fun onMessageSent(message: MessageEntity, tenant: Tenant, event: EventURN) {
        val recipient = accountApi.getAccount(message.recipientId).account
        if (recipient.fcmToken == null) {
            return
        }
        val sender = accountApi.getAccount(message.senderId).account

        val gateway = messagingServiceProvider.get(MessagingType.PUSH_NOTIFICATION)
        val url = "${tenant.webappUrl}/messages?recipient-id=${message.senderId}"
        val messageId = gateway.send(
            Message(
                recipient = Party(
                    displayName = recipient.displayName,
                    deviceToken = recipient.fcmToken
                ),
                mimeType = "text/plain",
                body = "${sender.displayName}: ${message.text}",
                url = url,
                data = mapOf(
                    "eventUrn" to event.urn,
                    "messageId" to (message.id?.toString() ?: ""),
                    "referenceId" to message.referenceId,
                    "url" to url
                )
            )
        )
        logger.add("fcm_message_id", messageId)
    }
}
