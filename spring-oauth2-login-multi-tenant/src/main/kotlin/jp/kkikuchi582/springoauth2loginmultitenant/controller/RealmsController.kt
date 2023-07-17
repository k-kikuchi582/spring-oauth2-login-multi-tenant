package jp.kkikuchi582.springoauth2loginmultitenant.controller

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.client.HttpClientErrorException.NotFound
import org.springframework.web.servlet.ModelAndView

@Controller
@RequestMapping(
    path = [
        "/{realm}"
    ]
)
class RealmsController(
    private val clientRegistrationRepository: ClientRegistrationRepository,
) {

    @GetMapping
    fun realms(@PathVariable("realm") realm: String): ModelAndView {
        val modelAndView = ModelAndView()
        val clientRegistration = clientRegistrationRepository.findByRegistrationId(realm)
        if (clientRegistration == null) {
            modelAndView.status = HttpStatus.NOT_FOUND
            return modelAndView
        }
        modelAndView.addObject("realm", realm)
        modelAndView.viewName = "/realms"
        return modelAndView
    }

}