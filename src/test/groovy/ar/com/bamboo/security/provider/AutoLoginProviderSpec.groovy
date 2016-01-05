package ar.com.bamboo.security.provider

import ar.com.bamboo.security.services.AutoLoginService
import ar.com.bamboo.security.token.AutoLoginAuthenticationToken
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
class AutoLoginProviderSpec extends Specification {

    AutoLoginProvider autoLoginProvider

    def setup() {
        autoLoginProvider = new AutoLoginProvider()
    }

    def cleanup() {
    }

    void "test authenticate success"() {
        setup:
        def autoLoginService = Mock(AutoLoginService)
        autoLoginService.isValidAuthentication(_) >> { AutoLoginAuthenticationToken autoLoginAuthenticationToken ->
            true
        }

        AutoLoginAuthenticationToken autoLoginAuthenticationToken = new AutoLoginAuthenticationToken("hash", "principal", null)

        when: "When then autologin is success"
        autoLoginProvider.autoLoginService = autoLoginService
        autoLoginAuthenticationToken = autoLoginProvider.authenticate(autoLoginAuthenticationToken)

        then: "the login is success and autoLoginAuthenticationToken is authenticated"
        autoLoginAuthenticationToken.authenticated

    }

    void "test authenticate fail"() {
        setup:
        def autoLoginService = Mock(AutoLoginService)
        autoLoginService.isValidAuthentication(_) >> { AutoLoginAuthenticationToken autoLoginAuthenticationToken ->
            false
        }

        AutoLoginAuthenticationToken autoLoginAuthenticationToken = new AutoLoginAuthenticationToken("hash", "principal", null)

        when: "When then autologin is fail"
        autoLoginProvider.autoLoginService = autoLoginService
        autoLoginAuthenticationToken = autoLoginProvider.authenticate(autoLoginAuthenticationToken)

        then: "autoLoginAuthenticationToken is null"
        !autoLoginAuthenticationToken

    }
}
