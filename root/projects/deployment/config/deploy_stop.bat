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
rem Set RMI_LISTEN_HOSTNAME to the hostname you wish the deployment server to listen on.
rem See http://www.springframework.org/docs/api/org/springframework/remoting/rmi/RmiServiceExporter.html
rem for more details.
set RMI_LISTEN_HOSTNAME=
echo .
echo ===================================
echo = Stop Alfresco Deployment Server =
echo ===================================
echo .

if "%RMI_LISTEN_HOSTNAME%"=="" goto StartServerWithoutRMIHostname
goto StartServerWithRMIHostname

:StartServerWithoutRMIHostname
"%JAVA_HOME%\bin\java" -server %JAVA_OPTS% -classpath . -Djava.ext.dirs=. org.alfresco.deployment.Main shutdown-context.xml 
goto End

:StartServerWithRMIHostname
"%JAVA_HOME%\bin\java" -server %JAVA_OPTS% -classpath . -Djava.ext.dirs=. -Djava.rmi.server.hostname=%RMI_LISTEN_HOSTNAME% org.alfresco.deployment.Main shutdown-context.xml 
goto End

:End
endlocal