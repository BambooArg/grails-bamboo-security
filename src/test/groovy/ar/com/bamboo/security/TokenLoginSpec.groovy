package ar.com.bamboo.security

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(TokenLogin)
@Build(TokenLogin)
class TokenLoginSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test constraints"() {

        when: "Creo un token login vacío"
        TokenLogin tokenLogin = new TokenLogin()
        tokenLogin.validate()

        then: "La validación falla porque no se completaron los campos obligatorios"
        tokenLogin.hasErrors()
        tokenLogin.errors.getFieldError("user").code == 'nullable'
        tokenLogin.errors.getFieldError("token").code == 'nullable'

        when: "Creo un token completo con todos los objetos cargados"
        TokenLogin tokenLogin1 = tokenLogin.build()
        tokenLogin1.validate()

        then: "La validacion es correcta"
        !tokenLogin1.hasErrors()
    }
}
