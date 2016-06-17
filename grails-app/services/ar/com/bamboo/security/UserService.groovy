package ar.com.bamboo.security

import ar.com.bamboo.commons.exception.BusinessValidator
import ar.com.bamboo.commonsEntity.Person
import ar.com.bamboo.commonsEntity.PersonService
import ar.com.bamboo.framework.BaseService
import ar.com.bamboo.framework.exceptions.ValidatorException
import ar.com.bamboo.framework.persistence.PaginatedResult
import ar.com.bamboo.security.exception.RoleNotExistException
import ar.com.bamboo.security.exception.UserNotExistException
import grails.core.GrailsApplication
import grails.gorm.DetachedCriteria
import grails.plugin.cache.Cacheable
import grails.plugin.springsecurity.SpringSecurityService
import grails.transaction.Transactional
import grails.util.Environment
import org.apache.commons.lang.RandomStringUtils

import java.security.SecureRandom

class UserService extends BaseService{

    PersonService personService
    SpringSecurityService springSecurityService
    GrailsApplication grailsApplication

    @Transactional
    public boolean createUser(User userTosave, String roleToAssign){
        if (userTosave.person && !userTosave.person.id ){
            log.debug("Guardando a la persona " + userTosave?.person?.firstName)
            personService.save(userTosave.person)
            if (userTosave.person.hasErrors()){
                throw new ValidatorException(model: userTosave.person)
            }
        }
        this.autogeneratePassword(userTosave)
        log.debug("Guardando al usuario " + userTosave?.username)
        boolean isSave = super.save(userTosave)
        if (userTosave.hasErrors()){
            throw new ValidatorException(model: userTosave)
        }
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

    private void autogeneratePassword(User user){
        //Genero el password aleatorio si es que no lo tiene
        if (Environment.currentEnvironment == Environment.PRODUCTION){
            user.password = RandomStringUtils.randomAlphanumeric(8)
        }else{
            user.password = 'pass'
            user.passwordNoEncoding = user.password
        }
    }

    @Transactional
    User changePasswordToAutogenerated(User userToChangePassword){
        this.autogeneratePassword(userToChangePassword)
        super.save(userToChangePassword)
        if (userToChangePassword.hasErrors()){
            throw new ValidatorException(model: userToChangePassword)
        }
        return userToChangePassword
    }

    @Transactional(readOnly = true)
    PaginatedResult list(Map params) {
        def where = { enabled == true } as DetachedCriteria<User>
        return this.listWithLimit(User.class, where, params)
    }

    @Transactional(readOnly = true)
    List<User> listAll() {
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
    @Cacheable(value = "default-cache", key = "#username")
    Long getIdByUsername(String username){
        StringBuilder hql = new StringBuilder("SELECT u.id FROM User u ")
                .append(" WHERE u.enabled = true and u.username = :username ")
        Map parameters = [username: username]
        return this.getUnique(User.class, hql.toString(), parameters)
    }

    @Transactional(readOnly = true)
    List<User> getByUsernameOrLastName(String usernameOrLastName) {
        StringBuilder hql = new StringBuilder("FROM User user WHERE user.enabled = true AND (user.username LIKE :username ")
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
        StringBuilder hql = new StringBuilder("SELECT ur.role.id  FROM UserRole ur ")
                .append(" WHERE ur.user = :user ")

        Map<String, Object> parameters = new HashMap<String, Object>()
        parameters.user = user
        return UserRole.executeQuery(hql.toString(), parameters)
    }

    @Transactional
    TokenLogin generateTokenLogin(User user){
        this.expiresOldTokenLoginByUser(user)
        SecureRandom prng = SecureRandom.getInstance("SHA1PRNG")
        String token = Integer.toHexString(prng.nextInt())
        token += Integer.toHexString(prng.nextInt())
        token += Integer.toHexString(prng.nextInt())
        TokenLogin tokenLogin = new TokenLogin(user: user, token: token)
        if (!tokenLogin.save()){
            log.error("Error en la generación de token para login")
            throw new ValidatorException(model: tokenLogin)
        }
        return tokenLogin
    }

    @Transactional
    def expiresOldTokenLoginByUser(User user) {
        List<TokenLogin> oldsToken = this.getAllTokenLoginNotExpiredByUser(user)
        for (token in oldsToken){
            this.expiresToken(token)
        }
    }

    @Transactional
    def expiresToken(TokenLogin tokenLogin) {
        tokenLogin.expired = true
        if (!tokenLogin.save()){
            throw new ValidatorException(model: tokenLogin)
        }
    }

    @Transactional(readOnly = true)
    List<TokenLogin> getAllTokenLoginNotExpiredByUser(User user){
        final User userArg = user
        def where = { enabled == true && expired == false && user == userArg} as DetachedCriteria<TokenLogin>
        super.listAll(TokenLogin.class, where)
    }

    @Transactional(readOnly = true)
    TokenLogin getTokenLoginNotExpiredByUser(User user){
        final User userArg = user
        def where = { enabled == true && expired == false && user == userArg} as DetachedCriteria<TokenLogin>
        List<TokenLogin> tokenLogins = super.listAll(TokenLogin.class, where)
        return tokenLogins ? tokenLogins[0] : null
    }

    @Transactional(readOnly = true)
    TokenLogin getTokenLoginNotExpiredByToken(String token) {
        final String tokenArg = token
        def where = { enabled == true && expired == false && token == tokenArg} as DetachedCriteria<TokenLogin>
        List<TokenLogin> tokenLogins = super.listAll(TokenLogin.class, where)
        return tokenLogins ? tokenLogins[0] : null
    }

    @Transactional
    def validateAccount(User user, String password) {
        user.password = password
        user.accountVerified = true
        user.dateAccountVerified = new Date()
        user.acceptedTermCondition = true
        user.termConditionDateAccept = new Date()
        if (!user.save()){
            throw new ValidatorException<User>(model: user)
        }
        this.expiresOldTokenLoginByUser(user)
    }

    @Transactional
    User changePassword(User user, String oldPassword, String password) {
        if (!samePassword(oldPassword, user.password)){
            throw new BusinessValidator("La contraseña actual no es correcta.")
        }

        if (samePassword(password, user.password)){
            throw new BusinessValidator("La contraseña debe ser diferente a la actual.")
        }

        this.changePassword(user, password)
    }

    @Transactional
    User changePassword(User user, String password) {
        user.password = password
        if (!user.save()){
            throw new ValidatorException<User>(model: user)
        }
        this.expiresOldTokenLoginByUser(user)
        return user
    }

    private boolean samePassword(String plainPassword, String encryptPassword) {
        springSecurityService.passwordEncoder.isPasswordValid(encryptPassword, plainPassword, null)
    }

    @Transactional
    TokenLogin changePassword(String username){
        User user = this.getByUsername(username)
        if (!user){
            throw new UserNotExistException(username)
        }
        this.generateTokenLogin(user)
    }

    @Transactional
    def updateUser(User user) {
        if (!super.save(user.person)){
            throw new ValidatorException<Person>(model: user.person)
        }
    }
}