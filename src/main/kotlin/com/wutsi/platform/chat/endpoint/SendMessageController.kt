package com.wutsi.platform.chat.endpoint

import com.wutsi.platform.chat.`delegate`.SendMessageDelegate
import com.wutsi.platform.chat.dto.SendMessageRequest
import com.wutsi.platform.chat.dto.SendMessageResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class SendMessageController(
    public val `delegate`: SendMessageDelegate
) {
    @PostMapping("/v1/messages")
    @PreAuthorize(value = "hasAuthority('chat-manage')")
    public fun invoke(
        @Valid @RequestBody
        request: SendMessageRequest
    ): SendMessageResponse =
        delegate.invoke(request)
}
