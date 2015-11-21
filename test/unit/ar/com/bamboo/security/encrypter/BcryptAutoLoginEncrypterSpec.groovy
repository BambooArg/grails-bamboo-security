package ar.com.bamboo.security.encrypter

import ar.com.bamboo.security.token.AutoLoginAuthenticationToken
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import spock.lang.Shared
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
class BcryptAutoLoginEncrypterSpec extends Specification {

    @Shared BcryptAutoLoginEncrypter bcryptAutoLoginEncrypter = new BcryptAutoLoginEncrypter()

    def setup() {

    }

    def cleanup() {
    }

    void "test encrypt and desencrypt"() {

        setup:
        UserDetails userDetails = new TestUserDetails(username: "mariano", password: "sarlanga")

        and: "the UserDetailsService is"

        def userDetailsService = mockFor(UserDetailsService)
        userDetailsService.demandExplicit.loadUserByUsername(1){ String username ->
            new TestUserDetails(username: username)
        }

        bcryptAutoLoginEncrypter.userDetailsService = userDetailsService.createMock()

        when: "Encrypt the userDetails "
        String hash = bcryptAutoLoginEncrypter.encrypt(userDetails)

        and: "Desencryt the same hash"
        AutoLoginAuthenticationToken authenticationToken = bcryptAutoLoginEncrypter.desEncrypt(hash)

        and: "is Valid encrypt"
        boolean isValidToken = bcryptAutoLoginEncrypter.isValidAutoLoginToken(authenticationToken.autoLoginToken, userDetails)

        then: "Get the same userDetails"
        authenticationToken.principal.username == userDetails.username
        isValidToken
    }

    class TestUserDetails implements UserDetails{

        Collection<? extends GrantedAuthority> authorities
        String password
        String username
        boolean accountNonExpired
        boolean accountNonLocked
        boolean credentialsNonExpired
        boolean enabled

    }
}
