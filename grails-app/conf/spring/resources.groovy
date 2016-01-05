import ar.com.bamboo.security.services.BambooUserDetailsService

// Place your Spring DSL code here
beans = {

   userDetailsService(BambooUserDetailsService){
        grailsApplication = ref('grailsApplication')
    }
}