package ar.com.bamboo.security

import ar.com.bamboo.commonsEntity.Person
import ar.com.bamboo.framework.exceptions.ValidatorException
import ar.com.bamboo.security.exception.RoleNotExistException
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import org.springframework.dao.DuplicateKeyException
import spock.lang.Specification

/**
 *
 */
@Integration
@Rollback
class UserServiceIntegrationSpec extends Specification {

    def userService

    Person person, person2


    def setupData() {
        new Role(authority: Role.ROLE_SUPERUSER).save(flush: true, failOnError: true)
        new Role(authority: "ROLE_ROLE2").save(flush: true, failOnError: true)
        person = new Person(firstName: "Mariano").save(flush: true, failOnError: true)
        person2 = new Person(firstName: "Mariano Alberto").save(flush: true, failOnError: true)
    }

    def cleanup() {
    }

    void "test save action without role"() {
        setup:
        setupData()

        when: "Intento guardar un usuario sin role pero con errores de constraint"
        User user = new User()
        then: "La acción devuelve false y no guarda"
        !userService.save(user)
        !user.id
        user.hasErrors()

        when: "Intento guardar un usuario sin roles pero sin errores de constraint"
        user = new User(username: "bamboo@gmail.com", password: "quedificil", person: person)
        then: "La acción devuelve true y guarda"
        userService.save(user)
        user.id
        !user.hasErrors()

        when: "Intento guardar un usuario que ya existe"
        user = new User(username: "bamboo@gmail.com", password: "quedificil", person: person)
        then: "La acción devuelve false"
        !userService.save(user)
        !user.id
        user.hasErrors()

    }

    void "test save action with role"() {
        setup:
        setupData()

        when: "Intento guardar un usuario con role pero con errores de constraint"
        User user = new User()
        userService.createUser(user, Role.ROLE_SUPERUSER)

        then: "La acción devuelve false y no guarda"
        thrown(ValidatorException)
        !user.id
        user.hasErrors()


        when: "Intento guardar un usuario con roles y con todas las constraint cargadas"
        user = new User(username: "bamboo@gmail.com", password: "quedificil", person: person)
        Role role = Role.where { authority == Role.ROLE_SUPERUSER }.get()

        then: "La acción devuelve true y guarda"
        role
        role.authority == Role.ROLE_SUPERUSER
        userService.createUser(user, Role.ROLE_SUPERUSER)
        user.id
        !user.hasErrors()

        when: "Intento asignarle el rol de superuser de nuevo a la misma person"
        userService.createUser(user, Role.ROLE_SUPERUSER)

        then: "Se arroja una exception"
        thrown(DuplicateKeyException)


        when: "Intento guardar un usuario repetido"
        user = new User(username: "bamboo@gmail.com", password: "quedificil", person: person)
        userService.createUser(user, Role.ROLE_SUPERUSER)

        then: "La acción devuelve true y guarda"
        thrown(ValidatorException.class)
        !user.id
        user.hasErrors()

        when: "Cuando registro a un usuario con un rol que no existe"
        user = new User(username: "bambo00o@gmail.com", password: "quedificil", person: person)
        userService.createUser(user, "ROLE_NO_EXISTE")
        then: "La acción arroja exception y no guarda nada"
        thrown(RoleNotExistException)
    }

    void "test edit action"() {
        setup:
        setupData()
        User userToEdit = new User(username: "bamboo@gmail.com", password: "quedificil", person: person)
        userService.save(userToEdit)

        when: "Modifico los campos obligatorios para probar que al modificar falles"
        userToEdit.username = ""
        userToEdit.password = ""
        boolean success = userService.save(userToEdit)
        userToEdit.discard()
        User userDB = User.get(userToEdit.id)

        then: "No se guarda la acción"
        !success
        userToEdit.id
        userToEdit.hasErrors()
        userDB.username == "bamboo@gmail.com"


        when: "Modifico los datos del usuario y se persisten"
        userDB.discard()
        userToEdit.username = "josesito@gmail.com"
        userToEdit.password = "quedificil2"
        userToEdit.person = person2
        success = userService.save(userToEdit)
        userDB = User.get(userToEdit.id)

        then: "La acción devuelve true y guarda"
        success
        userDB.username == userToEdit.username
        userDB.person.id == person2.id


        when: "Intengo agregarle otro rol al usuario, además le modifico datos"
        userDB.discard()
        userToEdit.username = "josesito2@gmail.com"
        success = userService.createUser(userToEdit, "ROLE_ROLE2")
        userDB = User.get(userToEdit.id)

        then: "La acción devuelve true y guarda"
        success
        userDB.username == userToEdit.username


        when: "Intengo agregarle otro rol al usuario, pero ya lo tiene"
        userDB.discard()
        userToEdit.username = "josesito3@gmail.com"
        userService.createUser(userToEdit, "ROLE_ROLE2")

        then: "Se hace rollback y no se guarda nada"
        thrown(DuplicateKeyException)
    }

    void "test listByRole method"() {
        setup:
        setupData()
        User user = new User(username: "gg@gmail.com", password: "password", person: person).save(flush: true, failOnError: true)
        UserRole.create(user, Role.findByAuthority(Role.ROLE_SUPERUSER), true)


        when: "Cuando busco los usuario que tienen un rol"
        List<User> users = userService.listByRole(Role.ROLE_SUPERUSER)

        then: "El resultado es una lista de usuarios"
        users != null
        users
        users.size() >= 1
        users[0].id
    }
}
