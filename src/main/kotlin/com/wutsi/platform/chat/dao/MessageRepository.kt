package com.wutsi.platform.chat.dao

import com.wutsi.platform.chat.entity.MessageEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface MessageRepository : CrudRepository<MessageEntity, Long> {
    fun findByReferenceId(referenceId: String): Optional<MessageEntity>
}
