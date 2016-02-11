package ar.com.bamboo.security

import ar.com.bamboo.framework.domains.BaseEntity

class TokenLogin implements BaseEntity{
    String token
    User user
    boolean expired

    static constraints = {
    }

}
