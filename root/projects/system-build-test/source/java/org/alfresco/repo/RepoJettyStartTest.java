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
 */

package org.alfresco.repo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import javax.net.ssl.SSLContext;

import junit.framework.TestCase;

import org.eclipse.jetty.http.security.Password;
import org.eclipse.jetty.security.Authenticator;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.security.authentication.ClientCertAuthenticator;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.ssl.SslSelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.core.io.ClassPathResource;

/**
 * Start Alfresco Repository (running on embedded Jetty)
 * 
 * @author janv
 */
public class RepoJettyStartTest extends TestCase
{
    public static final int JETTY_STOP_PORT = 8079;
    public static final String JETTY_LOCAL_IP = "localhost";
    
    private static Server server = null;
    
    public static void startJetty() throws Exception
    {
        try
        {
            System.out.println("["+new Date()+"] startJetty: starting embedded Jetty server ...");
            
            server = new Server();
            
            SelectChannelConnector connector = new SelectChannelConnector();
            connector.setPort(8080) ;
            connector.setHost(JETTY_LOCAL_IP);
            connector.setAcceptors(2);
            connector.setConfidentialPort(8443);
            connector.setMaxIdleTime(20000);
            
            SslSelectChannelConnector ssl_connector = new SslSelectChannelConnector();
            ssl_connector.setPort(8443);
            ssl_connector.setHost(JETTY_LOCAL_IP);
            ssl_connector.setKeystoreType("JCEKS");
            ssl_connector.setKeystore("keystore/ssl.keystore");
            ssl_connector.setKeyPassword("kT9X6oe68t");
            ssl_connector.setTruststoreType("JCEKS");
            ssl_connector.setTruststore("keystore/ssl.truststore");
            ssl_connector.setTrustPassword("kT9X6oe68t");
            ssl_connector.setAllowRenegotiate(true);
            ssl_connector.setNeedClientAuth(true);
            ssl_connector.setProtocol("https");
            ssl_connector.setProtocol("TLS");
            connector.setMaxIdleTime(240000);
            
            server.setConnectors(new Connector[]{connector, ssl_connector});

            // note: .../web-client/build/dist must be on classpath (and "alfresco.war" pre-built)
            String warPath = new ClassPathResource("alfresco.war").getURI().toString();
            
            System.out.println("["+new Date()+"] startJetty: warPath = "+warPath);
            
            WebAppContext webAppContext = new WebAppContext();
            webAppContext.setContextPath("/alfresco");
            
            SecurityHandler sh = webAppContext.getSecurityHandler();
            sh.setRealmName("Repository");
            sh.setAuthMethod("CLIENT-CERT");
            ClientCertAuthenticator authenticator = new ClientCertAuthenticator();
            sh.setAuthenticator(authenticator);
           
           
            HashLoginService loginService = new HashLoginService();
            loginService.setName("Repository");
            loginService.putUser("CN=Alfresco Repository Client, OU=Unknown, O=Alfresco Software Ltd., L=Maidenhead, ST=UK, C=GB", new Password("jyw8x/dn3NZEy362NIp9A2wjBYfOV96vrod6toGv1+RvcRM0I4UXpZpSQjr8T5ZO3URFb70KIaVnY0aayuuktmqFwMJrzZaBw7eDyWoRbKh1l+77myJcYKohq8csZml+X2sCn7YXbyHBWbSAERJR3iZ7NVIjs+vI20whOcrCriY="), new String[]{"repoclient"});
            sh.setLoginService(loginService);
            
            webAppContext.setWar(warPath);
            
            server.setHandler(webAppContext);
            
            // for clean shutdown, add monitor thread 
            
            // from: http://ptrthomas.wordpress.com/2009/01/24/how-to-start-and-stop-jetty-revisited/
            // adapted from: http://jetty.codehaus.org/jetty/jetty-6/xref/org/mortbay/start/Monitor.html
            Thread monitor = new MonitorThread();
            monitor.start();
            
            
            server.start();
            
            System.out.println("["+new Date()+"] startJetty: ... embedded Jetty server started !");
        }
        catch (Exception e)
        {
            System.out.println("["+new Date()+"] startJetty: ... failed to start embedded Jetty server: "+e);
            throw e;
        }
    }
    
    
    private static class MonitorThread extends Thread 
    {
        private ServerSocket socket;
        
        public MonitorThread() 
        {
            setDaemon(true);
            setName("StopMonitor");
            try 
            {
                socket = new ServerSocket(JETTY_STOP_PORT, 1, InetAddress.getByName(JETTY_LOCAL_IP));
            } 
            catch(Exception e) 
            {
                throw new RuntimeException(e);
            }
        }
        
        @Override
        public void run() 
        {
            Socket accept;
            try 
            {
                accept = socket.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(accept.getInputStream()));
                reader.readLine();
                server.stop();
                accept.close();
                socket.close();
            } 
            catch(Exception e) 
            {
                throw new RuntimeException(e);
            }
        }
    }
    
    public void testStartJetty() throws Exception
    {
        RepoJettyStartTest.startJetty();
    }
}
