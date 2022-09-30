package com.wutsi.platform.chat.endpoint

import com.wutsi.platform.chat.`delegate`.SearchConversationsDelegate
import com.wutsi.platform.chat.dto.SearchConversationRequest
import com.wutsi.platform.chat.dto.SearchConversationResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class SearchConversationsController(
    public val `delegate`: SearchConversationsDelegate
) {
    @PostMapping("/v1/conversations/search")
    @PreAuthorize(value = "hasAuthority('chat-read')")
    public fun invoke(
        @Valid @RequestBody
        request: SearchConversationRequest
    ):
        SearchConversationResponse = delegate.invoke(request)
}
