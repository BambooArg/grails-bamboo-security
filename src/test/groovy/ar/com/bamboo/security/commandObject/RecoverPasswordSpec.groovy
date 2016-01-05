package ar.com.bamboo.security.commandObject

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
class RecoverPasswordSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test constraints"() {
        when: "Creo un recover password sin ningún dato"
        def recoverPassword = new RecoverPassword()
        then: "La validación es inválida"
        !recoverPassword.validate()
        recoverPassword.hasErrors()
        recoverPassword.errors['token']
        recoverPassword.errors['password']
        recoverPassword.errors['confirmPassword']

        when: "Creo un recover password con los datos cargados, pero el formato del password es incorrecto. No tiene numeros"
        recoverPassword = new RecoverPassword(token: "asadsad", password: "holacomoandas", confirmPassword: "holacomoandas")
        then: "La validación es inválida"
        !recoverPassword.validate()
        recoverPassword.hasErrors()
        !recoverPassword.errors['token']
        recoverPassword.errors['password']
        !recoverPassword.errors['confirmPassword']

        when: "Creo un recover password con los datos cargados, pero el formato del password es incorrecto. No tiene letras"
        recoverPassword = new RecoverPassword(token: "asadsad", password: "1111111111", confirmPassword: "1111111111")
        then: "La validación es inválida"
        !recoverPassword.validate()
        recoverPassword.hasErrors()
        !recoverPassword.errors['token']
        recoverPassword.errors['password']
        !recoverPassword.errors['confirmPassword']

        when: "Creo un recover password con los datos cargados, pero el formato del password es incorrecto. No respeta el mínimo"
        recoverPassword = new RecoverPassword(token: "asadsad", password: "1g", confirmPassword: "1g")
        then: "La validación es inválida"
        !recoverPassword.validate()
        recoverPassword.hasErrors()
        !recoverPassword.errors['token']
        recoverPassword.errors['password']
        !recoverPassword.errors['confirmPassword']

        when: "Creo un recover password con los datos cargados, pero el formato del password es incorrecto. No respeta el maximo"
        recoverPassword = new RecoverPassword(token: "asadsad",
                password: "1gasdadfdfssdfsdfdsfsfsasd asdasd sadasdas dasdasda", confirmPassword: "1gasdadfdfssdfsdfdsfsfsasd asdasd sadasdas dasdasda")
        then: "La validación es inválida"
        !recoverPassword.validate()
        recoverPassword.hasErrors()
        !recoverPassword.errors['token']
        recoverPassword.errors['password']
        !recoverPassword.errors['confirmPassword']

        when: "Creo un recover password con los datos cargados, pero el formato del password es incorrecto. La confirmación no es igual"
        recoverPassword = new RecoverPassword(token: "asadsad",
                password: "pepe4433", confirmPassword: "pepe4433ff")
        then: "La validación es inválida"
        !recoverPassword.validate()
        recoverPassword.hasErrors()
        !recoverPassword.errors['token']
        !recoverPassword.errors['password']
        recoverPassword.errors['confirmPassword']

        when: "Creo un recover password con los datos cargados, pero el formato del password es incorrecto. La confirmación no es igual"
        recoverPassword = new RecoverPassword(token: "asadsad",
                password: "pepe4433", confirmPassword: "pepe4433")
        then: "La validación es inválida"
        recoverPassword.validate()
    }
}
