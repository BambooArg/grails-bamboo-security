package ar.com.bamboo.security.exception

import groovy.transform.CompileStatic

/**
 * Created by orko on 15/08/14.
 */
@CompileStatic
class RoleNotExistException extends RuntimeException{

    RoleNotExistException(String s) {
        super(s)
    }
}
