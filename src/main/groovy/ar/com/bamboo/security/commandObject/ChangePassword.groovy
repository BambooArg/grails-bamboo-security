package ar.com.bamboo.security.commandObject

import grails.validation.Validateable

/**
 * Created by orko on 22/03/15.
 */
class ChangePassword implements Validateable{
    String oldPassword
    String password
    String confirmPassword

    static constraints = {
        importFrom RecoverPassword
        oldPassword blank: false
    }
}
