#!/bin/bash

version="$(grep 'grailsBambooSecurityVersion =' build.gradle)"
version="${version#*=}"
version="${version//[[:blank:]\'\"]/}"

tagVersion="v$version"

echo "Publishing $version version"

EXIT_STATUS=0

if [[ $TRAVIS_REPO_SLUG == "BambooArg/grails-bamboo-security" && $TRAVIS_PULL_REQUEST == 'false' && $TRAVIS_TAG == $tagVersion && $EXIT_STATUS -eq 0 ]]; then
    ./gradlew publish
else
    if [[ $TRAVIS_REPO_SLUG == "BambooArg/grails-bamboo-security" && $TRAVIS_PULL_REQUEST == 'false' && $TRAVIS_TAG != $tagVersion && $EXIT_STATUS -eq 0 ]]; then
        echo "Not publish because $tagVersion is different to Travis Tag  $TRAVIS_TAG"
        EXIT_STATUS=1
    else
        EXIT_STATUS=0
    fi
fi

exit $EXIT_STATUS
