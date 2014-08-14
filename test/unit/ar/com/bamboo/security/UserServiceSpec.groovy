package ar.com.bamboo.security

import ar.com.bamboo.commonsEntity.Person
import grails.plugin.springsecurity.SpringSecurityService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(UserService)
@Mock([User, Role, UserRole])
class UserServiceSpec extends Specification {

    def setup() {
        new Role(authority: Role.ROLE_SUPERUSER).save(flush: true, failOnError: true)
    }

    def cleanup() {
    }

    void "test save action without role"() {
        given:
        def springSecurityService = mockFor(SpringSecurityService)
        springSecurityService.demandExplicit.encodePassword(){String  password ->
            return password
        }
        Person p = new Person(firstName: "Mariano")

        when: "Cuando registro usuario sin los datos oblogatorios"
        User user = new User()
        then: "El registro de usuario retorna false"
        !service.save(user)
        user.hasErrors()
        !user.id

        when: "Cuando registro usuario con los datos obligatorios"
        user = new User(username: "bamboo@gmail.com", password: "quedificil", person: p)
        then: "El registro de usuario retorna true y el usuario tiene id"
        service.save(user)
        !user.hasErrors()
        user.id

        when: "Cuando registro usuario repetido"
        user = new User(username: "bamboo@gmail.com", password: "quedificil", person: p)
        then: "El registro de usuario retorna false"
        !service.save(user)
        user.hasErrors()
        !user.id
    }

    void "test save action witho role"() {
        given:
        def springSecurityService = mockFor(SpringSecurityService)
        springSecurityService.demandExplicit.encodePassword(){String  password ->
            return password
        }
        Person p = new Person(firstName: "Mariano")

        when: "Cuando registro usuario sin los datos oblogatorios"
        User user = new User()
        then: "El registro de usuario retorna false"
        !service.save(user, Role.ROLE_SUPERUSER)
        user.hasErrors()
        !user.id

        when: "Cuando registro usuario con los datos obligatorios"
        user = new User(username: "bamboo@gmail.com", password: "quedificil", person: p)
        then: "El registro de usuario retorna true y el usuario tiene id"
        service.save(user, Role.ROLE_SUPERUSER)
        !user.hasErrors()
        user.id
        user.getAuthorities()
        UserRole.exists(user.id, 1)

        when: "Cuando registro usuario repetido"
        user = new User(username: "bamboo@gmail.com", password: "quedificil", person: p)
        then: "El registro de usuario retorna false"
        !service.save(user, Role.ROLE_SUPERUSER)
        user.hasErrors()
        !user.id
    }
}
