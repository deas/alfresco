@echo off
rem -------
rem Script for apply AMPs to installed WAR
rem -------

set ALF_HOME=@@BITROCK_INSTALLDIR@@
set JAVA_HOME="@@BITROCK_JAVA_HOME_WIN@@"
set CATALINA_HOME=%ALF_HOME%\@@BITROCK_TOMCAT_DIRNAME@@

if not exist "%ALF_HOME%\SetPaths.bat" goto getpaths
call "%ALF_HOME%\SetPaths.bat"
goto start

:getpaths
call "%ALF_HOME%\bin\RegPaths.exe"
call "%ALF_HOME%\SetPaths.bat"
del "%ALF_HOME%\SetPaths.bat"

:start
echo This script will apply all the AMPs in %ALF_HOME%\amps to the alfresco.war and all the AMPs in %ALF_HOME%\amps_share to the share.war in %CATALINA_HOME%\webapps
if ""%1"" == ""nowait"" goto nowait1
echo Press control-c to stop this script . . .
pause
:nowait1
"%JAVA_HOME%\bin\java" -jar "%ALF_HOME%\bin\alfresco-mmt.jar" install "%ALF_HOME%\amps" "%CATALINA_HOME%\webapps\alfresco.war" -directory "%2"
"%JAVA_HOME%\bin\java" -jar "%ALF_HOME%\bin\alfresco-mmt.jar" list "%CATALINA_HOME%\webapps\alfresco.war"
"%JAVA_HOME%\bin\java" -jar "%ALF_HOME%\bin\alfresco-mmt.jar" install "%ALF_HOME%\amps_share" "%CATALINA_HOME%\webapps\share.war" -directory "%2"
"%JAVA_HOME%\bin\java" -jar "%ALF_HOME%\bin\alfresco-mmt.jar" list "%CATALINA_HOME%\webapps\share.war"
echo .
echo About to clean out %ALF_HOME%/tomcat/webapps/alfresco directory and temporary files...
if ""%1"" == ""nowait"" goto nowait2
pause
:nowait2
rmdir /S /Q "%CATALINA_HOME%\webapps\alfresco"
rmdir /S /Q "%CATALINA_HOME%\webapps\share"
call "%ALF_HOME%\bin\clean_tomcat.bat"