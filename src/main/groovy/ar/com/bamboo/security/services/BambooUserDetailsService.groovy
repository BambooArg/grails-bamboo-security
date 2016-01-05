package ar.com.bamboo.security.services

import ar.com.bamboo.security.User
import ar.com.bamboo.security.UserService
import ar.com.bamboo.security.userDetails.BambooUserDetails
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.userdetails.GormUserDetailsService
import grails.plugin.springsecurity.userdetails.NoStackUsernameNotFoundException
import grails.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException

/**
 * Created by orko on 09/08/14.
 */
class BambooUserDetailsService extends GormUserDetailsService{

    @Autowired
    UserService userService

    @Override
    protected UserDetails createUserDetails(user, Collection<GrantedAuthority> authorities) {
        log.info("Creando BambooUserDetails para el usuario: ${user.username}")
        try{
            return new BambooUserDetails(user.username, user.password, user.enabled, !user.accountExpired,
                    !user.passwordExpired, !user.accountLocked, authorities, user.id, user.person.firstName ?: "",
                    user.person.lastName ?: "", user.accountVerified, user.acceptedTermCondition)
        }catch (Exception e){
            log.error("No se pudo crear BambooUserDetails: ${user.username}", e)
            throw new RuntimeException(e)
        }
    }

    @Override
    UserDetails loadUserByUsername(String username, boolean loadRoles) throws UsernameNotFoundException {
        User user = userService.getByUsername(username)
        if (!user) {
            log.warn "User not found: $username"
            throw new NoStackUsernameNotFoundException()
        }
        Collection<GrantedAuthority> authorities = loadAuthorities(user, username, loadRoles)
        createUserDetails user, authorities
    }

    @Override
    protected Collection<GrantedAuthority> loadAuthorities(user, String username, boolean loadRoles) {
        if (!loadRoles) {
            return []
        }

        def conf = SpringSecurityUtils.securityConfig
        boolean useGroups = conf.useRoleGroups
        String authorityGroupPropertyName = conf.authority.groupAuthorityNameField
        Collection<?> userAuthorities = userService.getRoleByUser(user)
        def authorities

        if (useGroups) {
            if (authorityGroupPropertyName) {
                authorities = userAuthorities.collect { it."$authorityGroupPropertyName" }.flatten().unique().collect { new SimpleGrantedAuthority(it."$authorityPropertyName") }
            }
            else {
                log.warn "Attempted to use group authorities, but the authority name field for the group class has not been defined."
            }
        }
        else {
            authorities = userAuthorities.collect { new SimpleGrantedAuthority(it.authority) }
        }
        authorities ?: [NO_ROLE]
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