package com.wutsi.platform.chat.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.platform.account.WutsiAccountApi
import com.wutsi.platform.account.dto.Account
import com.wutsi.platform.core.security.SubjectType
import com.wutsi.platform.core.security.SubjectType.USER
import com.wutsi.platform.core.security.spring.SpringAuthorizationRequestInterceptor
import com.wutsi.platform.core.security.spring.jwt.JWTBuilder
import com.wutsi.platform.core.test.TestRSAKeyProvider
import com.wutsi.platform.core.test.TestTokenProvider
import com.wutsi.platform.core.test.TestTracingContext
import com.wutsi.platform.core.tracing.TracingContext
import com.wutsi.platform.core.tracing.spring.SpringTracingRequestInterceptor
import com.wutsi.platform.tenant.WutsiTenantApi
import com.wutsi.platform.tenant.dto.GetTenantResponse
import com.wutsi.platform.tenant.dto.Logo
import com.wutsi.platform.tenant.dto.Tenant
import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.web.client.RestTemplate

abstract class AbstractSecuredController {
    companion object {
        const val USER_ID = 100L
        const val TENANT_ID = 1L
        const val DEVICE_ID = "1111-2222-3333"
    }

    private lateinit var tracingContext: TracingContext

    protected lateinit var rest: RestTemplate

    @MockBean
    protected lateinit var tenantApi: WutsiTenantApi

    @MockBean
    protected lateinit var accountApi: WutsiAccountApi

    protected val tenant = Tenant(
        id = 1,
        name = "test",
        logos = listOf(
            Logo(type = "PICTORIAL", url = "http://www.goole.com/images/1.png")
        ),
        countries = listOf("CM"),
        languages = listOf("en", "fr"),
        currency = "XAF",
        domainName = "www.wutsi.com",
        webappUrl = "https://www.wusi.me",
        monetaryFormat = "#,###,##0 XAF"
    )

    @BeforeEach
    open fun setUp() {
        tracingContext = TestTracingContext(tenantId = TENANT_ID.toString(), deviceId = DEVICE_ID)

        rest = createResTemplate()

        doReturn(GetTenantResponse(tenant)).whenever(tenantApi).getTenant(any())
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

    protected fun createAccount(id: Long = USER_ID, displayName: String = "Ray Sponsible", fcmToken: String? = null) =
        Account(
            id = id,
            displayName = displayName,
            fcmToken = fcmToken
        )
}
