/**
 * Created by orko on 04/01/16.
 */

//SECURITY
grails.plugin.springsecurity.userLookup.userDomainClassName = 'ar.com.bamboo.security.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'ar.com.bamboo.security.UserRole'
grails.plugin.springsecurity.authority.className = 'ar.com.bamboo.security.Role'
grails.plugin.springsecurity.controllerAnnotations.staticRules = [
        [pattern: '/',                  access: ['permitAll']],
        [pattern: '/index',             access: ['permitAll']],
        [pattern: '/index.gsp',         access: ['permitAll']],
        [pattern: '/assets/**',         access: ['permitAll']],
        [pattern: '/**/js/**',          access: ['permitAll']],
        [pattern: '/**/css/**',         access: ['permitAll']],
        [pattern: '/**/images/**',      access: ['permitAll']],
        [pattern: '/**/favicon.ico',    access: ['permitAll']]
]