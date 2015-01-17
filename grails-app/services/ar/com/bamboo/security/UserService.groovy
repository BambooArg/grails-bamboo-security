package ar.com.bamboo.security

import ar.com.bamboo.framework.BaseService
import ar.com.bamboo.framework.exceptions.ValidatorException
import ar.com.bamboo.security.exception.RoleNotExistException
import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import grails.util.Environment
import org.apache.commons.lang.RandomStringUtils
import org.springframework.cache.annotation.Cacheable

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
        if (Environment.currentEnvironment == Environment.PRODUCTION){
            userTosave.password = RandomStringUtils.randomAlphanumeric(8)
        }else{
            userTosave.password = 'pass'
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
    User getByUsername(String username) {
        Long id = grailsApplication.mainContext.userService.getIdByUsername(username)
        return id ? User.get(id) : null
    }

    @Transactional
    @Cacheable(value = "default-cache", key = "#username", unless="#result == null")
    Long getIdByUsername(String username){
        StringBuilder hql = new StringBuilder(" SELECT u.id FROM User u ")
                .append(" WHERE u.enabled = true and u.username = :username ")
        Map parameters = [username: username]
        return this.getUnique(User.class, hql.toString(), parameters)
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

    @Transactional(readOnly = true)
    List<Role> getRoleByUser(User user) {
        List<Long> idsRole = grailsApplication.mainContext.userService.getIdRoleByUser(user)
        return this.loadById(Role.class, idsRole)
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "default-cache", key = "#user.id")
    List<Long> getIdRoleByUser(User user) {
        StringBuilder hql = new StringBuilder(" SELECT ur.role.id  FROM UserRole ur ")
                .append(" WHERE ur.user = :user ")

        Map<String, Object> parameters = new HashMap<String, Object>()
        parameters.user = user
        return UserRole.executeQuery(hql.toString(), parameters)
    }
}