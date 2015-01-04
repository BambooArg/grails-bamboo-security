package ar.com.bamboo.security

import ar.com.bamboo.framework.BaseService
import ar.com.bamboo.framework.exceptions.ValidatorException
import ar.com.bamboo.security.exception.RoleNotExistException
import grails.gorm.DetachedCriteria
import grails.plugin.cache.Cacheable
import grails.transaction.Transactional
import org.apache.commons.lang.RandomStringUtils

class UserService extends BaseService{

    def grailsApplication
    def personService

    @Transactional
    public boolean save(User userTosave, String roleToAssign){
        if (userTosave.person && !userTosave.person.id ){
            log.debug("Guardando a la persona " + userTosave?.person?.firstName)
            personService.save(userTosave.person)
            if (userTosave.person.hasErrors()){
                throw new ValidatorException(model: userTosave.person)
            }
        }

        log.debug("Guardando al usuario " + userTosave?.username)
        //Genero el password aleatorio si es que no lo tiene
        if (!userTosave.password){
            userTosave.password = RandomStringUtils.randomAlphanumeric(8)
        }
        boolean isSave = grailsApplication.mainContext.baseService.save(userTosave)
        log.info("El usuario " + userTosave?.username + " se guardó bien? " + isSave)
        if (isSave && roleToAssign){
            Role role = Role.where { authority == roleToAssign }.get()
            if (role){
                log.debug("Se va a crear el rol ${role.authority} al ${userTosave?.username} ")
                UserRole userRole = UserRole.create(userTosave, role)
                if (userRole.hasErrors()){
                    throw new ValidatorException(errors: userRole.errors)
                }
                log.info("Se creó el role ${role.authority} al usuario ${userTosave?.username}")
            }else{
                throw new RoleNotExistException("No existe el rol " + roleToAssign)
            }
        }
        return isSave
    }

    @Transactional(readOnly = true)
    List<Object> list(Map params) {
        def where = { enabled == true } as DetachedCriteria<User>
        return this.listWithLimit(User.class, where, params)
    }

    @Transactional(readOnly = true)
    List<Object> listAll() {
        def where = { enabled == true } as DetachedCriteria<User>
        return this.listAll(User.class, where)
    }

    @Transactional(readOnly = true)
    List<User> listByRole(String roleArg) {
        def where = { role.authority == roleArg && user.enabled == true } as DetachedCriteria<UserRole>
        Map options = [projections: 'user']
        return this.listAll(UserRole.class, where, options)
    }

    @Transactional(readOnly = true)
    @Cacheable(value = 'default')
    User getByUsername(String usernameArg) {
        def where = { enabled == true && username == usernameArg} as DetachedCriteria<User>
        return this.getUnique(User.class, where)
    }

    @Transactional(readOnly = true)
    List<User> getByUsernameOrLastName(String usernameOrLastName) {
        StringBuilder hql = new StringBuilder(" FROM User user WHERE user.enabled = true AND (user.username LIKE :username ")
                .append(" OR user.person.lastName LIKE :lastName) ")

        Map<String, Object> parameters = new HashMap<String, Object>()
        parameters.username = usernameOrLastName + "%"
        parameters.lastName = usernameOrLastName + "%"


        return this.listAllHql(User.class, hql.toString(), parameters)
    }

}
