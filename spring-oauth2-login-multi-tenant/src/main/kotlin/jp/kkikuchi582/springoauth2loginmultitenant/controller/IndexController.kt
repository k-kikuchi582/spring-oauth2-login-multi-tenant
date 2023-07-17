package jp.kkikuchi582.springoauth2loginmultitenant.controller

import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping(path = ["/"])
class IndexController(
    private val clientRegistrationRepository: ClientRegistrationRepository,
) {
    @GetMapping
    fun index(model: Model): String {
        if ( clientRegistrationRepository is Iterable<*> ) {
            val realms = (clientRegistrationRepository as Iterable<ClientRegistration>)
                .map { it.registrationId }
                .toList()
            model.addAttribute("realms", realms)
            model.addAttribute("loginBaseURI", OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI)
        }
        return "/index";
    }

}