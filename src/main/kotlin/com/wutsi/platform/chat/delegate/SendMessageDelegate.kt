package com.wutsi.platform.chat.`delegate`

import com.wutsi.platform.chat.dao.MessageRepository
import com.wutsi.platform.chat.dto.SendMessageRequest
import com.wutsi.platform.chat.dto.SendMessageResponse
import com.wutsi.platform.chat.entity.MessageEntity
import com.wutsi.platform.chat.event.EventURN
import com.wutsi.platform.chat.event.MessageEventPayload
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.platform.core.tracing.TracingContext
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
public class SendMessageDelegate(
    private val securityManager: com.wutsi.platform.chat.service.SecurityManager,
    private val dao: MessageRepository,
    private val tracingContext: TracingContext,
    private val logger: KVLogger,
    private val eventStream: EventStream
) {
    public fun invoke(request: SendMessageRequest): SendMessageResponse {
        val msg = dao.save(
            MessageEntity(
                deviceId = tracingContext.deviceId(),
                tenantId = tracingContext.tenantId()?.toLong() ?: -1,
                senderId = securityManager.currentUserId() ?: -1,
                recipientId = request.recipientId,
                created = OffsetDateTime.now(),
                conversationId = generateConversationId(request),
                text = request.text,
                timestamp = request.timestamp
            )
        )
        notify(msg)

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

    private fun notify(message: MessageEntity) {
        val payload = MessageEventPayload(message.id ?: -1, message.conversationId)
        eventStream.enqueue(EventURN.MESSAGE_SENT.urn, payload)
        eventStream.publish(EventURN.MESSAGE_SENT.urn, payload)
    }
}
