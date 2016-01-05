package ar.com.bamboo.security.services

import ar.com.bamboo.commonsEntity.Person
import ar.com.bamboo.security.User
import ar.com.bamboo.security.userDetails.BambooUserDetails
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UsernameNotFoundException
import spock.lang.Specification

/**
 *
 */
@Integration
@Rollback
class BambooUserDetailsServiceSpec extends Specification {

    @Autowired
    BambooUserDetailsService bambooUserDetailsService
    Person person

    def setupData() {
        person = new Person(lastName: "pepe")
        person.save(flush: true, failOnError: true)
        new User(username: "josesito@gmail.com", password: "mypassword", person: person).save(flush: true,
                failOnError: true)
    }

    def cleanup() {
    }

    void "pruebo loadUserByUsername"() {
        setup:
        setupData()

        when: "busco el usuario por username"
        BambooUserDetails usuarioDetails = bambooUserDetailsService.loadUserByUsername("josesito@gmail.com")
        then: "El usuario existe"
        usuarioDetails
        usuarioDetails.id
        usuarioDetails.username == 'josesito@gmail.com'
        usuarioDetails.fullName == person.lastName

        when: "busco un usuario que no existe"
        bambooUserDetailsService.loadUserByUsername("jose@gmail.com")
        then: "Arroja la exceptión que el usuario no existe"
        thrown(UsernameNotFoundException)
    }
}
