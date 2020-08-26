#!/bin/bash

java -XX:+UseG1GC -XX:+UseStringDeduplication -Xms256m -Xmx1024m -cp libs/*:oboco-app-${project.version}.jar com.gitlab.jeeto.oboco.Server
