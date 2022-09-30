package com.wutsi.platform.chat.endpoint

import com.wutsi.platform.chat.`delegate`.SearchMessagesDelegate
import com.wutsi.platform.chat.dto.SearchMessageRequest
import com.wutsi.platform.chat.dto.SearchMessageResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class SearchMessagesController(
    public val `delegate`: SearchMessagesDelegate
) {
    @PostMapping("/v1/messages/search")
    @PreAuthorize(value = "hasAuthority('chat-read')")
    public fun invoke(
        @Valid @RequestBody
        request: SearchMessageRequest
    ): SearchMessageResponse =
        delegate.invoke(request)
}
