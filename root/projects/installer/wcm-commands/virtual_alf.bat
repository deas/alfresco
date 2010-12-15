@echo off
rem ---------------------------------------------------------------------------
rem Start script for the Virtualization Server
rem ---------------------------------------------------------------------------

rem set Alfresco home (includes trailing \  e.g. c:\alfresco\)
set ALF_HOME=%~dp0
set CATALINA_HOME=%ALF_HOME%virtual-tomcat

:start
set PATH=%ALF_HOME%bin;%PATH%

if not ""%1"" == ""start"" goto nostart

echo Starting Tomcat...
call "%CATALINA_HOME%\bin\startup.bat"

goto nostop
:nostart

if not ""%1"" == ""stop"" goto nostop

echo Shutting down Tomcat...
call "%CATALINA_HOME%\bin\shutdown.bat" 

:nostop