package ar.com.bamboo.security.encrypter

import ar.com.bamboo.security.token.AutoLoginAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails

/**
 * Created by orko on 24/10/15.
 */
interface AutoLoginEncrypter {

    String encrypt(UserDetails userDetails)

    AutoLoginAuthenticationToken desEncrypt(String encrypting)

    boolean isValidAutoLoginToken(String autoLoginToken, UserDetails userDetails)
}