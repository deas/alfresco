/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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
*  Author  Jon Cox  <jcox@alfresco.com>
*  File    VirtServerRegistry.java
*----------------------------------------------------------------------------*/

package org.alfresco.mbeans;

// server side
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.springframework.context.ApplicationContext;

public class VirtServerRegistry implements VirtServerRegistryMBean
{
    private static org.apache.commons.logging.Log log=
        org.apache.commons.logging.LogFactory.getLog( VirtServerRegistry.class );

    private String      virtServerJmxUrl_;
    private String      virtServerFQDN_;
    private Integer     virtServerHttpPort_;

    private String      passwordFile_;
    private String      accessFile_;
    private ObjectName  virtWebappRegistry_;

    private JMXConnector           conn_ ;
    private JMXServiceURL          jmxServiceUrl_;
    private Map<String,Object>     env_;
    private MBeanServerConnection  mbsc_;
    private ApplicationContext     context_;


    // The org.alfresco.mbeans.ContextListener
    // creates a VirtServerRegistry when it receives 
    // a  contextInitialized(ServletContextEvent event) 
    // call from the Alfresco webapp.
    //
    // See also: the deployment descriptor for ContextListener 
    //           within the alfresco webapp's web.xml file.
    //
    public VirtServerRegistry() 
    {
        if ( log.isInfoEnabled() )
            log.info("Creating VirtServerRegistry MBean");
    }


    public void setApplicationContext(ApplicationContext applicationContext)
    {
        context_ = applicationContext;
    }

    public void initialize()
    {
        Properties passwordProps = new Properties();
        String jmxrmi_password   = null;

        try 
        {
            // throws exception when there's no password file to be found.
            InputStream is = new FileInputStream(passwordFile_);
            try
            {
                passwordProps.load(is);
            }
            finally
            {
                if (is != null) { try { is.close(); } catch (IOException e) {} }
            }

            jmxrmi_password = passwordProps.getProperty("controlRole");
            env_ = new HashMap<String,Object>();
            String[] cred = new String[] { "controlRole", jmxrmi_password };
            env_.put("jmx.remote.credentials", cred );
            virtWebappRegistry_ = ObjectName.getInstance(
                "Alfresco:Name=VirtWebappRegistry,Type=VirtWebappRegistry");
        }
        catch (Exception e)
        {
            if ( log.isWarnEnabled() )
            {
                log.warn(
                  "WCM virtualization disabled "      + 
                  "(alfresco-jmxrmi.password and/or " +
                  "alfresco-jmxrmi.access isn't on classpath) due to: "+e);
            }
        }
    }

    // interface method implementations

    public void   setPasswordFile(String path) { passwordFile_ = path;}
    public String getPasswordFile()            { return passwordFile_;}

    public void   setAccessFile(String path)   { accessFile_   = path;}
    public String getAccessFile()              { return accessFile_; }

    public Integer getVirtServerHttpPort()     { return virtServerHttpPort_;}
    public String  getVirtServerFQDN()         { return virtServerFQDN_;    }

    /**
    *  Accepts the registration of a remote virtualization server's 
    *  JMXServiceURL, FQDN, and HTTP port.   The JMXServiceURL is 
    *  used to broadcast updates regarding the WEB-INF directory, 
    *  and start/stop events.  The FQDN and HTTP port are used
    *  to create URLs for viewing virtualized content.
    *  
    *  Note:  a JMXServiceURL tends to look something like this:
    *  service:jmx:rmi:///jndi/rmi://some-remote-hostname:50501/alfresco/jmxrmi
    */
    public synchronized 
    void registerVirtServerInfo( String  virtServerJmxUrl,
                                 String  virtServerFQDN,
                                 Integer virtServerHttpPort
                               )
    {
        if  (   virtServerJmxUrl_ != null  && 
              ! virtServerJmxUrl_.equals( virtServerJmxUrl )
            )
        {
            jmxServiceUrl_ = null;
        }
        virtServerJmxUrl_    = virtServerJmxUrl; 
        virtServerFQDN_      = virtServerFQDN;
        virtServerHttpPort_  = virtServerHttpPort;
    }


    // public synchronized void   setVirtServerJmxUrl(String virtServerJmxUrl) 
    // { 
    //     if  (   virtServerJmxUrl_ != null  && 
    //           ! virtServerJmxUrl_.equals( virtServerJmxUrl )
    //         )
    //     {
    //         jmxServiceUrl_ = null;
    //     }
    //     virtServerJmxUrl_ = virtServerJmxUrl; 
    // }

    synchronized 
    JMXServiceURL getJMXServiceURL()
    {
        if ( jmxServiceUrl_ != null)       { return jmxServiceUrl_; }
        if ( virtServerJmxUrl_  == null )  { return null; } 

        try { jmxServiceUrl_ = new JMXServiceURL( virtServerJmxUrl_ ); }
        catch (Exception e)
        {
            if ( log.isErrorEnabled() )
                log.error("Could not create JMXServiceURL from: " + 
                           virtServerJmxUrl_);

            jmxServiceUrl_ = null;
        }
        return jmxServiceUrl_;
    }
    
    public String getVirtServerJmxUrl()                  { return virtServerJmxUrl_; }

    /**
    *  Notifies remote listener that a AVM-based webapp has been updated;
    *  an "update" is any change to (or creation of) contents within
    *  WEB-INF/classes  WEB-INF/lib, WEB-INF/web.xml of a webapp.
    *
    * @param version      The version of the webapp being updated.
    *                     Typically, this is set to -1, which corresponds
    *                     to the very latest version ("HEAD").
    *                     If versinon != -1, you might want to consider
    *                     setting the 'isRecursive' parameter to false.
    *                     <p>
    *
    * @param pathToWebapp The full AVM path to the webapp being updated.
    *                     For example:  storeName:/www/avm_webapps/your_webapp
    *                     <p>
    *
    * @param isRecursive  When true, update all webapps that depend on this one.
    *                     For example, an author's webapps share jar/class files
    *                     with the master version in staging; thus, the author's
    *                     webapp "depends" on the webapp in staging.   Similarly,
    *                     webapps in an author's preview area depend on the ones
    *                     in the "main" layer of the author's sandbox.   
    *                     You might wish to set this parameter to 'false' if 
    *                     the goal is to bring a non-HEAD version of a staging 
    *                     area online, without forcing the virtualization server 
    *                     to load all the author sandboxes for this archived 
    *                     version as well.
    *                   
    */
    public boolean 
    updateWebapp(int version,  String pathToWebapp, boolean isRecursive )
    {
        return jmxRmiWebappNotification( "updateVirtualWebapp",
                                          version,
                                          pathToWebapp,
                                          isRecursive
                                       );
    }

    /**
    *  Notifies remote listener that a AVM-based webapp has been updated;
    *  an "update" is any change to (or creation of) contents within
    *  WEB-INF/classes  WEB-INF/lib, WEB-INF/web.xml of a webapp.
    *
    * @param version      The version of the webapp being updated.
    *                     Typically, this is set to -1, which corresponds
    *                     to the very latest version ("HEAD").
    *                     If versinon != -1, you might want to consider
    *                     setting the 'isRecursive' parameter to false.
    *                     <p>
    *
    * @param path         The full AVM path to the webapp(s) being updated.
    *                     For example:  storeName:/www/avm_webapps/your_webapp
    *                     or:           storeName:/www/avm_webapps
    *                     <p>
    *
    * @param isRecursive  When true, update all webapps that depend on this one.
    *                     For example, an author's webapps share jar/class files
    *                     with the master version in staging; thus, the author's
    *                     webapp "depends" on the webapp in staging.   Similarly,
    *                     webapps in an author's preview area depend on the ones
    *                     in the "main" layer of the author's sandbox.   
    *                     You might wish to set this parameter to 'false' if 
    *                     the goal is to bring a non-HEAD version of a staging 
    *                     area online, without forcing the virtualization server 
    *                     to load all the author sandboxes for this archived 
    *                     version as well.
    *                   
    */
    public boolean 
    updateAllWebapps(int version,  String path, boolean isRecursive )
    {
        return jmxRmiWebappNotification( "updateAllVirtualWebapps",
                                          version,
                                          path,
                                          isRecursive
                                       );
    }

    /**
    *  Notifies remote listener that a AVM-based webapp has been removed.
    *
    * @param version      The version of the webapp being removed.
    *                     Typically, this is set to -1, which corresponds
    *                     to the very latest version ("HEAD").
    *                     If versinon != -1, you might want to consider
    *                     setting the 'isRecursive' parameter to false.
    *                     <p>
    *
    * @param pathToWebapp The full AVM path to the webapp being removed.
    *                     For example:  storeName:/www/avm_webapps/your_webapp
    *                     <p>
    *
    * @param isRecursive  When true, remove all webapps that depend on this one.
    *                     For example, an author's webapps share jar/class files
    *                     with the master version in staging; thus, the author's
    *                     webapp "depends" on the webapp in staging.   Similarly,
    *                     webapps in an author's preview area depend on the ones
    *                     in the "main" layer of the author's sandbox.   
    *                     You might wish to set this parameter to 'false' if 
    *                     the goal is to bring a non-HEAD version of a staging 
    *                     area online, without forcing the virtualization server 
    *                     to load all the author sandboxes for this archived 
    *                     version as well.
    */
    public boolean 
    removeWebapp(int version, String pathToWebapp, boolean isRecursive )
    {
        return jmxRmiWebappNotification( "removeVirtualWebapp",
                                          version,
                                          pathToWebapp,
                                          isRecursive
                                       );
    }


    /**
    *  Notifies remote listener that a AVM-based webapp has been removed.
    *
    * @param version      The version of the webapp being removed.
    *                     Typically, this is set to -1, which corresponds
    *                     to the very latest version ("HEAD").
    *                     If versinon != -1, you might want to consider
    *                     setting the 'isRecursive' parameter to false.
    *                     <p>
    *
    * @param path         The full AVM path to the webapp(s) being removed.
    *                     For example:  storeName:/www/avm_webapps/your_webapp
    *                              or:  storeName:/www/avm_webapps
    *                     <p>
    *
    * @param isRecursive  When true, remove all webapps that depend on this one.
    *                     For example, an author's webapps share jar/class files
    *                     with the master version in staging; thus, the author's
    *                     webapp "depends" on the webapp in staging.   Similarly,
    *                     webapps in an author's preview area depend on the ones
    *                     in the "main" layer of the author's sandbox.   
    *                     You might wish to set this parameter to 'false' if 
    *                     the goal is to bring a non-HEAD version of a staging 
    *                     area online, without forcing the virtualization server 
    *                     to load all the author sandboxes for this archived 
    *                     version as well.
    */
    public boolean 
    removeAllWebapps(int version, String path, boolean isRecursive )
    {
        return jmxRmiWebappNotification( "removeAllVirtualWebapps",
                                          version,
                                          path,
                                          isRecursive
                                       );
    }

    public boolean verifyJmxRmiConnection()
    {
        boolean result = false;
        
        // Typically the JMXServiceURL looks something like this:
        //  "service:jmx:rmi://ignored/jndi/rmi://localhost:50501/alfresco/jmxrmi"

        if ( getVirtServerJmxUrl() == null )
        { 
            if ( log.isWarnEnabled() )
                log.warn("No virtualization servers have registered as listeners");

            return result; 
        }

        if ( conn_ == null)
        {
            try 
            { 
                conn_ = JMXConnectorFactory.connect( getJMXServiceURL() , env_);
                mbsc_ = conn_.getMBeanServerConnection();
            }
            catch (Exception e)
            {
                if ( log.isErrorEnabled() )
                    log.error("Could not connect to virtualization server: " + 
                              getVirtServerJmxUrl() );

                return result;
            }
        }
        // If virtualization server have been terminated (E.g. Control+C in the console)
        // virtServerJmxUrl_ is not null and conn_ is not null so it is impossible to detect terminated/refused
        // connection. Just check connection to accessibility.
        else
        {
            result = pingVirtServer();
        }
        return result;
    }

    /**
     * Fast check of the accessibility of the virtualization server.
     * 
     * @see org.alfresco.linkvalidation.LinkValidationServiceImpl
     * 
     * @return Returns true if server is accessible.
     */
    public boolean pingVirtServer()
    {
        if (conn_ != null)
        {
            try
            {
                // Detects if the connection is connected/disconnected/terminated
                // See javax.management.remote.rmi.RMIConnector.getConnectionId() implementation
                conn_.getConnectionId();
                return true;
            }
            catch (Exception ex)
            {
                closeJmxRmiConnection();
                return false;
            }
        }
        return false;
    }

    protected boolean 
    jmxRmiWebappNotification( String  action,
                              int     version, 
                              String  pathToWebapp,
                              boolean isRecursive
                            )
    {
        long start = System.currentTimeMillis();
        
        if  ( ! verifyJmxRmiConnection() ) { return false; }
        
        try
        {
            Boolean result = 
                (Boolean) mbsc_.invoke( virtWebappRegistry_, 
                                        action,
                                        new Object [] { new Integer( version ),
                                                        pathToWebapp,
                                                        new Boolean( isRecursive )
                                                      },
                                        new String [] { "java.lang.Integer",
                                                        "java.lang.String",
                                                        "java.lang.Boolean"
                                                      }
                                      );
           
            if ( ! result.booleanValue() ) 
            { 
                if ( log.isErrorEnabled() )
                    log.error("Action failed: " + action + "  Version: " + 
                              version  + "  Webapp: " + pathToWebapp );

                return false; 
            }
            return true;
        }
        catch (Exception e)
        {
            if ( log.isErrorEnabled() )
                log.error(
                  "Could not connect to JMX Server within remote " +
                  "virtualization server " + 
                  "(this may be a transient error.)");

            closeJmxRmiConnection();

            return false;
        }
        finally
        {
            if (log.isDebugEnabled())
            {
                log.debug("jmxRmiWebappNotification: "+action+"["+version+", "+pathToWebapp+","+isRecursive+" in "+(System.currentTimeMillis()-start)+" ms");
            }
        }
    }
    
    public void closeJmxRmiConnection()
    {
        // Close the connection asynchronously.
        // This avoids having to wait for a network 
        // timout if the remote server crashed. 

        JMXConnectorCloser conn_closer = new JMXConnectorCloser( conn_ );
        new Thread( conn_closer ).start();

        conn_ = null;
    }

    // How to register w/o Spring (continued): 
    //
    //    private MBeanServer getServer() 
    //    {
    //        MBeanServer mbserver = null;
    //        ArrayList   mbservers = MBeanServerFactory.findMBeanServer(null);
    //
    //        if (mbservers.size() > 0) 
    //        {
    //            mbserver = (MBeanServer) mbservers.get(0);
    //        }
    //
    //        // Cnly create an MBeanServer if the system didn't already have one
    //        if ( mbserver == null )
    //        {
    //            mbserver = MBeanServerFactory.createMBeanServer();
    //        } 
    //        return mbserver;
    //    }
}


/**
*  Helper class to close a JMXConnector in a separate thread.
* 
*  Closing a connection is a potentially slow operation. 
*  For example, if the server has crashed, the close operation might 
*  have to wait for a network protocol timeout. 
*
*  See also: 
*  http://java.sun.com/j2se/1.5.0/docs/api/javax/management/remote/JMXConnector.html#close()
*/  
class JMXConnectorCloser implements Runnable
{
    JMXConnector conn_;

    JMXConnectorCloser(JMXConnector conn ) { conn_ = conn; }

    public void run()
    {
        try 
        { 
            if ( conn_ != null) {  conn_.close(); }
        }
        catch (Exception e){}
    }
}
