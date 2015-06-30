package ar.com.bamboo.security.userDetails

import grails.plugin.springsecurity.userdetails.GrailsUser
import groovy.transform.CompileStatic
import org.springframework.security.core.GrantedAuthority

/**
 * Created by orko on 09/08/14.
 */
@CompileStatic
class BambooUserDetails extends GrailsUser{

    final String fullName
    final boolean acceptedTermCondition
    final boolean accountVerified

    BambooUserDetails(String username, String password, boolean enabled, boolean accountNonExpired,
                      boolean credentialsNonExpired, boolean accountNonLocked, Collection<GrantedAuthority> authorities,
                      long id, String firstName, String lastName, boolean accountVerified, boolean acceptedTermCondition) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities, id)
        if (firstName && lastName){
            this.fullName = firstName + " " + lastName
        }else{
            if (firstName){
                this.fullName = firstName
            }else{
                this.fullName = lastName
            }
        }

        this.acceptedTermCondition = acceptedTermCondition
        this.accountVerified = accountVerified
    }
}
