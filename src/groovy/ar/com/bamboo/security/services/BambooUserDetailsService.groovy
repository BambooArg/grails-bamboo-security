package ar.com.bamboo.security.services

import ar.com.bamboo.security.User
import ar.com.bamboo.security.userDetails.BambooUserDetails
import grails.plugin.springsecurity.userdetails.GormUserDetailsService
import grails.plugin.springsecurity.userdetails.NoStackUsernameNotFoundException
import grails.transaction.Transactional
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException

/**
 * Created by orko on 09/08/14.
 */
class BambooUserDetailsService extends GormUserDetailsService{


    @Override
    protected UserDetails createUserDetails(user, Collection<GrantedAuthority> authorities) {
        log.info("Creando BambooUserDatails para el usuario: ${user.username}")
        return new BambooUserDetails(user.username, user.password, user.enabled, !user.accountExpired,
                !user.passwordExpired, !user.accountLocked, authorities, user.id, user.person.firstName ?: "",
                user.person.lastName ?: "")
    }

    @Override
    @Transactional(readOnly = true)
    UserDetails loadUserByUsername(String username, boolean loadRoles) throws UsernameNotFoundException {
        User user = grailsApplication.mainContext.userService.getByUsername(username)
        if (!user) {
            log.warn "User not found: $username"
            throw new NoStackUsernameNotFoundException()
        }
        Collection<GrantedAuthority> authorities = loadAuthorities(user, username, loadRoles)
        createUserDetails user, authorities
    }

    /**
     * {@inheritDoc}
     * @see org.springframework.security.core.userdetails.UserDetailsService#loadUserByUsername(
     * 	java.lang.String)
     */
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        loadUserByUsername username, true
    }
}
