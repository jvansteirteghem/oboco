#!/bin/bash

PID_FILE="application.pid"
JAVA_OPTIONS=
DATA_DEVICE=
DATA_DIRECTORY=
# raspberry pi
#JAVA_OPTIONS="-Dos.arch=armv71"
#DATA_DEVICE="/dev/sda1"
#DATA_DIRECTORY="/media/pi/data"

mount_data() {
    if [ ! "$DATA_DEVICE" = "" ]; then
        if mountpoint -q "$DATA_DIRECTORY"; then
            echo "data is mounted ..."
        else
            echo "data is mounting ..."
            if [ ! -d "$DATA_DIRECTORY" ]; then
                sudo mkdir "$DATA_DIRECTORY"
            fi
            sudo mount "$DATA_DEVICE" "$DATA_DIRECTORY"
            echo "data is mounted ..."
        fi
    fi
}

unmount_data() {
    if [ ! "$DATA_DEVICE" = "" ]; then
        if mountpoint -q "$DATA_DIRECTORY"; then
            echo "data is unmounting ..."
            sudo umount "$DATA_DIRECTORY"
            echo "data is unmounted ..."
        else
            echo "data is unmounted"
        fi
    fi
}

start() {
    mount_data
    if [ ! -f $PID_FILE ]; then
        echo "application is starting ..."
        nohup java ${JAVA_OPTIONS} -cp libs/*:oboco-app-${project.version}.jar com.gitlab.jeeto.oboco.Server >/dev/null 2>&1 &
        PID=$!
        echo $PID > $PID_FILE
        sleep 5
        echo "application is started ..."
    else
        echo "application is started ..."
    fi
}

stop() {
    if [ -f $PID_FILE ]; then
        echo "application is stopping ..."
        PID=$(cat $PID_FILE);
        kill $PID;
        rm $PID_FILE
        sleep 5
        echo "application is stopped ..."
    else
        echo "application is stopped ..."
    fi
    unmount_data
}

status() {
    if [ -f $PID_FILE ]; then
        echo "application is started ..."
    else
        echo "application is stopped ..."
    fi
}

case "$1" in
    start)
        start
        ;;
    stop)
        stop
        ;;
    status)
        status
        ;;
    restart)
        stop
        start
        ;;
  *)
    echo "Usage: $0 { start | stop | status | restart }"
    exit 1
esac
exit 0