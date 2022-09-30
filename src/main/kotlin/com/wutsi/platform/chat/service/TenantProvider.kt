package com.wutsi.platform.chat.service

import com.wutsi.platform.core.tracing.TracingContext
import org.springframework.stereotype.Service

@Service
public class TenantProvider(
    private val tracingContext: TracingContext
) {
    fun id(): Long? =
        tracingContext.tenantId()?.toLong()
}
