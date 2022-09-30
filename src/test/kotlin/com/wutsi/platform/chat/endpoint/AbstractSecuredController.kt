package com.wutsi.platform.chat.endpoint

import com.wutsi.platform.core.security.SubjectType
import com.wutsi.platform.core.security.SubjectType.USER
import com.wutsi.platform.core.security.spring.SpringAuthorizationRequestInterceptor
import com.wutsi.platform.core.security.spring.jwt.JWTBuilder
import com.wutsi.platform.core.test.TestRSAKeyProvider
import com.wutsi.platform.core.test.TestTokenProvider
import com.wutsi.platform.core.test.TestTracingContext
import com.wutsi.platform.core.tracing.TracingContext
import com.wutsi.platform.core.tracing.spring.SpringTracingRequestInterceptor
import org.junit.jupiter.api.BeforeEach
import org.springframework.web.client.RestTemplate

abstract class AbstractSecuredController {
    companion object {
        const val USER_ID = 100L
        const val TENANT_ID = 1L
        const val DEVICE_ID = "1111-2222-3333"
    }

    private lateinit var tracingContext: TracingContext

    protected lateinit var rest: RestTemplate

    @BeforeEach
    open fun setUp() {
        tracingContext = TestTracingContext(tenantId = TENANT_ID.toString(), deviceId = DEVICE_ID)

        rest = createResTemplate()
    }

    protected fun createResTemplate(
        scope: List<String> = listOf(
            "chat-manage",
            "chat-read"
        ),
        subjectId: Long = USER_ID,
        subjectType: SubjectType = USER,
        admin: Boolean = false
    ): RestTemplate {
        val rest = RestTemplate()

        val tokenProvider = TestTokenProvider(
            JWTBuilder(
                subject = subjectId.toString(),
                subjectType = subjectType,
                scope = scope,
                keyProvider = TestRSAKeyProvider(),
                admin = admin
            ).build()
        )

        rest.interceptors.add(SpringTracingRequestInterceptor(tracingContext))
        rest.interceptors.add(SpringAuthorizationRequestInterceptor(tokenProvider))
        return rest
    }
}
