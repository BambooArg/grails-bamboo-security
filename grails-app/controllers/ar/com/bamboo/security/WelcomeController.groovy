package ar.com.bamboo.security

import ar.com.bamboo.framework.BaseController
import ar.com.bamboo.security.commandObject.AccountValidator
import org.springframework.security.core.context.SecurityContextHolder

class WelcomeController extends BaseController{

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
        render model: [accountValidator: new AccountValidator(token: tokenLogin.token)], view: "welcome"
    }

    def validate(AccountValidator accountValidator){
        if (!accountValidator){
            notFound()
            return
        }

        TokenLogin tokenLogin = userService.getTokenLoginNotExpiredByToken(accountValidator.token)
        if (!tokenLogin){
            render view: "/tokenExpired"
            return
        }

        if (!accountValidator.validate()){
            render model: [accountValidator: accountValidator], view: "welcome"
            return
        }

        userService.validateAccount(tokenLogin.user, accountValidator.password)
        redirect controller: "login", action: "auth"
    }
}
