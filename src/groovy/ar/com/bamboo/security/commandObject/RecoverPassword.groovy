package ar.com.bamboo.security.commandObject

import grails.validation.Validateable

/**
 * Created by orko on 18/03/15.
 */
@Validateable
class RecoverPassword {
    String token
    String password
    String confirmPassword

    static constraints = {
        password blank: false, nullable: false
        confirmPassword blank: false, nullable: false, validator:  { val, obj ->
            def ret = true
            if (val && obj.password){
                ret = obj.password == val?: "passwordDifferent"
            }
            return  ret
        }
    }
}
