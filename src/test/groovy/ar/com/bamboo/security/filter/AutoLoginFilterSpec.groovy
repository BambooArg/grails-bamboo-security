package ar.com.bamboo.security.filter

import ar.com.bamboo.security.services.AutoLoginService
import ar.com.bamboo.security.token.AutoLoginAuthenticationToken
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import org.grails.plugins.testing.GrailsMockHttpServletRequest
import org.grails.plugins.testing.GrailsMockHttpServletResponse
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

        def securityContext = Mock(SecurityContext)

        def securityContextMock = securityContext

        securityContext.setAuthentication(_) >> { Authentication authentication ->
            securityContextImpl = authentication
        }

        SecurityContextHolder.metaClass.'static'.getContext = { ->
            securityContextMock
        }

        def autoLoginService = Mock(AutoLoginService)
        autoLoginService.isAutoLogin(_) >> { HttpServletRequest request1 ->
            true
        }

        autoLoginService.autoLogin(_) >> { HttpServletRequest request1 ->
            new AutoLoginAuthenticationToken("hash", "Principal", null)
        }

        def authenticationManager = Mock(AuthenticationManager)
        authenticationManager.authenticate(_) >> { Authentication authentication ->
            authentication
        }

        def requestCache = Mock(RequestCache)
        def requestCacheStatus = null
        requestCache.getRequest(_, _) >> { HttpServletRequest request1, HttpServletResponse response2 ->
            null
        }

        requestCache.saveRequest(_, _) >> { HttpServletRequest request1, HttpServletResponse response2 ->
            requestCacheStatus = true
        }
        def event = null
        def successHandler = null

        when: "Filter whith autologin into the request"
        autoLoginFilter.requestCache = requestCache
        autoLoginFilter.authenticationManager = authenticationManager
        autoLoginFilter.autoLoginService = autoLoginService

        autoLoginFilter.doFilter(request, new GrailsMockHttpServletResponse(), new MockFilterChain())

        then: "The user is login into securityContextImpl"
        requestCacheStatus
        securityContextImpl.autoLoginToken == "hash"
        !event
        !successHandler
    }

}
