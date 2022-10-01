package com.wutsi.platform.chat.entity

import java.time.OffsetDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "T_MESSAGE")
data class MessageEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val tenantId: Long = -1,
    val senderId: Long = -1,
    val recipientId: Long = -1,
    val conversationId: String = "",
    val referenceId: String = "",
    val text: String = "",
    val created: OffsetDateTime = OffsetDateTime.now(),
    val deviceId: String? = null,
    val timestamp: Long = -1
)
