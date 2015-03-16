package ar.com.bamboo.security.commandObject

import grails.validation.Validateable

/**
 * Created by orko on 14/03/15.
 */
@Validateable
class AccountValidator {
    Long idUser
    String password
    String confirmPassword
    boolean termsAndConditions

    static constraints = {
        password blank: false, nullable: false
        confirmPassword blank: false, nullable: false, validator:  { val, obj ->
            def ret = true
            if (val && obj.password){
                ret = obj.password == val?: "passwordDifferent"
            }
            return  ret
        }
        termsAndConditions validator: { val, obj ->
            def ret = true
            if (!val){
                ret = "notAcceptTermsAndConditions"
            }
            return ret
        }
    }
}
