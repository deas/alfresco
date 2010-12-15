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


# Set RMI_LISTEN_HOSTNAME to the hostname you wish the deployment server to listen on.
# See http://www.springframework.org/docs/api/org/springframework/remoting/rmi/RmiServiceExporter.html
# for more details.
RMI_LISTEN_HOSTNAME=
echo .
echo ===================================
echo = Stop Alfresco Deployment Server =
echo ===================================
echo .

if [ -z $RMI_LISTEN_HOSTNAME ]; then
    $JAVA_HOME/bin/java -server -classpath . -Djava.ext.dirs=. org.alfresco.deployment.Main shutdown-context.xml
else
    $JAVA_HOME/bin/java -server -classpath . -Djava.ext.dirs=. -Djava.rmi.server.hostname=$RMI_LISTEN_HOSTNAME org.alfresco.deployment.Main shutdown-context.xml
fi
