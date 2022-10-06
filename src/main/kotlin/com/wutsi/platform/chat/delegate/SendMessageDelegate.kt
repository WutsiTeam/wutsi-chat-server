package com.wutsi.platform.chat.`delegate`

import com.wutsi.platform.chat.dao.MessageRepository
import com.wutsi.platform.chat.dto.SendMessageRequest
import com.wutsi.platform.chat.dto.SendMessageResponse
import com.wutsi.platform.chat.entity.MessageEntity
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.tracing.TracingContext
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
public class SendMessageDelegate(
    private val securityManager: com.wutsi.platform.chat.service.SecurityManager,
    private val dao: MessageRepository,
    private val tracingContext: TracingContext,
    private val logger: KVLogger
) {
    fun invoke(request: SendMessageRequest): SendMessageResponse =
        send(
            request = request,
            senderId = securityManager.currentUserId() ?: -1,
            tenantId = tracingContext.tenantId()?.toLong() ?: -1,
            deviceId = tracingContext.deviceId()
        )

    fun send(request: SendMessageRequest, senderId: Long, tenantId: Long, deviceId: String?): SendMessageResponse {
        // Store
        val msg = dao.save(
            MessageEntity(
                deviceId = deviceId,
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

        logger.add("message_id", msg.id)
        return SendMessageResponse(
            id = msg.id!!
        )
    }
}
