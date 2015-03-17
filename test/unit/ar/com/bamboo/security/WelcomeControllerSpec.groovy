package ar.com.bamboo.security

import ar.com.bamboo.security.commandObject.AccountValidator
import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(WelcomeController)
@Build(TokenLogin)
class WelcomeControllerSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test index"() {
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
        then: "The view is welcome"
        view == '/welcome/welcome'
        model.size() == 1
        model.accountValidator

        when: "Cuando el token enviado no es válido o expiró"
        response.reset()
        controller.userService = userServiceNoToken.createMock()
        controller.index("adasddssad")
        then: "Token expired"
        view == '/welcome/tokenExpired'
    }

    void "test validate"() {
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
        userServiceChangePassword.demandExplicit.validateAccount(){User user, String password ->

        }

        when: "The validate action with null params"
        controller.validate(null)
        then: "Response is 404"
        response.status == 404

        when: "The validate action with changepassword not valid"
        response.reset()
        controller.userService = userServiceToken.createMock()
        controller.validate(new AccountValidator())
        then: "Vuelve a la pantalla de welcome con los errores"
        view == '/welcome/welcome'
        model.size() == 1
        model.accountValidator

        when: "Cuando el token enviado no es válido o expiró"
        response.reset()
        controller.userService = userServiceNoToken.createMock()
        controller.validate(new AccountValidator())
        then: "Token expired"
        view == '/welcome/tokenExpired'

        when: "The validate action with changepassword valid"
        response.reset()
        controller.userService = userServiceChangePassword.createMock()
        AccountValidator changePassword = new AccountValidator(token: "dsadsad", password: "ggg", confirmPassword: "ggg",
                termsAndConditions: true)
        controller.validate(changePassword)
        then: "Se redirecciona a login"
        response.redirectedUrl == "/login/auth"
    }
}
