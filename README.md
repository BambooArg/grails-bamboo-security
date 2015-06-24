[![Build Status](https://travis-ci.org/orkonano/grails-bamboo-security.svg?branch=master)


grails-bamboo-security
======================

El plugin está destinado a manejar todo lo relativo a la seguridad de las aplicaciones de bamboo.
Su función es gestinar las funcionalidades comunes a todos los proyecto y ser lo suficientemente flexible,
como para permitir su extensión en caso de necesitarlo.


#Instalación

Agregar el plugin al proyecto
```groovy
compile ":grails-bamboo-security:0.1.0"
```

##Dependencias

El proyecto depende de los siguientes plugins. Actualmente al agregar grails-bamboo-security al proyecto, **no** agrega al proyecto las dependencias.

- spring-security-core
- grails-bamboo-architecture
- grails-bamboo-commons
- grails-bamboo-commons-entity


## Dominio

Las clases por default para el manejo de usuarios y roles son *User*, *Role* y *UserRole*, a su vez el plugin cuenta con dos clases helper como *BambooUserDetails* y *BambooUserDetailsService*

*BambooUserDetailsService* está configurada como un bean de Spring bajo el nombre de *userDetailsService*


#Build

Para compilar el proyecto e intalarlo localmente se debe ejecutar

 ```grails
grails maven-install 
```

Para publicar un release se debe ejecutar

```grails
grails publish-plugin

```

Para publicar un snapshot se debe ejecutar

```grails
grails publish-plugin --repository=bambooRepoSnapshot

```

El repositorio default para la publicación es http://nexus-bambooarg.rhcloud.com/nexus/content/groups/public/


###**Atención**
Tener en cuenta que se tiene que tener configurado en .grails/setting.groovy
```groovy
grails.project.repos.default = "bambooRepo"
grails.project.repos.bambooRepo.url = "http://nexus-bambooarg.rhcloud.com/nexus/content/repositories/releases/"
grails.project.repos.bambooRepo.type = "maven"
grails.project.repos.bambooRepo.username = username (poner el username real)
grails.project.repos.bambooRepo.password = password (poner el password real)

grails.project.repos.bambooRepoSnapshot.url = "http://nexus-bambooarg.rhcloud.com/nexus/content/repositories/snapshots/"
grails.project.repos.bambooRepoSnapshot.type = "maven"
grails.project.repos.bambooRepoSnapshot.username = username
grails.project.repos.bambooRepoSnapshot.password = password


#Test

El proyecto usa travis-ci como entorno de integración continua. https://travis-ci.org/orkonano/grails-bamboo-security.
Se ejecutan tantos los test unitarios como integrales, corriendo la base de datos de test en memoria.


#Cómo usarlo

Luego de instalar **grails-bamboo-security**  y **spring-security-core**, **no** se debe correr el script de spring-security-core grails 
's2-quickstart', sino que configurar el proyecto en el archivo Config.groovy con las siguiente configuración de spring-security


```groovy
grails.plugin.springsecurity.userLookup.userDomainClassName = 'ar.com.bamboo.security.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'ar.com.bamboo.security.UserRole'
grails.plugin.springsecurity.authority.className = 'ar.com.bamboo.security.Role'
grails.plugin.springsecurity.controllerAnnotations.staticRules = [
	'/':                              ['permitAll'],
	'/index':                         ['permitAll'],
	'/index.gsp':                     ['permitAll'],
	'/assets/**':                     ['permitAll'],
	'/**/js/**':                      ['permitAll'],
	'/**/css/**':                     ['permitAll'],
	'/**/images/**':                  ['permitAll'],
	'/**/favicon.ico':                ['permitAll']
]
```

###### **Mejoras**: En el _Install.groovy ya realizar esta tarea. Está cargada en la issue #1
