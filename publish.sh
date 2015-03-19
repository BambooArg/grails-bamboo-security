#!/bin/bash

echo "instalando bamboo security"
grails clean && grails refresh-dependencies && grails maven-install

#grails publish-plugin 

