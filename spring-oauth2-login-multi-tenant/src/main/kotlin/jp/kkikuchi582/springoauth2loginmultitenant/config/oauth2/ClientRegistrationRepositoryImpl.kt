package jp.kkikuchi582.springoauth2loginmultitenant.config.oauth2

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.stereotype.Component

@Component
class ClientRegistrationRepositoryImpl(
    private val registrations: Map<String, ClientRegistration>
) : ClientRegistrationRepository, Iterable<ClientRegistration> {
    @Autowired
    constructor() : this(
        listOf(
            ClientRegistration
                .withRegistrationId("realm1")
                .clientId("realm1-client")
                .clientSecret("PdFqUcqqkcTJB269DSVnIzO1gKfNhE7s")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .scope("openid")
                .issuerUri("http://localhost:8080/realms/realm1")
                .authorizationUri("http://localhost:8080/realms/realm1/protocol/openid-connect/auth")
                .tokenUri("http://localhost:8080/realms/realm1/protocol/openid-connect/token")
                .userInfoUri("http://localhost:8080/realms/realm1/protocol/openid-connect/userinfo")
                .jwkSetUri("http://localhost:8080/realms/realm1/protocol/openid-connect/certs")
                .providerConfigurationMetadata(
                    mapOf(
                        "end_session_endpoint" to "http://localhost:8080/realms/realm1/protocol/openid-connect/logout",
                    )
                )
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .userNameAttributeName("preferred_username")
                .build(),
            ClientRegistration
                .withRegistrationId("realm2")
                .clientId("realm2-client")
                .clientSecret("4eoXvjLbHPt6kE97bdzUVBFQYODb629d")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .scope("openid")
                .issuerUri("http://localhost:8080/realms/realm2")
                .authorizationUri("http://localhost:8080/realms/realm2/protocol/openid-connect/auth")
                .tokenUri("http://localhost:8080/realms/realm2/protocol/openid-connect/token")
                .userInfoUri("http://localhost:8080/realms/realm2/protocol/openid-connect/userinfo")
                .jwkSetUri("http://localhost:8080/realms/realm2/protocol/openid-connect/certs")
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .providerConfigurationMetadata(
                    mapOf(
                        "end_session_endpoint" to "http://localhost:8080/realms/realm2/protocol/openid-connect/logout",
                    )
                )
                .userNameAttributeName("preferred_username")
                .build(),
        ).associateBy { it.registrationId }
    )

    override fun findByRegistrationId(registrationId: String): ClientRegistration? {
        return registrations[registrationId]
    }

    override fun iterator(): Iterator<ClientRegistration> {
        return registrations.values.iterator()
    }
}