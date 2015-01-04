package ar.com.bamboo.security

import ar.com.bamboo.commonsEntity.Person
import ar.com.bamboo.framework.domains.BaseEntity

class User extends BaseEntity{

    transient springSecurityService
    transient passwordNoEncoding

    String username
    String password
    boolean accountExpired
    boolean accountLocked
    boolean passwordExpired
    boolean acceptedTermCondition
    boolean accountVerified
    Date termConditionDateAccept
    Date dateAccountVerified
    Person person

    static transients = ['springSecurityService']

    static constraints = {
        username blank: false, unique: true, email: true
        password blank: false
        termConditionDateAccept nullable: true
        dateAccountVerified nullable: true
    }

    static mapping = {
        password column: '`password`'
        person fetch: 'join'
    }

    Set<Role> getAuthorities() {
        UserRole.findAllByUser(this).collect { it.role }
    }

    @Override
    protected void executeMoreBeforeInsert() {
        encodePassword()
        acceptedTermCondition = false
        accountVerified = false
    }

    def beforeUpdate() {
        if (isDirty('password')) {
            encodePassword()
        }
    }

    protected void encodePassword() {
        passwordNoEncoding = password
        password = springSecurityService?.passwordEncoder ? springSecurityService.encodePassword(password) : password
    }

    public String toString(){
        return username
    }

}