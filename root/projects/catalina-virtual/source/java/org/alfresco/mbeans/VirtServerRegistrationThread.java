/*-----------------------------------------------------------------------------
*  Copyright 2007-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
*  
*  
*  Author  Jon Cox  <jcox@alfresco.com>
*  File    VirtServerRegistrationThread.java
*----------------------------------------------------------------------------*/


package org.alfresco.mbeans;

import java.util.Properties;
import javax.management.Attribute;
import org.alfresco.jndi.AVMFileDirContext;
import java.io.FileInputStream;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.alfresco.mbeans.VirtServerInfoMBean;
import org.springframework.context.ApplicationContext;
import java.util.Map;
import java.util.HashMap;
import org.alfresco.catalina.host.AVMHost;

/**
*  Registers virtualization server with an AVM server.
*  Later, when the AVM server does something that will
*  require this virtualization server to do a recursive
*  classloader reload, the AVM server will send back a 
*  message to the virtualization server, telling it
*  which virtual webapp to reload.
*
*  The registration is done repeatedly (every N seconds where n == 20 for now)
*  to deal with the possibility of the alfresco server restarting.
*/

public class VirtServerRegistrationThread extends Thread
{
	private enum ServerStatus
	{
		NOT_AVAILABLE,
		AVAILABLE,
	}
	
    private static org.apache.commons.logging.Log log =
        org.apache.commons.logging.LogFactory.getLog( VirtServerRegistrationThread.class );

    ApplicationContext springContext_;
    JMXServiceURL      url_;         // URL of the JMX service on Alfresco
    Map<String,Object> env_;
    String             virt_url_;
    String             virt_domain_;
    String             virt_fqdn_;
    int                virt_http_port_;
    int                virt_connect_retry_interval_;
    ObjectName         virt_registry_;
    Attribute          virt_server_attrib_;
    boolean            done_  = false;
    JMXConnector       conn_;       // Connection to Alfresco JMX
    
    /**
     * Assume the server is available until we know otherwise
     */
	ServerStatus alfrescoStatus = ServerStatus.AVAILABLE;

    public VirtServerRegistrationThread()
    {
        springContext_   = AVMHost.GetSpringApplicationContext();

        VirtServerInfoMBean serverInfo = 
            (VirtServerInfoMBean)  
                springContext_.getBean("virtServerInfo");

        // Get info pertaining to the CIFS mount path
        // Also, expose the virtualization server's OS
        // for the remote Alfresco server.  This could
        // come in handy sharing a CIFS mount and/or debugging.

        String os_name = System.getProperty("os.name");
        serverInfo.setVirtServerOsName( os_name );


        // There *has* to be a mount path, even if it's bogus.
        // The "mount_path" forms the basis for JNDI asset names.
        // Users will probably leave these settings alone, because
        // if the automounting is not wanted, they can control that
        // via: alfresco.virtserver.cifs.avm.versiontree.win.automount
        // or :  alfresco.virtserver.cifs.avm.versiontree.unix.automount
        // (depending on whether they're on Windows or Unix/Mac)
        //
        // In any event, because of the criticality of having a non-null 
        // value, there's a check for a null mount location, just to be safe.

        String mount_path;

        if ( os_name.startsWith("Windows") )
        {
            mount_path = 
              serverInfo.getVirtServerCifsAvmVersionTreeWin();

            if ( mount_path == null )
            {
                // The mount point name ends up in logfiles.
                // Therefore, make it something so that even
                // if a person who fails to show an admin all
                // their config files, the reason why they can't
                // access their JSPs via CIFS will be obvious.
                // 
                // It's also nice to provide context-free search
                // phrases that can be entered into search engines
                // like google, so that one question/answer post
                // helps everybody.

                mount_path = "c:\\alfresco_no_cifs\\";
            }
            else
            {
                if (mount_path.length() == 1 )  // drive letter
                {
                    mount_path = mount_path + ":\\";
                }
                else if ( (mount_path.length() == 2) &&
                          (mount_path.charAt(1) == ':')
                        )
                {
                    mount_path = mount_path + "\\";
                }
            }
        }
        else
        {
            mount_path = 
              serverInfo.getVirtServerCifsAvmVersionTreeUnix();
            if ( mount_path == null )
            {
                mount_path = "/alfresco_no_cifs/";   
            }
        }

        AVMFileDirContext.setAVMFileDirMountPoint( mount_path );



        String catalina_base;
        catalina_base = System.getProperty("catalina.base");
        if ( catalina_base == null)
        {   
            catalina_base = System.getProperty("catalina.home");
        }
        if ( catalina_base != null)
        {   
            if ( ! catalina_base.endsWith("/") )
            {   
                catalina_base = catalina_base + "/";
            }
        }
        else { catalina_base = ""; }


        String password_file = catalina_base + "conf/alfresco-jmxrmi.password";
        Properties passwordProps = new Properties();
        String jmxrmi_password   = null;

        try 
        {
            passwordProps.load( new FileInputStream( password_file ) );
            jmxrmi_password = passwordProps.getProperty("controlRole");

            virt_connect_retry_interval_ = 
                serverInfo.getVirtServerConnectionRetryInterval();


            // Create a JMXServiceURL to connect to the Alfresco JMX RMI server
            // These urls tend to look like:
            // 
            //  "service:jmx:rmi://ignored/jndi/rmi://localhost:50500/alfresco/jmxrmi"

            String avm_jmx_url = "service:jmx:rmi://ignored/jndi/rmi://" +
                                  serverInfo.getAlfrescoJmxRmiHost()     +
                                  ":"                                    +
                                  serverInfo.getAlfrescoJmxRmiPort()     +
                                  "/alfresco/jmxrmi";

            url_ = new JMXServiceURL( avm_jmx_url );
            
            if (log.isInfoEnabled())
            {
                log.info("Remote Alfresco JMX Server url_ is " + avm_jmx_url);
            }

            env_ = new HashMap<String,Object>();

            String[] cred = new String[] { "controlRole", jmxrmi_password };
            env_.put("jmx.remote.credentials", cred );

            virt_registry_ = ObjectName.getInstance(
                "Alfresco:Name=VirtServerRegistry,Type=VirtServerRegistry");

            virt_domain_    = serverInfo.getVirtServerDomain();
            virt_fqdn_      = virt_domain_;
            virt_http_port_ = serverInfo.getVirtServerHttpPort();


            // The FQDN of the virtualization server is always:
            //
            //           ${alfresco.virtserver.domain}
            //
            // The invariant are: 
            //
            //    [1]  The virtualization domain:   ${alfresco.virtserver.domain} 
            //         and all its subdomains:    *.${alfresco.virtserver.domain}
            //         must resolve to the same IP address.
            //
            //    [2]  The IP address resolved in [1] must be equal to the
            //         IP address that the virtualization server listens on.
            //
            // See: $VIRTUAL_TOMCAT_HOME/conf/alfresco-virtserver.properties
            //

            virt_url_ = "service:jmx:rmi://ignored/jndi/rmi://" + 
                        virt_fqdn_                              + 
                        ":"                                     +
                        serverInfo.getVirtServerJmxRmiPort()    + 
                        "/alfresco/jmxrmi";

            virt_server_attrib_ = new Attribute("VirtServerJmxUrl", virt_url_ );
        }
        catch (Exception e)
        {
            log.error(
              "Could not find password file for remote Alfresco JMX Server",e);
        }
    }

    public void run() 
    {
        while ( getDone() != true )
        {
            registerVirtServer();

            // Take a nap.  
            try { Thread.sleep( virt_connect_retry_interval_ ); } 
            catch (Exception e) 
            {
                // Not much you can do about an exception here, just ignore it.
            }
        }

        // Say goodbye to the server (this time for good).
        
        JMXConnectorCloseThread conn_close = new JMXConnectorCloseThread(conn_);
        conn_close.start();                // async close
        conn_ = null;                      // prep for new connection
    }

    private void registerVirtServer()
    {
        try
        {
            if (conn_ == null  ) 
            { 
                conn_ = JMXConnectorFactory.connect(url_, env_); 
                
                if (log.isInfoEnabled())
                {
                    log.info("Connected to remote Alfresco JMX Server");
                }
            }

            MBeanServerConnection mbsc = conn_.getMBeanServerConnection();

            // mbsc.setAttribute( virt_registry_, virt_server_attrib_);

            mbsc.invoke( virt_registry_, 
                         "registerVirtServerInfo", 
                         new Object [] { virt_url_ , 
                                         virt_fqdn_,
                                         new Integer( virt_http_port_ )
                                       },
                         new String [] { "java.lang.String",
                                         "java.lang.String",      
                                         "java.lang.Integer"
                                       }
                       );
            
            alfrescoStatus = ServerStatus.AVAILABLE;

        }
        catch (Exception e)
        {
        	if(alfrescoStatus == ServerStatus.AVAILABLE)
        	{
        	    alfrescoStatus = ServerStatus.NOT_AVAILABLE;
        		log.error("Connection failure to remote Alfresco JMX Server: " + url_ + e.getMessage());
        	}
        	
            // The server isn't responding.  If the server crashed,
            // we could get stuck in a network timeout here that's 
            // much longer than our normal periodic retry
            // (depending on the socket options that were set).
            //
            // Therefore, do an async close. 
            //
            // Worst case analysis:
            //
            //    Even if we're retrying every few seconds, the
            //    async close is still ok because the number of 
            //    spawned threads does not grow without bound.
            //    As soon as the first timeout occurs, the subsequent 
            //    ones in the "close pipeline" die at the same rate 
            //    they're being spawned by the retry logic.
            //    Thus, everything works as long as the system can
            //    a spike of <network-timeout-in-sec>/<retry-in-sec>
            //    extra threads can be allocated.
            //
            //    Given:
            //
            //        Max timeout    ~ 4 min == 240 sec
            //        Default retry  ~ 5 sec
            //
            //    Max expected (worst case):  48 thread pipeline
            //
            //    You could only really even get this with a network
            //    that had totally flipped out; typically, the response
            //    would be very fast after the 1st failed connect.
            //
            //    Hence, the "worst case" is unlikely and unproblematic.
            
            JMXConnectorCloseThread conn_close = 
                new JMXConnectorCloseThread(conn_);

            conn_close.start();         // async close
            conn_ = null;               // prep for new connection
        }
    }

    /** 
    * Called by org.alfresco.catalina.host.AVMHost.stop()
    * so that the registration thread exists and the server
    * can shut down cleanly.
    */
    public void setDone()    { done_ = true; }

    /**
    *  Called within this thread's run() method to
    *  determine if it's time to end gracefully.
    */
    public boolean getDone() { return done_ ;}

    /**
    *  Allow the close operation on a thread to be async;
    *  closing a connection is a potentially slow operation. 
    *  <p>
    *  If the server has crashed, the close operation might 
    *  have to wait for a network protocol timeout.  This 
    *  class lets the calling thread avoid blocking.
    */
    protected class JMXConnectorCloseThread extends Thread
    {
        JMXConnector connection_;

        public  JMXConnectorCloseThread( JMXConnector connection  )
        {
            connection_ = connection;
        }

        public void run()
        {
            if ( connection_ != null )         // Here's where we might need
            {                                  // to wait out a lengthy network
                try { connection_.close(); }   // timout.  Ignore any errors;
                catch  (Exception e) { ; }     // they're meaningless here.
            }
        }
    }
}
