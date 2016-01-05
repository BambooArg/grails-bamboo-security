package ar.com.bamboo.security.provider

import ar.com.bamboo.security.services.AutoLoginService
import ar.com.bamboo.security.token.AutoLoginAuthenticationToken
import org.springframework.beans.factory.InitializingBean
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException

/**
 * Created by orko on 24/10/15.
 */
class AutoLoginProvider implements AuthenticationProvider, InitializingBean {

    AutoLoginService autoLoginService

    @Override
    void afterPropertiesSet() throws Exception {
        assert autoLoginService, "autoLoginService must be specified"
    }

    @Override
    Authentication authenticate(Authentication authentication) throws AuthenticationException {
        AutoLoginAuthenticationToken autoLoginAuthenticationToken = (AutoLoginAuthenticationToken)authentication
        if (autoLoginService.isValidAuthentication(autoLoginAuthenticationToken)){
            autoLoginAuthenticationToken.authenticated = true
            return authentication
        }else{
            return null
        }
    }

    @Override
    boolean supports(Class<?> authentication) {
        (AutoLoginAuthenticationToken.class.isAssignableFrom(authentication));
    }

    /**
     * Copies the authentication details from a source Authentication object to a destination one, provided the
     * latter does not already have one set.
     *
     * @param source source authentication
     * @param dest the destination authentication object
     */
    private void copyDetails(Authentication source, Authentication dest) {
        if ((dest instanceof AbstractAuthenticationToken) && (dest.getDetails() == null)) {
            AbstractAuthenticationToken token = (AbstractAuthenticationToken) dest;

            token.setDetails(source.getDetails());
        }
    }

}
