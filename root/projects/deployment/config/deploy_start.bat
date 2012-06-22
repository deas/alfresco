@echo off
setlocal
if "%JAVA_HOME%"=="" goto NoJavaHome
if not exist %JAVA_HOME%\bin\java.exe goto InvalidJavaHome
goto StartServer

:NoJavaHome
echo.
echo Error: JAVA_HOME environment variable is not set.
goto End

:InvalidJavaHome
echo.
echo Error: JAVA_HOME '%JAVA_HOME%' does not contain a valid Java installation.
goto End

:StartServer

rem Set the ext dirs to the location of the JRE or JDK extension folders
if exist "%JAVA_HOME%\jre\lib\ext" ( set JAVA_EXT_DIR=%JAVA_HOME%\jre\lib\ext) else ( set JAVA_EXT_DIR=%JAVA_HOME%\lib\ext)
rem set _CLASSPATH to the location of this file without a trailing \ (to avoid unintended escaping of quotes)
set _CLASSPATH=%~dp0
if %_CLASSPATH:~-1%==\ set _CLASSPATH=%_CLASSPATH:~0,-1%

rem Set RMI_LISTEN_HOSTNAME to the hostname you wish the deployment server to listen on.
rem See http://www.springframework.org/docs/api/org/springframework/remoting/rmi/RmiServiceExporter.html
rem for more details.
set RMI_LISTEN_HOSTNAME=
echo .
echo =================================
echo = Alfresco File System Receiver =
echo =================================
echo .
if "%RMI_LISTEN_HOSTNAME%"=="" goto StartServerWithoutRMIHostname
goto StartServerWithRMIHostname

:StartServerWithoutRMIHostname
start /min "Deployment Server" cmd /C ""%JAVA_HOME%\bin\java" -server -classpath "%_CLASSPATH%" "-Djava.ext.dirs=.;%JAVA_EXT_DIR%" org.alfresco.deployment.Main application-context.xml"
goto End

:StartServerWithRMIHostname
start /min "Deployment Server" cmd /C ""%JAVA_HOME%\bin\java" -server -classpath "%_CLASSPATH%" "-Djava.ext.dirs=.;%JAVA_EXT_DIR%" -Djava.rmi.server.hostname=%RMI_LISTEN_HOSTNAME% org.alfresco.deployment.Main application-context.xml"
goto End

:End
endlocal