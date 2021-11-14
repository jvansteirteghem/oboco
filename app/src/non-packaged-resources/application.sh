#!/bin/bash

JAVA_OPTIONS=
# raspberry pi
#JAVA_OPTIONS="-Dos.arch=armv71"

java ${JAVA_OPTIONS} -cp libs/*:oboco-app-${project.version}.jar com.gitlab.jeeto.oboco.Server
