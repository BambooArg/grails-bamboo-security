package ar.com.bamboo.security.encrypter

import ar.com.bamboo.security.token.AutoLoginAuthenticationToken
import grails.plugin.springsecurity.authentication.encoding.BCryptPasswordEncoder
import org.apache.commons.codec.binary.Base64
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService

/**
 * Created by orko on 24/10/15.
 */
class BcryptAutoLoginEncrypter implements AutoLoginEncrypter, InitializingBean{

    static final Logger LOG = LoggerFactory.getLogger(BcryptAutoLoginEncrypter.name)
    static final Integer LOG_ROUND = 15

    BCryptPasswordEncoder bCryptPasswordEncoder
    UserDetailsService userDetailsService
    GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper()
    String key

    BcryptAutoLoginEncrypter(){
        bCryptPasswordEncoder = new BCryptPasswordEncoder(LOG_ROUND)
    }

    @Override
    void afterPropertiesSet() throws Exception {
        assert bCryptPasswordEncoder, "bCryptPasswordEncoder must be specified"
        assert userDetailsService, "userDetailsService must be specified"

        if (!key){
            key = "AUTOLOGIN_KEY"
        }
    }

    @Override
    String encrypt(UserDetails userDetails) {
        LOG.debug("Encriptando para autologin al usuario")
        String phraseToEncrypt = enhancePhrase(userDetails)
        String autoLoginToken =  bCryptPasswordEncoder.encodePassword(phraseToEncrypt, null)
        String autologinHash = userDetails.username + ":" + autoLoginToken
        new String(Base64.encodeBase64(autologinHash.bytes))
    }

    private String enhancePhrase(UserDetails userDetails) {
        key + ":" + userDetails.password
    }

    @Override
    AutoLoginAuthenticationToken desEncrypt(String encrypting) {
        String decodedAutoLoginHash = new String(Base64.decodeBase64(encrypting))
        String[] tokens = decodedAutoLoginHash.split(":")
        if (tokens.size() != 2){
            LOG.error("La frase encriptada tiene tamaño erroneo")
            throw new BCryptAutoLoginException("Encrypted phrase error parse")
        }
        def (String username, String autoLoginToken) = tokens
        UserDetails userDetails = userDetailsService.loadUserByUsername(username)
        new AutoLoginAuthenticationToken(autoLoginToken, userDetails, authoritiesMapper
                .mapAuthorities(userDetails.getAuthorities()))
    }

    boolean isValidAutoLoginToken(String autoLoginToken, UserDetails userDetails){
        String phraseToEncrypt = enhancePhrase(userDetails)
        bCryptPasswordEncoder.isPasswordValid(autoLoginToken, phraseToEncrypt, null)
    }

}
