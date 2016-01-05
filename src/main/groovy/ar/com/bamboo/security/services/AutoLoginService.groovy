package ar.com.bamboo.security.services

import ar.com.bamboo.security.User
import ar.com.bamboo.security.encrypter.AutoLoginEncrypter
import ar.com.bamboo.security.token.AutoLoginAuthenticationToken
import grails.plugin.springsecurity.userdetails.GrailsUserDetailsService
import org.springframework.beans.factory.InitializingBean
import org.springframework.security.authentication.AuthenticationDetailsSource
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails

import javax.servlet.http.HttpServletRequest

/**
 * Created by orko on 25/10/15.
 */
class AutoLoginService implements InitializingBean{

    public static final String PARAM_NAME = "autoLogin"
    AutoLoginEncrypter autoLoginEncrypter
    GrailsUserDetailsService userDetailsService
    AuthenticationDetailsSource authenticationDetailsSource

    @Override
    void afterPropertiesSet() throws Exception {
        assert autoLoginEncrypter, "autoLoginEncrypter must be specified"
        assert userDetailsService, "userDetailsService must be specified"
        assert authenticationDetailsSource, "authenticationDetailsSource must be specified"
    }

    boolean isAutoLogin(HttpServletRequest request){
        getAutoLoginParam(request)
    }

    private String getAutoLoginParam(HttpServletRequest request) {
        request.getParameter(PARAM_NAME)
    }

    Authentication autoLogin(HttpServletRequest request) {
        String autoLoginHash = getAutoLoginParam(request)
        AutoLoginAuthenticationToken authentication = autoLoginEncrypter.desEncrypt(autoLoginHash)
        if (authentication){
            authentication.setDetails(authenticationDetailsSource.buildDetails(request));
        }
        return authentication
    }

    boolean isValidAuthentication(AutoLoginAuthenticationToken token){
        autoLoginEncrypter.isValidAutoLoginToken(token.autoLoginToken, token.principal)
    }

    String generateAutoLogin(User user){
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.username)
        autoLoginEncrypter.encrypt(userDetails)
    }


}
