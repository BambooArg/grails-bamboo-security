package ar.com.bamboo.security.service

import ar.com.bamboo.security.encrypter.AutoLoginEncrypter
import ar.com.bamboo.security.services.AutoLoginService
import ar.com.bamboo.security.token.AutoLoginAuthenticationToken
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import org.codehaus.groovy.grails.plugins.testing.GrailsMockHttpServletRequest
import org.springframework.security.authentication.AuthenticationDetailsSource
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.WebAuthenticationDetails
import spock.lang.Shared
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
class AutoLoginServiceSpec extends Specification {

    @Shared AutoLoginService autoLoginService = new AutoLoginService()

    def setup() {
    }

    def cleanup() {
    }

    void "test isAutoLogin success"() {
        setup:
        def request = new GrailsMockHttpServletRequest()
        request.setParameter("autoLogin", "saraza")

        when: "Test if the request is autoLogin"
        boolean isAutoLogin = autoLoginService.isAutoLogin(request)

        then: "The request is autologin"
        isAutoLogin

    }

    void "test isAutoLogin fail"() {
        setup:
        def request = new GrailsMockHttpServletRequest()
        request.setParameter("autoLoginss", "saraza")

        when: "Test if the request is autoLogin"
        boolean isAutoLogin = autoLoginService.isAutoLogin(request)

        then: "The request is autologin"
        !isAutoLogin
    }

    void "test autoLogin method"() {
        setup:
        def request = new GrailsMockHttpServletRequest()
        request.setParameter("autoLogin", "saraza")
        request.setRemoteAddr("123.456.778")

        def autoLoginEncrypter = mockFor(AutoLoginEncrypter)
        autoLoginEncrypter.demandExplicit.desEncrypt(1){ String hash ->
            new AutoLoginAuthenticationToken("hash", "Principal", null)
        }

        def authenticationDetailsSource = mockFor(AuthenticationDetailsSource)
        authenticationDetailsSource.demandExplicit.buildDetails(1) {HttpServletRequest request1 ->
            new WebAuthenticationDetails(request1)
        }

        when: "call the autologin method"
        autoLoginService.autoLoginEncrypter = autoLoginEncrypter.createMock()
        autoLoginService.authenticationDetailsSource = authenticationDetailsSource.createMock()
        Authentication authentication = autoLoginService.autoLogin(request)

        then: "The authentication isAssinable from AutoLoginAuthenticationToken"
        AutoLoginAuthenticationToken.class.isAssignableFrom(authentication.class)
        authentication.details.remoteAddress =="123.456.778"

    }
}
