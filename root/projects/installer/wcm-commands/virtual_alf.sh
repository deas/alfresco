#!/bin/sh
# Start or stop Virtualization server
# Set the following to where Virtual Tomcat is installed
APPSERVER=./virtual-tomcat
# Set any default JVM values
#
if [ "$1" = "start" ]; then
  "$APPSERVER"/bin/startup.sh
elif [ "$1" = "stop" ]; then
  "$APPSERVER"/bin/shutdown.sh
fi
