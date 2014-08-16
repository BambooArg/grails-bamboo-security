package ar.com.bamboo.security

import ar.com.bamboo.commonsEntity.Person
import ar.com.bamboo.framework.BaseService
import ar.com.bamboo.framework.exceptions.ValidatorException
import ar.com.bamboo.security.exception.RoleNotExistException
import grails.gorm.DetachedCriteria
import grails.transaction.Transactional

class UserService extends BaseService{

    def grailsApplication

    @Transactional
    public boolean save(User userTosave, String roleToAssign){
        log.debug("Guardando al usuario " + userTosave?.username)
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

}
