package com.wutsi.platform.chat.delegate

import com.wutsi.platform.chat.dto.Conversation
import com.wutsi.platform.chat.dto.Message
import com.wutsi.platform.chat.entity.MessageEntity

fun MessageEntity.toMessage() = Message(
    id = this.id ?: -1,
    conversationId = this.conversationId,
    senderId = this.senderId,
    recipientId = this.recipientId,
    text = this.text,
    created = this.created,
    deviceId = this.deviceId,
    timestamp = this.timestamp
)

fun MessageEntity.toConversation() = Conversation(
    id = this.conversationId,
    lastMessage = this.toMessage()
)
