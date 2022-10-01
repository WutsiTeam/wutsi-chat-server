package com.wutsi.platform.chat.`delegate`

import com.wutsi.platform.chat.dao.MessageRepository
import com.wutsi.platform.chat.dto.SearchConversationRequest
import com.wutsi.platform.chat.dto.SearchConversationResponse
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.tracing.TracingContext
import org.springframework.stereotype.Service
import javax.sql.DataSource

@Service
public class SearchConversationsDelegate(
    private val ds: DataSource,
    private val dao: MessageRepository,
    private val tracingContext: TracingContext,
    private val securityManager: com.wutsi.platform.chat.service.SecurityManager,
    private val logger: KVLogger
) {
    public fun invoke(request: SearchConversationRequest): SearchConversationResponse {
        logger.add("limit", request.limit)
        logger.add("offset", request.offset)
        logger.add("current_user_id", securityManager.currentUserId())

        val ids = loadIds(request)
        val messages = dao.findAllById(ids)
            .toList()
            .sortedWith(
                compareBy { ids.indexOf(it.id) }
            )
        return SearchConversationResponse(
            conversations = messages.map { it.toConversation() }
        )
    }

    private fun loadIds(request: SearchConversationRequest): List<Long> {
        val currentUserId = securityManager.currentUserId()
        val tenantId = tracingContext.tenantId()
        val sql = """
            SELECT MAX(id) as max_id, MAX(timestamp), conversation_id
            FROM T_MESSAGE
            WHERE
                (sender_id=$currentUserId OR recipient_id=$currentUserId)
                AND tenant_id=$tenantId
            GROUP BY conversation_id
            ORDER BY MAX(timestamp) DESC
            LIMIT ${request.limit}
            OFFSET ${request.offset}
        """.trimIndent()
        val cnn = ds.connection
        cnn.use {
            val stmt = cnn.createStatement()
            stmt.use {
                val rs = stmt.executeQuery(sql)
                rs.use {
                    val ids = mutableListOf<Long>()
                    while (rs.next()) {
                        ids.add(rs.getLong("max_id"))
                    }
                    return ids
                }
            }
        }
    }
}
