package com.wutsi.platform.chat.service

import com.wutsi.platform.core.security.SubjectType
import com.wutsi.platform.core.security.WutsiPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class SecurityManager {
    fun currentUserId() = if (currentPrincipal().type == SubjectType.USER) {
        currentPrincipal().id.toLong()
    } else {
        null
    }

    private fun currentPrincipal(): WutsiPrincipal =
        SecurityContextHolder.getContext().authentication.principal as WutsiPrincipal
}
