package ar.com.bamboo.security

import ar.com.bamboo.commons.exception.BusinessValidator
import ar.com.bamboo.commonsEntity.Person
import ar.com.bamboo.framework.exceptions.ValidatorException
import ar.com.bamboo.framework.persistence.PaginatedResult
import grails.buildtestdata.mixin.Build
import grails.plugin.springsecurity.SpringSecurityService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.springframework.security.authentication.encoding.PasswordEncoder
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(UserService)
@Build(User)
@Mock([User, Role, UserRole])
class UserServiceSpec extends Specification {

    def springSecurityService = Mock(SpringSecurityService.class)

    def passwordEncoder = Mock(PasswordEncoder.class)

    def userService = Mock(UserService.class)

    def setup(){
        new Role(authority: Role.ROLE_SUPERUSER).save(flush: true, failOnError: true)
        new Role(authority: "ROLE_ROLE2").save(flush: true, failOnError: true)
        new Role(authority: "ROLE_ROLE3").save(flush: true, failOnError: true)

        //Guardo varios usuarios para poder hacer el test luego
        Person p = new Person()
        for ( i in 1..20 ){
            User user = new User(username: "bamboo${i}@gmail.com", password: "dasdasdasd", person: p).save(flush: true, failOnError: true)
            if ( (i % 2) == 0){
                user.enabled = false
                user.save(flush: true, failOnError: true)
            }
        }
    }

    def cleanup() {
    }

    void "test save action without role"() {
        given:
        def springSecurityService = Mock(SpringSecurityService)
        springSecurityService.encodePassword(_) >> {String  password ->
            return password
        }
        Person p = new Person(firstName: "Mariano")


        when: "Cuando registro usuario sin los datos oblogatorios"
        User user = new User()

        then: "El registro de usuario retorna false"
        !service.save(user)
        user.hasErrors()
        !user.id


        when: "Cuando registro usuario con los datos obligatorios"
        user = new User(username: "bamboo@gmail.com", password: "quedificil", person: p)

        then: "El registro de usuario retorna true y el usuario tiene id"
        service.save(user)
        !user.hasErrors()
        user.id


        when: "Cuando registro usuario repetido"
        user = new User(username: "bamboo@gmail.com", password: "quedificil", person: p)

        then: "El registro de usuario retorna false"
        !service.save(user)
        user.hasErrors()
        !user.id
    }

    void "test list action"(){
        setup:
        def params = [max: 5]

        when: "Cuando se busca con un maximo de 5"
        PaginatedResult result = service.list(params)

        then: "El resultado de es el maximos para el listResult y el total para countResult"
        result
        result.result.size() == 5
        result.totalRows == 10
    }

    void "test changePassword method fail old password invalid"(){
        setup:
        passwordEncoder.isPasswordValid(_, _, _) >> { String hashedPassword, String plainPassword, Object salt ->
            false
        }
        def passwordEncoderMocked = passwordEncoder
        springSecurityService.getPasswordEncoder() >> {
            passwordEncoderMocked
        }

        and:
        User user = new User(password: "passwordHashed")

        and:
        service.springSecurityService = springSecurityService

        when: "Se intenta modificar el password con la contraseña actual erronea"
        service.changePassword(user, "viejaPassword", "nuevaPassword")

        then: "BusinessValidator is thrown"
        BusinessValidator e = thrown(BusinessValidator.class)
        e.message == "La contraseña actual no es correcta."
    }

    void "test changePassword method fail same password"(){
        setup:
        passwordEncoder.isPasswordValid(_, _, _) >> { String hashedPassword, String plainPassword, Object salt ->
            plainPassword == 'mismaPassword'
        }
        def passwordEncoderMocked = passwordEncoder
        springSecurityService.getPasswordEncoder() >> {
            passwordEncoderMocked
        }

        and:
        User user = new User(password: "invalida")

        and:
        service.springSecurityService = springSecurityService

        when: "Se intenta modificar el password, con la misma password actual"
        service.changePassword(user, "mismaPassword", "mismaPassword")

        then: "BusinessValidator is thrown"
        BusinessValidator e = thrown(BusinessValidator.class)
        e.message == "La contraseña debe ser diferente a la actual."
    }

    void "test changePassword method fail because invalid user"(){
        setup:
        passwordEncoder.isPasswordValid(_, _, _) >> { String hashedPassword, String plainPassword, Object salt ->
            if (plainPassword == "viejaPassword"){
                return true
            }else if (plainPassword == "nuevaPassword"){
                return false
            }
        }
        def passwordEncoderMocked = passwordEncoder
        springSecurityService.getPasswordEncoder() >> {
            passwordEncoderMocked
        }

        and:
        User user = new User(password: "passwordHashed")

        and:
        service.springSecurityService = springSecurityService

        when: "Se intenta modificar el password con un usuario inválido"
        service.changePassword(user, "viejaPassword", "nuevaPassword")

        then: "BusinessValidator is thrown"
        ValidatorException e = thrown(ValidatorException.class)
        e.model
        e.model.is(user)
    }

    void "test changePassword method success"(){
        setup:
        passwordEncoder.isPasswordValid(_, _, _) >> { String hashedPassword, String plainPassword, Object salt ->
            if (plainPassword == "viejaPassword"){
                return true
            }else if (plainPassword == "nuevaPassword"){
                return false
            }
        }

        def passwordEncoderMocked = passwordEncoder
        springSecurityService.getPasswordEncoder() >> {
            passwordEncoderMocked
        }

        userService.expiresOldTokenLoginByUser(_) >> {User user ->

        }
        def userServiceMock = userService

        and:
        User user = User.build()

        and:
        service.springSecurityService = springSecurityService
        service.metaClass.getProxyUserService = { ->
            userServiceMock
        }

        when: "Se intenta modificar el password con todos los datos correctos"
        service.changePassword(user, "viejaPassword", "nuevaPassword")

        then: "La validación es correcta"
        user.password == "nuevaPassword"
    }
}
