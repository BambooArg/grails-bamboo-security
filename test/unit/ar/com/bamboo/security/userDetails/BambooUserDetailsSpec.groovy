package ar.com.bamboo.security.userDetails

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
class BambooUserDetailsSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test constrictor"() {
        when: "Cuando no se le pasa ni nombre ni apellido"
        BambooUserDetails userDetails = new BambooUserDetails("bambooUser", "password", true, true, true, true, [], 1,
                "", "")
        then: "El detalle no tiene nada como full name"
        !userDetails.fullName

        when: "Cuando no se le pasa el nombre"
        userDetails = new BambooUserDetails("bambooUser", "password", true, true, true, true, [], 1, "mariano", "")
        then: "El detalle sólo tiene cargado el nombre"
        userDetails.fullName == "mariano"

        when: "Cuando no se le pasa el apellido"
        userDetails = new BambooUserDetails("bambooUser", "password", true, true, true, true, [], 1, "", "Kfuri")
        then: "El detalle solo tiene cargado el apellido"
        userDetails.fullName == "Kfuri"

        when: "Cuando no se le pasa el nombre y el apellido"
        userDetails = new BambooUserDetails("bambooUser", "password", true, true, true, true, [], 1, "mariano", "Kfuri")
        then: "El detalle solo tiene cargado el full name, separando el nombre del apellido por un espacio"
        userDetails.fullName == "mariano Kfuri"
    }
}
