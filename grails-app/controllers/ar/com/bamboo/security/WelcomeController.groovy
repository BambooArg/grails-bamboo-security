package ar.com.bamboo.security

import ar.com.bamboo.security.commandObject.AccountValidator

import static org.springframework.http.HttpStatus.NOT_FOUND

class WelcomeController {

    def userService

    def index(String token) {
        if (!token){
            notFound()
            return
        }
        TokenLogin tokenLogin = userService.getTokenLoginNotExpiredByToken(token)
        if (!tokenLogin){
            render view: "tokenExpired"
            return
        }
        render model: [accountValidator: new AccountValidator(idUser: tokenLogin.user.id)], view: "welcome"
    }

    def validate(AccountValidator accountValidator){
        if (!accountValidator){
            notFound()
            return
        }

        if (!accountValidator.validate()){
            render model: [accountValidator: accountValidator], view: "welcome"
            return
        }

        userService.validateAccount(accountValidator.idUser, accountValidator.password)
        redirect controller: "login", action: "auth"
    }

    protected void notFound() {
        withFormat {
            json {
                response.status = NOT_FOUND.value()
            }

            '*'{
                response.status = NOT_FOUND.value()
            }
        }
    }
}
