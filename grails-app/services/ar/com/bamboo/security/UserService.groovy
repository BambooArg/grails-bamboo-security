package ar.com.bamboo.security

import ar.com.bamboo.framework.BaseService
import ar.com.bamboo.security.exception.RoleNotExistException
import grails.transaction.Transactional
import org.springframework.transaction.annotation.Propagation

class UserService extends BaseService{

    @Transactional
    public boolean save(User userTosave, String roleToAssign = null){
        log.debug("Guardando al usuario " + userTosave?.username)
        boolean isSave = super.save(userTosave)
        log.info("El usuario " + userTosave?.username + " se guardó bien? " + isSave)
        if (isSave && roleToAssign){
            Role role = Role.where { authority == roleToAssign }.get()
            if (role){
                log.debug("Se va a crear el rol ${role.authority} al ${userTosave?.username} ")
                UserRole.create(userTosave, role)
                log.info("Se creó el role ${role.authority} al usuario ${userTosave?.username}")
            }else{
                throw new RoleNotExistException("No existe el rol " + roleToAssign)
            }
        }
        return isSave
    }

}
