package ar.com.bamboo.security.token

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

/**
 * Created by orko on 24/10/15.
 */
class AutoLoginAuthenticationToken extends AbstractAuthenticationToken{

    final Object principal
    final String autoLoginToken

    /**
     * Creates a token with the supplied array of authorities.
     *
     * @param authorities the collection of <tt>GrantedAuthority</tt>s for the
     *                    principal represented by this authentication object.
     */
    AutoLoginAuthenticationToken(String autoLoginToken, Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities)
        this.autoLoginToken = autoLoginToken
        this.principal = principal
    }

    @Override
    Object getCredentials() {
        return ""
    }

    @Override
    Object getPrincipal() {
        return principal
    }
}
