package ar.com.bamboo.security.commandObject

import grails.validation.Validateable

/**
 * Created by orko on 14/03/15.
 */
class AccountValidator extends RecoverPassword implements Validateable{
    boolean termsAndConditions

    static constraints = {
        termsAndConditions validator: { val, obj ->
            def ret = true
            if (!val){
                ret = "notAcceptTermsAndConditions"
            }
            return ret
        }
    }
}
