package com.wutsi.platform.chat.`delegate`

import com.wutsi.platform.chat.dao.MessageRepository
import com.wutsi.platform.chat.dto.SendMessageRequest
import com.wutsi.platform.chat.dto.SendMessageResponse
import com.wutsi.platform.chat.entity.MessageEntity
import com.wutsi.platform.chat.event.EventURN
import com.wutsi.platform.chat.event.MessageEventPayload
import com.wutsi.platform.chat.service.NotificationService
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.platform.core.tracing.TracingContext
import com.wutsi.platform.tenant.WutsiTenantApi
import com.wutsi.platform.tenant.dto.Tenant
import org.apache.commons.codec.digest.DigestUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
public class SendMessageDelegate(
    private val securityManager: com.wutsi.platform.chat.service.SecurityManager,
    private val dao: MessageRepository,
    private val tracingContext: TracingContext,
    private val logger: KVLogger,
    private val eventStream: EventStream,
    private val tenantApi: WutsiTenantApi,
    private val notificationService: NotificationService
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(SendMessageRequest::class.java)
    }

    public fun invoke(request: SendMessageRequest): SendMessageResponse {
        // Store
        val tenantId = tracingContext.tenantId()!!.toLong()
        val msg = dao.save(
            MessageEntity(
                deviceId = tracingContext.deviceId(),
                tenantId = tenantId,
                senderId = securityManager.currentUserId() ?: -1,
                recipientId = request.recipientId,
                created = OffsetDateTime.now(),
                conversationId = generateConversationId(request),
                text = request.text,
                timestamp = request.timestamp,
                referenceId = request.referenceId
            )
        )

        // Notify recipient
        val tenant = tenantApi.getTenant(tenantId).tenant
        notify(msg, tenant)

        // Publish
        publish(msg)

        logger.add("message_id", msg.id)
        return SendMessageResponse(
            id = msg.id!!
        )
    }

    private fun generateConversationId(request: SendMessageRequest): String =
        DigestUtils.md5Hex(
            listOfNotNull(request.recipientId, securityManager.currentUserId())
                .sorted()
                .joinToString(",")
        )

    private fun notify(msg: MessageEntity, tenant: Tenant) {
        try {
            notificationService.onMessageSent(msg, tenant, EventURN.MESSAGE_SENT)
        } catch (ex: Exception) {
            LOGGER.warn("Unable to send notification", ex)
        }
    }

    private fun publish(message: MessageEntity) {
        val payload = MessageEventPayload(message.id ?: -1, message.conversationId)
        eventStream.publish(EventURN.MESSAGE_SENT.urn, payload)
    }
}
