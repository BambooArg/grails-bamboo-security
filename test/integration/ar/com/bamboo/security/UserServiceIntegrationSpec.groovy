package ar.com.bamboo.security

import ar.com.bamboo.commonsEntity.Person
import grails.test.spock.IntegrationSpec
import org.springframework.dao.DuplicateKeyException

/**
 *
 */
class UserServiceIntegrationSpec extends IntegrationSpec {

    def userService

    Person person


    def setup() {
        new Role(authority: Role.ROLE_SUPERUSER).save(flush: true, failOnError: true)
        person = new Person(firstName: "Mariano").save(flush: true, failOnError: true)
    }

    def cleanup() {
    }

    void "test save action without role"() {

        when: "Intento guardar un usuario sin role pero con errores de constraint"
        User user = new User()
        then: "La acción devuelve false y no guarda"
        !userService.save(user)
        !user.id
        user.hasErrors()

        when: "Intento guardar un usuario sin roles pero sin errores de constraint"
        user = new User(username: "bamboo@gmail.com", password: "quedificil", person: person)
        then: "La acción devuelve true y guarda"
        userService.save(user)
        user.id
        !user.hasErrors()

        when: "Intento guardar un usuario que ya existe"
        user = new User(username: "bamboo@gmail.com", password: "quedificil", person: person)
        then: "La acción devuelve false"
        !userService.save(user)
        !user.id
        user.hasErrors()

    }

    void "test save action with role"() {

        when: "Intento guardar un usuario con role pero con errores de constraint"
        User user = new User()
        then: "La acción devuelve false y no guarda"
        !userService.save(user, Role.ROLE_SUPERUSER)
        !user.id
        user.hasErrors()

        when: "Intento guardar un usuario con roles y con todas las constraint cargadas"
        user = new User(username: "bamboo@gmail.com", password: "quedificil", person: person)
        Role role = Role.where { authority == Role.ROLE_SUPERUSER }.get()
        then: "La acción devuelve true y guarda"
        role
        role.authority == Role.ROLE_SUPERUSER
        userService.save(user, Role.ROLE_SUPERUSER)
        user.id == 2
        !user.hasErrors()
       // UserRole.exists(user.id, role.id)

        when: "Intento asignarle el rol de superuser de nuevo a la misma person"
        userService.save(user, Role.ROLE_SUPERUSER)
        then: "Se arroja una exception"
        thrown(DuplicateKeyException)

        when: "Intento guardar un usuario repetido"
        user = new User(username: "bamboo@gmail.com", password: "quedificil", person: person)
        then: "La acción devuelve true y guarda"
        !userService.save(user, Role.ROLE_SUPERUSER)
        !user.id
        user.hasErrors()

    }
}
