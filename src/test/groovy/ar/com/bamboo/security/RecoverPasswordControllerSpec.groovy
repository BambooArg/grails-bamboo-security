package ar.com.bamboo.security

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
        setup:
        def userServiceNoToken = Mock(UserService.class)
        userServiceNoToken.getTokenLoginNotExpiredByToken(_) >> { String token ->
            return null
        }

        def userServiceToken = Mock(UserService.class)
        userServiceToken.getTokenLoginNotExpiredByToken(_) >> { String token ->
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
        controller.userService = userServiceToken
        controller.index("342424242423")

        then: "The view is changePassword"
        view == '/recoverPassword/changePassword'
        model.size() == 1
        model.recoverPassword
    }

    void "test changePassword"() {
        setup:
        def userServiceNoToken = Mock(UserService.class)
        userServiceNoToken.getTokenLoginNotExpiredByToken(_) >> { String token ->
            return null
        }

        def userServiceToken = Mock(UserService.class)
        userServiceToken.getTokenLoginNotExpiredByToken(_) >> { String token ->
            return TokenLogin.build()
        }

        def userServiceChangePassword = Mock(UserService.class)
        userServiceChangePassword.getTokenLoginNotExpiredByToken(_) >> { String token ->
            return TokenLogin.build()
        }
        userServiceChangePassword.changePassword(_, _) >> { User user, String password ->

        }


        when: "The changePassword action with null params"
        controller.changePassword(null)

        then: "Response is 404"
        response.status == 404


        when: "The changePassword action with changepassword not valid"
        response.reset()
        controller.userService = userServiceToken
        controller.changePassword(new RecoverPassword())

        then: "Vuelve a la pantalla de welcome con los errores"
        view == '/recoverPassword/changePassword'
        model.size() == 1
        model.recoverPassword


        when: "Cuando el token enviado no es válido o expiró"
        response.reset()
        controller.userService = userServiceNoToken
        controller.changePassword(new RecoverPassword())

        then: "Token expired"
        view == '/tokenExpired'


        when: "The changePassword action with changepassword valid"
        response.reset()
        controller.userService = userServiceChangePassword
        RecoverPassword changePassword = new RecoverPassword(token: "dsadsad", password: "ggg", confirmPassword: "ggg")
        controller.changePassword(changePassword)

        then: "Se redirecciona a login"
        response.status == 200
    }
}
