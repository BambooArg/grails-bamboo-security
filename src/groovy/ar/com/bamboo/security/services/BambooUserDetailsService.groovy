package ar.com.bamboo.security.services

import ar.com.bamboo.security.userDetails.BambooUserDetails
import grails.plugin.springsecurity.userdetails.GormUserDetailsService
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

/**
 * Created by orko on 09/08/14.
 */
class BambooUserDetailsService extends GormUserDetailsService{


    @Override
    protected UserDetails createUserDetails(user, Collection<GrantedAuthority> authorities) {
        log.info("Creando BambooUserDatails para el usuario: ${user.username}")
        return new BambooUserDetails(user.username, user.password, user.enabled, !user.accountExpired,
                !user.passwordExpired, !user.accountLocked, authorities, user.id, user.firstName, user.lastName)
    }
}
