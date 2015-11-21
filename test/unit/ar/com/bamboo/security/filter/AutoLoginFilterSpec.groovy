package ar.com.bamboo.security.filter

import ar.com.bamboo.security.services.AutoLoginService
import ar.com.bamboo.security.token.AutoLoginAuthenticationToken
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import org.codehaus.groovy.grails.plugins.testing.GrailsMockHttpServletRequest
import org.codehaus.groovy.grails.plugins.testing.GrailsMockHttpServletResponse
import org.springframework.mock.web.MockFilterChain
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.savedrequest.RequestCache
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
class AutoLoginFilterSpec extends Specification {

    AutoLoginFilter autoLoginFilter

    def setup() {
        autoLoginFilter = new AutoLoginFilter()
    }

    def cleanup() {
    }

    void "test doFilter success autologin without successhandler and eventPublisher"() {

        setup:
        def request = new GrailsMockHttpServletRequest()
        request.setParameter("autoLogin", "saraza")

        def securityContextImpl = null

        def securityContext = mockFor(SecurityContext)

        def securityContextMock = securityContext.createMock()

        securityContext.demandExplicit.setAuthentication(1){ Authentication authentication ->
            securityContextImpl = authentication
        }

        SecurityContextHolder.metaClass.'static'.getContext = { ->
            securityContextMock
        }

        def autoLoginService = mockFor(AutoLoginService)
        autoLoginService.demandExplicit.isAutoLogin(1){ HttpServletRequest request1 ->
            true
        }

        autoLoginService.demandExplicit.autoLogin(1){ HttpServletRequest request1 ->
            new AutoLoginAuthenticationToken("hash", "Principal", null)
        }

        def authenticationManager = mockFor(AuthenticationManager)
        authenticationManager.demandExplicit.authenticate(1){ Authentication authentication ->
            authentication
        }

        def requestCache = mockFor(RequestCache)
        def requestCacheStatus = null
        requestCache.demandExplicit.getRequest(1){HttpServletRequest request1, HttpServletResponse response2 ->
            null
        }

        requestCache.demandExplicit.saveRequest(1){HttpServletRequest request1, HttpServletResponse response2 ->
            requestCacheStatus = true
        }
        def event = null
        def successHandler = null

        when: "Filter whith autologin into the request"
        autoLoginFilter.requestCache = requestCache.createMock()
        autoLoginFilter.authenticationManager = authenticationManager.createMock()
        autoLoginFilter.autoLoginService = autoLoginService.createMock()

        autoLoginFilter.doFilter(request, new GrailsMockHttpServletResponse(), new MockFilterChain())

        then: "The user is login into securityContextImpl"
        requestCacheStatus
        securityContextImpl.autoLoginToken == "hash"
        !event
        !successHandler
    }

}
