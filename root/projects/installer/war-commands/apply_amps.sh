#!/bin/sh
# -------
# Script for apply AMPs to installed WAR
# -------
export SCRIPT=$(readlink -f $0)
export SCRIPTPATH=$(dirname $SCRIPT)
export ALF_HOME=${SCRIPTPATH%/*}
export CATALINA_HOME=$ALF_HOME/tomcat
echo "This script will apply all the AMPs in ./amps and ./amps_share to the alfresco.war and share.war files in ./tomcat/webapps"
echo "Press control-c to stop this script . . ."
echo "Press any other key to continue . . ."
read RESP
java -jar $ALF_HOME/bin/alfresco-mmt.jar install $ALF_HOME/amps $CATALINA_HOME/webapps/alfresco.war -directory
java -jar $ALF_HOME/bin/alfresco-mmt.jar list $CATALINA_HOME/webapps/alfresco.war
java -jar $ALF_HOME/bin/alfresco-mmt.jar install $ALF_HOME/amps_share $CATALINA_HOME/webapps/share.war -directory
java -jar $ALF_HOME/bin/alfresco-mmt.jar list $CATALINA_HOME/webapps/share.war
echo "About to clean out ./tomcat/webapps/alfresco and ./tomcat/webapps/share directories and temporary files..."
echo "Press control-c to stop this script . . ."
echo "Press any other key to continue . . ."
read DUMMY
rm -rf $CATALINA_HOME/webapps/alfresco
rm -rf $CATALINA_HOME/webapps/share
.$ALF_HOME/bin/clean_tomcat.sh