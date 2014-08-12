package ar.com.bamboo.security

import ar.com.bamboo.commonsEntity.Person
import grails.plugin.springsecurity.SpringSecurityService
import grails.test.mixin.Mock
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@Mock(User)
class UserSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test contraint"() {
        given:
        mockForConstraintsTests User
        when: "Los atributos username, password y person son nulos"
        def user = new User()
        then: "la validación falla porque"
        !user.validate()
        user.hasErrors()
        user.errors['username'] == 'nullable'
        user.errors['password'] == 'nullable'
        user.errors['person'] == 'nullable'


        when: "Los atributos estan vacios (username y password) y person null"
        user = new User(username: "", password: "")
        then: "La validacion falla"
        !user.validate()
        user.hasErrors()
        user.errors['username'] == 'nullable'
        user.errors['password'] == 'nullable'
        user.errors['person'] == 'nullable'


        when: "Cuando el user name no es un email y person null"
        user = new User(username: "orkero", password: "asdsd")
        then: "La validacion falla"
        !user.validate()
        user.hasErrors()
        user.errors['username'] == 'email'
        user.errors['person'] == 'nullable'


        when: "Cuando el username es un email y el password esta cargado, pero no la persona"
        user = new User(username: "orkero@fff.com", password: "asdsd")
        then: "Validacion success"
        !user.validate()
        user.hasErrors()
        user.errors['person'] == 'nullable'

        when: "Todos los campos bien cargados"
        user = new User(username: "orkero@fff.com", password: "asdsd", person: new Person())
        then: "Validacion success"
        user.validate()
        !user.hasErrors()
        user.save()
    }

    void "test unique username"() {
        given:
        mockForConstraintsTests User

        when: "cuando registro un usuario que no está repetido"
        def springSecurityService = Mock(SpringSecurityService)
        def user1 = new User(username: "jose@jose.com", password:  "4344", person: new Person())
        user1.springSecurityService = springSecurityService
        then: "se guarda ok"
        user1.save(flush: true, failOnError: true)

        when: "Registro dos usuarios con mismo username"
        def userRepetido = new User(username: "jose@jose.com", password:  "4344", person: new Person())
        userRepetido.springSecurityService = springSecurityService
        then: "la validación falla"
        !userRepetido.save(flush: true)
        !userRepetido.validate()
        userRepetido.hasErrors()
        userRepetido.errors['username'] == 'unique'
    }
}
