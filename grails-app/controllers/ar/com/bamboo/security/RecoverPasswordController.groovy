package ar.com.bamboo.security

import ar.com.bamboo.framework.BaseController
import ar.com.bamboo.security.commandObject.RecoverPassword
import org.springframework.security.core.context.SecurityContextHolder

class RecoverPasswordController extends BaseController{

    def userService

    def index(String token) {
        if (!token){
            notFound()
            return
        }
        SecurityContextHolder.clearContext()
        TokenLogin tokenLogin = userService.getTokenLoginNotExpiredByToken(token)
        if (!tokenLogin){
            render view: "/tokenExpired"
            return
        }
        render model: [recoverPassword: new RecoverPassword(token: tokenLogin.token)], view: "recoverPassword"
    }

    def changePassword(RecoverPassword recoverPassword){
        if (!recoverPassword){
            notFound()
            return
        }

        TokenLogin tokenLogin = userService.getTokenLoginNotExpiredByToken(recoverPassword.token)
        if (!tokenLogin){
            render view: "/tokenExpired"
            return
        }

        if (!recoverPassword.validate()){
            render model: [recoverPassword: recoverPassword], view: "recoverPassword"
            return
        }

        userService.changePassword(tokenLogin.user, recoverPassword.password)
        redirect controller: "login", action: "auth"
    }
}
