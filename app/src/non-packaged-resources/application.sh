#!/bin/bash

java -cp libs/*:oboco-app-${project.version}.jar com.gitlab.jeeto.oboco.Server
# raspberry pi
#java -Dos.arch=armv71 -cp libs/*:oboco-app-${project.version}.jar com.gitlab.jeeto.oboco.Server
