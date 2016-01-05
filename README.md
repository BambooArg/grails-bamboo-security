[![Build Status](https://travis-ci.org/BambooArg/grails-bamboo-security.svg?branch=master)](https://travis-ci.org/BambooArg/grails-bamboo-security.svg)

grails-bamboo-security
======================

El plugin está destinado a manejar todo lo relativo a la seguridad de las aplicaciones de bamboo.
Su función es gestinar las funcionalidades comunes a todos los proyecto y ser lo suficientemente flexible,
como para permitir su extensión en caso de necesitarlo.


#Instalación

Agregar el plugin al proyecto
```groovy
compile "org.grails.plugin:grails-bamboo-security:$version"
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


# Build

Para compilar el proyecto e intalarlo localmente se debe ejecutar

 ```script
gradle install
```

Para publicar un release se debe ejecutar

```script
gradle publishMavenPublicationToBambooReleaseRepository

```

Para publicar un snapshot se debe ejecutar

```script
gradle publishMavenPublicationToBambooSNAPSHOTRepository

```

El repositorio default para la publicación es http://nexus-bambooarg.rhcloud.com/nexus/content/groups/public/


###**Atención**
Tener en cuenta que se tiene que tener configurado:

Las variables de entorno

```script
BAMBOO_REPOSITORY_USERNAME
BAMBOO_REPOSITORY_PASSWORD

```

o las propiedades del proyecto
```script
bambooRepositoryUsername
bambooRepositoryPassword

```


# Test

El proyecto usa travis-ci como entorno de integración continua. https://travis-ci.org/BambooArg/grails-bamboo-security.
Se ejecutan tantos los test unitarios como integrales, corriendo la base de datos de test en memoria.


# ¿Cómo usarlo?

Luego de instalar **grails-bamboo-security**  y **spring-security-core**, **no** se debe correr el script de spring-security-core grails 
's2-quickstart', sino que configurar el proyecto en el archivo application.groovy con las siguiente configuración de spring-security


```groovy
grails.plugin.springsecurity.userLookup.userDomainClassName = 'ar.com.bamboo.security.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'ar.com.bamboo.security.UserRole'
grails.plugin.springsecurity.authority.className = 'ar.com.bamboo.security.Role'
grails.plugin.springsecurity.controllerAnnotations.staticRules = [
        [pattern: '/',                  access: ['permitAll']],
        [pattern: '/index',             access: ['permitAll']],
        [pattern: '/index.gsp',         access: ['permitAll']],
        [pattern: '/assets/**',         access: ['permitAll']],
        [pattern: '/**/js/**',          access: ['permitAll']],
        [pattern: '/**/css/**',         access: ['permitAll']],
        [pattern: '/**/images/**',      access: ['permitAll']],
        [pattern: '/**/favicon.ico',    access: ['permitAll']]
]
]
```

###### **Mejoras**: En el _Install.groovy ya realizar esta tarea. Está cargada en la issue #1
