#!/bin/sh
# ---------------------------------
# Script to clean Tomcat temp files
# ---------------------------------
echo "Cleaning temporary Alfresco files from Tomcat..."
rm -rf @@BITROCK_TOMCAT_DIRNAME@@/temp/Alfresco tomcat/work/Catalina/localhost/alfresco
rm -rf @@BITROCK_TOMCAT_DIRNAME@@/work/Catalina/localhost/share
rm -rf @@BITROCK_TOMCAT_DIRNAME@@/work/Catalina/localhost/awe
rm -rf @@BITROCK_TOMCAT_DIRNAME@@/work/Catalina/localhost/wcmqs