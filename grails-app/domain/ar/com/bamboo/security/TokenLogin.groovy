package ar.com.bamboo.security

import ar.com.bamboo.framework.domains.BaseEntity

class TokenLogin extends BaseEntity{
    String token
    User user
    boolean expired

    static constraints = {
    }

    @Override
    protected void executeMoreBeforeInsert() {
        expired = false
    }
}
