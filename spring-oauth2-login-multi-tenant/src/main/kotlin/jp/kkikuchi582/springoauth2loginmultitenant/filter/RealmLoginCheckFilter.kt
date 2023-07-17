package jp.kkikuchi582.springoauth2loginmultitenant.filter

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationTrustResolver
import org.springframework.security.authentication.AuthenticationTrustResolverImpl
import org.springframework.security.config.annotation.SecurityConfigurer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.client.authentication.OAuth2AuthorizationCodeAuthenticationToken
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter
import org.springframework.security.web.DefaultRedirectStrategy
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.RedirectStrategy
import org.springframework.security.web.context.SecurityContextHolderFilter
import org.springframework.security.web.savedrequest.RequestCache
import org.springframework.security.web.util.UrlUtils
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.web.util.UriComponentsBuilder

class RealmLoginCheckFilter(
    private val clientRegistrationRepository: ClientRegistrationRepository,
    private val requestCache: RequestCache?,
) : Filter {
    private val trustResolver: AuthenticationTrustResolver = AuthenticationTrustResolverImpl()
    private val realmMatcher: RequestMatcher = AntPathRequestMatcher("/{realm}/**")

    // cf. org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver#authorizationRequestMatcher
    private val authorizationRequestMatcher: RequestMatcher =
        AntPathRequestMatcher("${OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI}/{registrationId}")
    private val redirectStrategy: RedirectStrategy = DefaultRedirectStrategy()

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        doFilter(request as HttpServletRequest, response as HttpServletResponse, chain)
    }

    private fun doFilter(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val realm = getRealm(request)
        // realmへのアクセスでないなら関係ない
        if (realm == null) {
            chain.doFilter(request, response)
            return
        }
        if (!isExistsRealm(realm)) {
            chain.doFilter(request, response)
            return
        }
        if (isAuthorizationRequest(request)) {
            chain.doFilter(request, response)
            return
        }

        // 認証されていないならログインページへ
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication == null || trustResolver.isAnonymous(authentication)) {
            redirectToLogin(request, response, realm)
            return
        }

        // 認証済みのrealmと異なるならログインページへ
        val authenticatedRealm = getAuthenticatedRealm(authentication)
        // realmを取得できないので無視
        if (authenticatedRealm == null) {
            chain.doFilter(request, response)
            return
        }

        // アクセスされたrealmと認証されているrealmが異なるのでログインページへ
        if (realm != authenticatedRealm) {
            redirectToLogin(request, response, realm)
            return
        }

        chain.doFilter(request, response)
    }

    private fun getRealm(request: HttpServletRequest): String? {
        val matcher = realmMatcher.matcher(request)
        return matcher.variables["realm"]

//        val servletPath = request.servletPath
//        if (servletPath.length <= 1) {
//            return null
//
//        }
//        val secondSlashIndex = servletPath.indexOf('/', 1)
//        if (secondSlashIndex < 0) {
//            return servletPath.substring(1)
//        }
//        return servletPath.substring(1, secondSlashIndex)
    }

    private fun isExistsRealm(realm: String): Boolean {
        return clientRegistrationRepository.findByRegistrationId(realm) != null
    }

    private fun isAuthorizationRequest(request: HttpServletRequest): Boolean {
        return authorizationRequestMatcher.matches(request)
    }

    private fun redirectToLogin(request: HttpServletRequest, response: HttpServletResponse, realm: String) {
        // cf. OAuth2AuthorizationRequestRedirectFilter
        // cf. SavedRequestAwareAuthenticationSuccessHandler
        requestCache?.saveRequest(request, response)

        // cf. org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler#postLogoutRedirectUri
        val redirectUrl = UriComponentsBuilder
            .fromHttpUrl(UrlUtils.buildFullRequestUrl(request))
            .replacePath(request.contextPath)
            .replaceQuery(null)
            .fragment(null)
            .pathSegment(
                OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI.substring(1),
                realm
            )
            .build()
            .toUriString()

        redirectStrategy.sendRedirect(request, response, redirectUrl)
    }

    private fun getAuthenticatedRealm(authentication: Authentication): String? {
        return when (authentication) {
            is OAuth2AuthenticationToken -> authentication.authorizedClientRegistrationId
            is OAuth2AuthorizationCodeAuthenticationToken -> authentication.clientRegistration.registrationId
            is OAuth2LoginAuthenticationToken -> authentication.clientRegistration.registrationId
            else -> null
        }
    }

    class Configurer(
        private val clientRegistrationRepository: ClientRegistrationRepository,
    ): SecurityConfigurer<DefaultSecurityFilterChain, HttpSecurity> {
        override fun init(builder: HttpSecurity) {}

        override fun configure(builder: HttpSecurity) {
            val requestCache = builder.getSharedObject(RequestCache::class.java)
            val filter = RealmLoginCheckFilter(clientRegistrationRepository, requestCache)
            builder.addFilterAfter(filter, SecurityContextHolderFilter::class.java)
        }
    }

    companion object {
        fun configure(clientRegistrationRepository: ClientRegistrationRepository) = Configurer(clientRegistrationRepository)
    }

}
