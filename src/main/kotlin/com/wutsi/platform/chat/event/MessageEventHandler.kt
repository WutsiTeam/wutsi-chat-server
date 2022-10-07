package com.wutsi.platform.chat.event

import com.wutsi.platform.chat.dao.MessageRepository
import com.wutsi.platform.chat.delegate.SendMessageDelegate
import com.wutsi.platform.chat.dto.SendMessageRequest
import com.wutsi.platform.chat.error.ErrorURN
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.ParameterType
import com.wutsi.platform.core.error.exception.NotFoundException
import com.wutsi.platform.rtm.event.MessageReceivedEventPayload
import com.wutsi.platform.rtm.event.MessageSentEventPayload
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import javax.transaction.Transactional

@Service
class MessageEventHandler(
    private val delegate: SendMessageDelegate,
    private val dao: MessageRepository
) {
    @Transactional
    fun onMessageSent(payload: MessageSentEventPayload) {
        payload.chatMessage ?: return

        delegate.send(
            request = SendMessageRequest(
                conversationId = payload.chatMessage?.roomId ?: "",
                referenceId = payload.chatMessage?.id ?: "",
                text = payload.chatMessage?.text ?: "",
                timestamp = payload.chatMessage?.createdAt ?: -1,
                recipientId = payload.chatMessage?.metadata?.get("recipientId")?.toString()?.toLong() ?: -1
            ),
            senderId = payload.chatMessage?.author?.id?.toLong() ?: -1,
            tenantId = payload.chatMessage?.metadata?.get("tenantId")?.toString()?.toLong() ?: -1,
            deviceId = payload.chatMessage?.metadata?.get("deviceId")?.toString()
        )
    }

    @Transactional
    fun onMessageReceived(payload: MessageReceivedEventPayload) {
        payload.chatMessageId ?: return

        val message = dao.findByReferenceId(payload.chatMessageId!!)
            .orElseThrow {
                throw NotFoundException(
                    error = Error(
                        code = ErrorURN.MESSAGE_NOT_FOUND.urn,
                        parameter = Parameter(
                            name = "chatMessageId",
                            value = payload.chatMessageId,
                            type = ParameterType.PARAMETER_TYPE_PAYLOAD
                        )
                    )
                )
            }
        message.received = OffsetDateTime.now()
        dao.save(message)
    }
}
