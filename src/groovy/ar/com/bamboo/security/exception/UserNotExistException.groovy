package ar.com.bamboo.security.exception

import groovy.transform.CompileStatic

/**
 * Created by orko on 15/08/14.
 */
@CompileStatic
class UserNotExistException extends RuntimeException{

    UserNotExistException(String username) {
        super(username)
    }
}
