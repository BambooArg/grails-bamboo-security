#!/bin/bash

echo "instalando bamboo security"
grails clean && grails refresh-dependencies && grails publish-plugin

#grails publish-plugin 

