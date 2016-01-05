package ar.com.bamboo.security.commandObject

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
class ChangePasswordSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test constraint fails"() {

        when: "Valido los campos obligatorios cuando no se carga nada"
        ChangePassword changePassword = new ChangePassword()
        changePassword.validate()

        then: "La validación falla"
        changePassword.hasErrors()
        changePassword.errors['oldPassword'].code == 'nullable'
        changePassword.errors['password'].code == 'nullable'
        changePassword.errors['confirmPassword'].code == 'nullable'
        changePassword.errors.errorCount == 3

        when: "Valido los campos obligatorios cuando no se carga nada"
        ChangePassword changePasswordBlank = new ChangePassword(oldPassword: "", password: "", confirmPassword: "")
        changePasswordBlank.validate()

        then: "La validación falla"
        changePasswordBlank.hasErrors()
        changePasswordBlank.errors['oldPassword'].code == 'blank'
        changePasswordBlank.errors['password'].code == 'blank'
        changePasswordBlank.errors['confirmPassword'].code == 'blank'
        changePasswordBlank.errors.errorCount == 3

        when: "El password no cumple con el formato de 8-20 caracteres y alphanumérico"
        ChangePassword changePasswordMin = new ChangePassword(oldPassword: "password", password: "passwo",
                confirmPassword: "passwo")
        changePasswordMin.validate()
        ChangePassword changePasswordMax = new ChangePassword(oldPassword: "password",
                password: "entrounpassworddemasiadolargoparaquevalidepormaximodecaracteres",
                confirmPassword: "entrounpassworddemasiadolargoparaquevalidepormaximodecaracteres")
        changePasswordMax.validate()
        ChangePassword changePassword3 = new ChangePassword(oldPassword: "password",
                password: "nanonanonano", confirmPassword: "nanonanonano")
        changePassword3.validate()

        then: "Fallan las 3 porque no cumplen con los parámetros de formato válido"
        changePasswordMin.hasErrors()
        changePasswordMin.errors.errorCount == 1
        changePasswordMin.errors['password'].code == 'minSize.notmet'
        changePasswordMax.hasErrors()
        changePasswordMax.errors.errorCount == 1
        changePasswordMax.errors['password'].code == 'maxSize.exceeded'
        changePassword3.hasErrors()
        changePassword3.errors.errorCount == 1
        changePassword3.errors['password'].code == 'passwordFormatInvalid'

        when: "La nueva password es diferente a la password de confirmación"
        ChangePassword changePasswordDifferent = new ChangePassword(oldPassword: "OldPass", password: "jose5555",
                confirmPassword: "maria43432")
        changePasswordDifferent.validate()

        then: "Falla la validación porque la password y la confirmación son diferentes"
        changePasswordDifferent.hasErrors()
        changePasswordDifferent.errors.errorCount == 1
        changePasswordDifferent.errors['confirmPassword'].code == 'passwordDifferent'
    }

    void "test constraint success"() {

        when: "Todos los parámetros son correctos"
        ChangePassword changePassword = new ChangePassword(oldPassword: "hola", password: "nano2403",
                confirmPassword: "nano2403")
        changePassword.validate()

        then: "La validación es correcta"
        !changePassword.hasErrors()
    }

}

