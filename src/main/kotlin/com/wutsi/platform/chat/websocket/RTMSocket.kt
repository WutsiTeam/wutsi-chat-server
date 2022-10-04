package com.wutsi.platform.chat.websocket

import org.slf4j.LoggerFactory
import org.springframework.cache.Cache
import org.springframework.stereotype.Component
import javax.websocket.OnOpen
import javax.websocket.Session
import javax.websocket.server.PathParam
import javax.websocket.server.ServerEndpoint

@Component
@ServerEndpoint(value = "/rtm/{conversation-id}")
public class RTMSocket(
    private val cache: Cache
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(RTMSocket::class.java)
    }

    /**
     * Open a new session and return to client the session-id
     */
    @OnOpen
    fun onOpen(session: Session, @PathParam("conversation-id") conversationId: String) {
        val sessionId = session.id
        LOGGER.info("session_id=$sessionId conversation_id=$conversationId")

        // Associate the conversation with the session
        cache.put(conversationKey(conversationId), sessionId)

        // Associate the conversation with the session
        session.asyncRemote.sendText(sessionId)
    }

//    @EventListener
//    fun onEvent(event: Event) {
//        if (event.type == EventURN.MESSAGE_SENT.urn) {
//            LOGGER.info("event_type=${event.type} event_payload=${event.payload}")
//        }
//    }

    private fun conversationKey(conversationId: String) = "conversation_$conversationId"
}
