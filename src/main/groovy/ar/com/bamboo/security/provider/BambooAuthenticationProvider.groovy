package ar.com.bamboo.security.provider

import ar.com.bamboo.security.userDetails.BambooUserDetails
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UserDetails

/**
 * Created by orko on 30/06/15.
 */
class BambooAuthenticationProvider extends DaoAuthenticationProvider{

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
                                                  UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        super.additionalAuthenticationChecks(userDetails, authentication)

        BambooUserDetails bambooUserDetails = (BambooUserDetails) userDetails
        if (!bambooUserDetails.accountVerified || !bambooUserDetails.acceptedTermCondition){
            throw new BadCredentialsException(this.messages.getMessage("BambooAuthenticationProvider.badCredentials",
                    "Bad credentials"));
        }
    }
}
