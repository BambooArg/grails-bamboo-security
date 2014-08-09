package ar.com.bamboo.security

class Role {

    public static final String ROLE_SUPERUSER = "ROLE_SUPERUSER"

	String authority

	static mapping = {
		cache true
	}

	static constraints = {
		authority blank: false, unique: true
	}
}
