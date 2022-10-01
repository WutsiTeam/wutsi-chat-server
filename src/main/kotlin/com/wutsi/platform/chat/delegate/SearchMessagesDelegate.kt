package com.wutsi.platform.chat.`delegate`

import com.wutsi.platform.chat.dto.SearchMessageRequest
import com.wutsi.platform.chat.dto.SearchMessageResponse
import com.wutsi.platform.chat.entity.MessageEntity
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service
import javax.persistence.EntityManager
import javax.persistence.Query

@Service
public class SearchMessagesDelegate(
    private val em: EntityManager,
    private val logger: KVLogger
) {
    public fun invoke(request: SearchMessageRequest): SearchMessageResponse {
        logger.add("conversation_id", request.conversationId)
        logger.add("limit", request.limit)
        logger.add("offset", request.offset)

        val sql = sql(request)
        val query = em.createQuery(sql)
        parameters(request, query)
        val messages = query
            .setFirstResult(request.offset)
            .setMaxResults(request.limit)
            .resultList as List<MessageEntity>

        logger.add("count", messages.size)
        return SearchMessageResponse(
            messages = messages.map { it.toMessage() }
        )
    }

    private fun sql(request: SearchMessageRequest): String {
        val select = select()
        val where = where(request)
        return if (where.isNullOrEmpty()) {
            select
        } else {
            "$select WHERE $where ORDER BY a.timestamp DESC"
        }
    }

    private fun select(): String =
        "SELECT a FROM MessageEntity a"

    private fun where(request: SearchMessageRequest): String {
        val criteria = mutableListOf<String>()

        if (!request.conversationId.isNullOrEmpty()) {
            criteria.add("a.conversationId = :conversation_id")
        }
        if (request.accountId1 != null) {
            criteria.add("(a.senderId = :account_id1 OR a.recipientId = :account_id1)")
        }
        if (request.accountId2 != null) {
            criteria.add("(a.senderId = :account_id2 OR a.recipientId = :account_id2)")
        }
        return criteria.joinToString(separator = " AND ")
    }

    private fun parameters(request: SearchMessageRequest, query: Query) {
        if (!request.conversationId.isNullOrEmpty()) {
            query.setParameter("conversation_id", request.conversationId)
        }
        if (request.accountId1 != null) {
            query.setParameter("account_id1", request.accountId1)
        }
        if (request.accountId2 != null) {
            query.setParameter("account_id2", request.accountId2)
        }
    }
}
