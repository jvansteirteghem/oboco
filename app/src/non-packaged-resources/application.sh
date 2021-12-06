#!/bin/bash

JAVA_OPTIONS=
# raspberry pi
#JAVA_OPTIONS="-Dos.arch=armv71 -Xms1024m -Xmx2048m"

java ${JAVA_OPTIONS} -cp libs/*:oboco-app-${project.version}.jar com.gitlab.jeeto.oboco.Server
