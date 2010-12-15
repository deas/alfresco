#!/bin/sh

# Make sure prerequisite environment variables are set
if [ -z "$JAVA_HOME" ]; then
    JAVA_PATH=`which java 2>/dev/null`
    if [ "x$JAVA_PATH" != "x" ]; then
      JAVA_LOCATION=`dirname $JAVA_PATH 2>/dev/null`
      JAVA_HOME=`dirname $JAVA_LOCATION 2>/dev/null`
    fi
fi

if [ -z "$JAVA_HOME" ]; then
    echo "The JAVA_HOME  environment variable is not defined"
    exit 1
fi

#
# Set the JAVA_EXT_DIR to point to the java extensions folder.
# this will be needed to pick up the cryptography libraries
#
if [ -e "$JAVA_HOME/lib/ext" ]
then
    JAVA_EXT_DIR=$JAVA_HOME/lib/ext
fi

if [ -e "$JAVA_HOME/jre/lib/ext" ]
then
    JAVA_EXT_DIR=$JAVA_HOME/jre/lib/ext
fi 

if [ -z "$JAVA_EXT_DIR" ]; 
then
   JAVA_EXT_DIR=/usr/lib/jvm/jre/lib/ext
fi
 

# Set RMI_LISTEN_HOSTNAME to the hostname you wish the deployment server to listen on.
# See http://www.springframework.org/docs/api/org/springframework/remoting/rmi/RmiServiceExporter.html
# for more details.
RMI_LISTEN_HOSTNAME=""
echo .
echo ====================================
echo = Start Alfresco Deployment Server =
echo ====================================
echo .

if [ -z "$RMI_LISTEN_HOSTNAME" ] ; then
    nohup $JAVA_HOME/bin/java -server -classpath . -Djava.ext.dirs=.:$JAVA_EXT_DIR org.alfresco.deployment.Main application-context.xml &
else
    nohup $JAVA_HOME/bin/java -server -classpath . -Djava.ext.dirs=.:$JAVA_EXT_DIR -Djava.rmi.server.hostname=$RMI_LISTEN_HOSTNAME org.alfresco.deployment.Main application-context.xml &
fi
