package ar.com.bamboo.security.services

import ar.com.bamboo.security.UserService
import ar.com.bamboo.security.userDetails.BambooUserDetails
import grails.plugin.springsecurity.SpringSecurityService
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by orko on 04/01/15.
 */
class BambooSecurityService extends SpringSecurityService{

    @Autowired
    UserService userService

    Object getCurrentUser() {
        if (!isLoggedIn()) {
            return null
        }
        BambooUserDetails user = (BambooUserDetails)principal
        userService.getByUsername(user.username)
    }
}
