package ar.com.bamboo.security.services

import ar.com.bamboo.security.User
import ar.com.bamboo.security.userDetails.BambooUserDetails
import grails.test.spock.IntegrationSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UsernameNotFoundException

/**
 *
 */
class BambooUserDetailsServiceSpec extends IntegrationSpec {

    @Autowired
    BambooUserDetailsService bambooUserDetailsService

    def setup() {
        new User(username: "josesito@gmail.com", password: "mypassword").save(flush: true, failOnError: true)
    }

    def cleanup() {
    }

    void "pruebo loadUserByUsername"() {
        when: "busco el usuario por username"
        BambooUserDetails usuarioDetails = bambooUserDetailsService.loadUserByUsername("josesito@gmail.com")
        then: "El usuario existe"
        usuarioDetails
        usuarioDetails.id
        usuarioDetails.username == 'josesito@gmail.com'

        when: "busco un usuario que no existe"
        bambooUserDetailsService.loadUserByUsername("jose@gmail.com")
        then: "Arroja la exceptión que el usuario no existe"
        thrown(UsernameNotFoundException)
    }
}
