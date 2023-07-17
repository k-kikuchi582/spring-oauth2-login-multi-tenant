package jp.kkikuchi582.springoauth2loginmultitenant.config

import jp.kkikuchi582.springoauth2loginmultitenant.filter.RealmLoginCheckFilter
import org.springframework.boot.autoconfigure.security.StaticResourceLocation
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.boot.autoconfigure.security.servlet.StaticResourceRequest
import org.springframework.boot.autoconfigure.security.servlet.StaticResourceRequest.StaticResourceRequestMatcher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val clientRegistrationRepository: ClientRegistrationRepository
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.invoke {
            authorizeHttpRequests {
                authorize(PathRequest.toStaticResources().atCommonLocations(), permitAll)
                authorize("/", permitAll)
                authorize(anyRequest, authenticated)
            }
            formLogin {
                disable()
            }
            oauth2Login {
                clientRegistrationRepository = this@SecurityConfig.clientRegistrationRepository
            }
            logout {
                logoutSuccessHandler = oidcLogoutSuccessHandler()
            }
            http.apply(RealmLoginCheckFilter.configure(this@SecurityConfig.clientRegistrationRepository))
        }

        return http.build()
    }


    private fun oidcLogoutSuccessHandler(): LogoutSuccessHandler {
        val oidcClientInitiatedLogoutSuccessHandler =
            OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository)
        oidcClientInitiatedLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}")
        return oidcClientInitiatedLogoutSuccessHandler
    }
}