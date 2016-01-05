package ar.com.bamboo.security

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestFor(Role)
class RoleSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test contraint"() {

        when: "El atributo authority no puede estar vacío"
        Role role = new Role()

        then: "la validación falla porque"
        !role.validate()
        role.hasErrors()
        role.errors.getFieldError("authority").code == 'nullable'


        when: "El atributo authority no es vacío"
        role = new Role(authority: Role.ROLE_SUPERUSER)

        then: "la validación funciona"
        role.validate()
        !role.hasErrors()
    }

    void "test unique authority"() {

        when: "Registro un primer registro para luego poder validar"
        Role role = new Role(authority: Role.ROLE_SUPERUSER)

        then: "El rol se guarda con éxito"
        role.save(flush: true, failOnError: true)


        when: "Cuando registro dos Roles con el mismo nombre authority"
        role = new Role(authority: Role.ROLE_SUPERUSER)

        then: "No permite guardar"
        !role.save(flush: true)
        !role.validate()
        role.hasErrors()
        role.errors.getFieldError("authority").code == 'unique'
    }
}
