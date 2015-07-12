package ar.com.bamboo.security

import ar.com.bamboo.security.commandObject.AccountValidator
import ar.com.bamboo.security.commandObject.RecoverPassword
import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(RecoverPasswordController)
@Build(TokenLogin)
class RecoverPasswordControllerSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test recover action"() {
        given:
        def userServiceNoToken = mockFor(UserService.class)
        userServiceNoToken.demandExplicit.getTokenLoginNotExpiredByToken(){ String token ->
            return null
        }

        def userServiceToken = mockFor(UserService.class)
        userServiceToken.demandExplicit.getTokenLoginNotExpiredByToken(){ String token ->
            return TokenLogin.build()
        }

        when: "The index action with null token params"
        controller.index(null)
        then: "Response is 404"
        response.status == 404

        when: "The index action with empty token params"
        response.reset()
        controller.index("")
        then: "Response is 404"
        response.status == 404

        when: "The index action with token that not exists"
        response.reset()
        controller.userService = userServiceToken.createMock()
        controller.index("342424242423")
        then: "The view is changePassword"
        view == '/recoverPassword/changePassword'
        model.size() == 1
        model.recoverPassword
    }

    void "test changePassword"() {
        given:
        def userServiceNoToken = mockFor(UserService.class)
        userServiceNoToken.demandExplicit.getTokenLoginNotExpiredByToken(1){ String token ->
            return null
        }

        def userServiceToken = mockFor(UserService.class)
        userServiceToken.demandExplicit.getTokenLoginNotExpiredByToken(){ String token ->
            return TokenLogin.build()
        }

        def userServiceChangePassword = mockFor(UserService.class)
        userServiceChangePassword.demandExplicit.getTokenLoginNotExpiredByToken(){ String token ->
            return TokenLogin.build()
        }
        userServiceChangePassword.demandExplicit.changePassword(){User user, String password ->

        }

        when: "The changePassword action with null params"
        controller.changePassword(null)
        then: "Response is 404"
        response.status == 404

        when: "The changePassword action with changepassword not valid"
        response.reset()
        controller.userService = userServiceToken.createMock()
        controller.changePassword(new RecoverPassword())
        then: "Vuelve a la pantalla de welcome con los errores"
        view == '/recoverPassword/changePassword'
        model.size() == 1
        model.recoverPassword

        when: "Cuando el token enviado no es válido o expiró"
        response.reset()
        controller.userService = userServiceNoToken.createMock()
        controller.changePassword(new RecoverPassword())
        then: "Token expired"
        view == '/tokenExpired'

        when: "The changePassword action with changepassword valid"
        response.reset()
        controller.userService = userServiceChangePassword.createMock()
        RecoverPassword changePassword = new RecoverPassword(token: "dsadsad", password: "ggg", confirmPassword: "ggg")
        controller.changePassword(changePassword)
        then: "Se redirecciona a login"
        response.status == 200
    }
}
