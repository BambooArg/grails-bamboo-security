package ar.com.bamboo.security

import ar.com.bamboo.commonsEntity.Person
import ar.com.bamboo.security.exception.RoleNotExistException
import grails.plugin.springsecurity.SpringSecurityService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.springframework.dao.DuplicateKeyException
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(UserService)
@Mock([User, Role, UserRole])
class UserServiceSpec extends Specification {



    def setup() {
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
        def springSecurityService = mockFor(SpringSecurityService)
        springSecurityService.demandExplicit.encodePassword(){String  password ->
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
        def params = [max: 5]

        when: "Cuando se busca con un maximo de 5"
        def  (List<User> listResult, Integer countResult) = service.list(params)

        then: "El resultado de es el maximos para el listResult y el total para countResult"
        listResult.size() == 5
        countResult == 10
    }


    /*   void "test save action with role"() {
           given:
           def springSecurityService = mockFor(SpringSecurityService)
           springSecurityService.demandExplicit.encodePassword(){String  password ->
               return password
           }
           Person p = new Person(firstName: "Mariano")

           when: "Cuando registro usuario sin los datos oblogatorios"
           User user = new User()
           then: "El registro de usuario retorna false"
           !service.save(user, Role.ROLE_SUPERUSER)
           user.hasErrors()
           !user.id

           when: "Cuando registro usuario con los datos obligatorios"
           user = new User(username: "bamboo@gmail.com", password: "quedificil", person: p)
           then: "El registro de usuario retorna true y el usuario tiene id"
           service.save(user, Role.ROLE_SUPERUSER)
           !user.hasErrors()
           user.id
           user.getAuthorities()
           UserRole.exists(user.id, 1)

           when: "Cuando registro usuario repetido"
           user = new User(username: "bamboo@gmail.com", password: "quedificil", person: p)
           then: "El registro de usuario retorna false"
           !service.save(user, Role.ROLE_SUPERUSER)
           user.hasErrors()
           !user.id

           when: "Cuando regitro a un usuario con un rol que no existe"
           user = new User(username: "bambo00o@gmail.com", password: "quedificil", person: p)
           service.save(user, "ROLE_NO_EXISTE")
           then: "El registro de usuario retorna false"
           thrown(RoleNotExistException)
       }

       void "test edit action"(){
           given:
           def springSecurityService = mockFor(SpringSecurityService)
           springSecurityService.demandExplicit.encodePassword(){String  password ->
               return password
           }
           Person p = new Person(firstName: "Mariano")
           User userToEdit = new User(username: "bamboo@gmail.com", password: "password", person: p)
                   .save(flush: true, failOnError: true)

           when: "Cuando lo edito modificando un dato oblogatorio dejandolo sin ser obligatorio"
           userToEdit.username = ''
           userToEdit.password = ''
           userToEdit.person = null
           boolean isSave = service.save(userToEdit)
           then: "El update del usuario retorna false"
           !isSave
           userToEdit.hasErrors()

           when: "Cuando lo edito modificando dejando los datos obligatorios"
           userToEdit.username = 'alberto@gmail.com'
           userToEdit.password = 'superpassword'
           userToEdit.person = p
           isSave =  service.save(userToEdit)
           then: "El update del usuario retorna true"
           isSave
           !userToEdit.hasErrors()
       }

       void "test agregar nuevo rol a usuario con un rol"() {
           given:
           def springSecurityService = mockFor(SpringSecurityService)
           springSecurityService.demandExplicit.encodePassword(){String  password ->
               return password
           }
           Person p = new Person(firstName: "Mariano")
           User userWithRol = new User(username: "bamboo@gmail.com", password: "password", person: p)
           service.save(userWithRol, Role.ROLE_SUPERUSER)

           when: "Cuando se quiere agregar un nuevo rol a un usuario"
           boolean success = service.save(userWithRol, "ROLE_ROLE2")
           then: "SE agrega correctamente el rol"
           success
           !userWithRol.hasErrors()

           when: "Cuando se quiere agregar un nuevo rol a un usuario y además modificar datos del usuario"
           userWithRol.username = 'alberto@gmail.com'
           success = service.save(userWithRol, "ROLE_ROLE3")
           then: "El registro de usuario retorna false"
           success
           !userWithRol.hasErrors()

       }*/
}
