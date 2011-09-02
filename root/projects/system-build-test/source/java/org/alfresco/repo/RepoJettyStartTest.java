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

import junit.framework.TestCase;

import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.server.Server;
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
    public static final String JETTY_LOCAL_IP = "127.0.0.1";
    
    private static Server server = null;
    
    public static void startJetty() throws Exception
    {
        try
        {
            System.out.println("["+new Date()+"] startJetty: starting embedded Jetty server ...");
            
            server = new Server(8080);

            // note: .../web-client/build/dist must be on classpath (and "alfresco.war" pre-built)
            String warPath = new ClassPathResource("alfresco.war").getURI().toString();
            
            System.out.println("["+new Date()+"] startJetty: warPath = "+warPath);
            
            WebAppContext webAppContext = new WebAppContext();
            webAppContext.setContextPath("/alfresco");
            
            // with a login-config in web.xml, jetty seems to require this in order to start successfully
            webAppContext.getSecurityHandler().setLoginService(new HashLoginService());
            
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
