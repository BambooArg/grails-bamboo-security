package ar.com.bamboo.security

import ar.com.bamboo.framework.domains.BaseEntity

class User extends BaseEntity{

    transient springSecurityService

    String username
    String password
    String firstName
    String lastName
    boolean accountExpired
    boolean accountLocked
    boolean passwordExpired

    static transients = ['springSecurityService']

    static constraints = {
        username blank: false, unique: true, email: true
        password blank: false
        firstName blank: true, nullable: true
        lastName blank: true, nullable: true
    }

    static mapping = {
        password column: '`password`'
    }

    Set<Role> getAuthorities() {
        UserRole.findAllByUser(this).collect { it.role }
    }

    @Override
    protected void executeMoreBeforeInsert() {
        encodePassword()
    }

    def beforeUpdate() {
        if (isDirty('password')) {
            encodePassword()
        }
    }

    protected void encodePassword() {
        password = springSecurityService?.passwordEncoder ? springSecurityService.encodePassword(password) : password
    }

}
