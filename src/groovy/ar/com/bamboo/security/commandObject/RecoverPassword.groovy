package ar.com.bamboo.security.commandObject

import grails.validation.Validateable

import java.util.regex.Pattern

/**
 * Created by orko on 18/03/15.
 */
@Validateable
class RecoverPassword {
    public static Pattern PASSWORD_PATTERN_NUMERIC = ~/\d/
    public static Pattern PASSWORD_PATTERN_WORD = ~/[a-zA-Z]/
    String token
    String password
    String confirmPassword

    static constraints = {
        password blank: false, nullable: false, minSize: 8, maxSize: 20, validator: { val, obj ->
            def ret = true
            if (val && val.size() >= 8 && val.size() <= 20) {
                boolean hasNumber = RecoverPassword.PASSWORD_PATTERN_NUMERIC.matcher(val).find()
                boolean hasLetter = RecoverPassword.PASSWORD_PATTERN_WORD.matcher(val).find()
                if (!hasNumber || !hasLetter){
                    ret =  "passwordFormatInvalid"
                }
            }
            return  ret
        }
        confirmPassword blank: false, nullable: false, validator:  { val, obj ->
            def ret = true
            if (val && obj.password){
                ret = obj.password == val?: "passwordDifferent"
            }
            return  ret
        }
    }
}
