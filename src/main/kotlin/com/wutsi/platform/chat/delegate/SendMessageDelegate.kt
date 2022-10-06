package com.wutsi.platform.chat.`delegate`

import com.wutsi.platform.chat.dao.MessageRepository
import com.wutsi.platform.chat.dto.SendMessageRequest
import com.wutsi.platform.chat.dto.SendMessageResponse
import com.wutsi.platform.chat.entity.MessageEntity
import com.wutsi.platform.chat.service.NotificationService
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.tracing.TracingContext
import com.wutsi.platform.rtm.event.EventURN
import com.wutsi.platform.tenant.WutsiTenantApi
import com.wutsi.platform.tenant.dto.Tenant
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
public class SendMessageDelegate(
    private val securityManager: com.wutsi.platform.chat.service.SecurityManager,
    private val dao: MessageRepository,
    private val tracingContext: TracingContext,
    private val logger: KVLogger,
    private val tenantApi: WutsiTenantApi,
    private val notificationService: NotificationService
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(SendMessageRequest::class.java)
    }

    fun invoke(request: SendMessageRequest): SendMessageResponse =
        send(request, securityManager.currentUserId() ?: -1)

    fun send(request: SendMessageRequest, senderId: Long): SendMessageResponse {
        // Store
        val tenantId = tracingContext.tenantId()!!.toLong()
        val msg = dao.save(
            MessageEntity(
                deviceId = tracingContext.deviceId(),
                tenantId = tenantId,
                senderId = senderId,
                recipientId = request.recipientId,
                created = OffsetDateTime.now(),
                conversationId = request.conversationId,
                text = request.text,
                timestamp = request.timestamp,
                referenceId = request.referenceId
            )
        )

        // Notify recipient
        val tenant = tenantApi.getTenant(tenantId).tenant
        notify(msg, tenant)

        logger.add("message_id", msg.id)
        return SendMessageResponse(
            id = msg.id!!
        )
    }

    private fun notify(msg: MessageEntity, tenant: Tenant) {
        try {
            notificationService.onMessageSent(msg, tenant, EventURN.MESSAGE_SENT)
        } catch (ex: Exception) {
            LOGGER.warn("Unable to send notification", ex)
        }
    }
}
