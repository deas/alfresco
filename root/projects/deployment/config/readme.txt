Setting up the Alfresco Standalone Deployment Receiver
------------------------------------------------------------------------
The automatic installer will install a standalone deployment receiver.

Here are instructions for manual install.

1. Unzip the deployment zip file into a convenient location. (It does not
   make it's own directory.) For discussion's sake let's say that the distribution file
   has been expanded into /opt/deployment.
   
2. Configure deployment.properties. Open deployment.properties in your
   text editor of choice.  Choose locations for each of the following:
   
   A. deployment.filesystem.datadir - This is the location that the filesystem deployment receiver stores
      deployed files during a deployment, before committing them to their final
      locations. For example:
        
        deployment.filesystem.datadir =/opt/deployment/depdata
   
   B. deployment.filesystem.logdir - This is the location in which the filesystem deployment receiver stores
      deployment time log data. For example:
        
        deployment.filesystem.logdir=/opt/deployment/deplog
        
   C. deployment.filesystem.metadatadir - This is the location in which the filesystem deployment receiver stores
      metadata about deployed content. For example:
      
        dep.metadatadir=/opt/deployment/depmetadata
        
   D. deployment.rmi.port - If you are using RMI as your transport protocol.  The port number to use for the RMI registry. Choose this so as not to
      conflict with any other services.   By default the standalone deployment receiver uses 44100
      
   E. deployment.rmi.service.port - If you are using RMI as your transport protocol. The port number to use for RMI service. Choose this so as not to
      conflict with any other services.
      
	Please note for windows
    	for directory locations windows backslashes need to be escaped e.g. c:\\dir1\\dir2
    	or you can use the UNIX '/' character as a directory separator e.g c:/dir1/dir2
      
3. Configure your deployment targets.

	Deployment targets are placed in the "deployment" folder with the filename "deployment/*target.xml".
	
	By default a single filesystem receiver is defined with simple configuration via deployment.properties.
	
	To define more targets follow the pattern of default-target.xml.    There are two steps involved a) definition of your target and 
	b) registration of your target with the deployment engine.
    
4. Define your custom runnables for prepare and postCommit callbacks

   If you have any custom runnables, use the configuration of the sampleProgramRunnable as a template.
   
   Define your runnable bean
   <bean id="myBean" class="xxx.Myclass">
         <property name="exampleProperty">
         	<value>Hello World</value>
         </property>
    </bean>
     
    add your runnable beans to the target, like so
     
                     <entry key="postCommit">
                         <list>
                             <ref bean="sampleRunnable"/>
                             <ref bean="myBean"/>
                             <ref bean="superBean"/>
                         </list>
                     </entry>

5. Define any transport adapters.  
  
6. Run the receiver. Execute deploy_start.sh (or deploy_start.bat) as the user you want your deployed
   content to be owned by.
   
   