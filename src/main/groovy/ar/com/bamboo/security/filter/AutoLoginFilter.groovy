package ar.com.bamboo.security.filter

import ar.com.bamboo.security.services.AutoLoginService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.savedrequest.RequestCache
import org.springframework.web.filter.GenericFilterBean

import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by orko on 24/10/15.
 */
class AutoLoginFilter extends GenericFilterBean {

    AuthenticationManager authenticationManager
    AutoLoginService autoLoginService
    boolean createSessionOnSuccess = true
    RequestCache requestCache
    @Autowired(required = false)
    ApplicationEventPublisher eventPublisher
    AuthenticationSuccessHandler successHandler

    public AutoLoginFilter(){
    }

    @Override
    public void afterPropertiesSet() {
        assert authenticationManager, "authenticationManager must be specified"
        assert autoLoginService, "autoLoginService must be specified"
        assert requestCache, "autoLoginService must be specified"
    }

    @Override
    void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (autoLoginService.isAutoLogin(request)){
            Authentication autoLoginAuth = autoLoginService.autoLogin(request);
            if (autoLoginAuth) {
                try {
                    autoLoginAuth = authenticationManager.authenticate(autoLoginAuth);

                    // Store to SecurityContextHolder
                    SecurityContextHolder.getContext().setAuthentication(autoLoginAuth);

                    onSuccessfulAuthentication(request, response, autoLoginAuth);

                    if (logger.isDebugEnabled()) {
                        logger.debug("SecurityContextHolder populated with autologin: '"
                                + SecurityContextHolder.getContext().getAuthentication() + "'");
                    }

                    // Fire event
                    if (this.eventPublisher != null) {
                        eventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(
                                SecurityContextHolder.getContext().getAuthentication(), this.getClass()));
                    }

                    if (successHandler != null) {
                        successHandler.onAuthenticationSuccess(request, response, autoLoginAuth);

                        return;
                    }

                } catch (AuthenticationException authenticationException) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("SecurityContextHolder not populated with autologin , as "
                                + "AuthenticationManager rejected Authentication returned by AutoLoginService: '"
                                + autoLoginAuth, authenticationException);
                    }
                }
            }else{
                if (logger.isDebugEnabled()) {
                    logger.debug("The autoLogin hash is incorrect. Cannot login user");
                }
            }
        }

        chain.doFilter(request, response);
    }

    def onSuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                   Authentication authentication) {
        if (!requestCache.getRequest(request, response)) {
            requestCache.saveRequest(request, response)
        }

        try {
            if (createSessionOnSuccess) {
                request.getSession(true);
            }
        }catch (IllegalStateException ignored) {
            // ignored
        }
    }

    public void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
}
