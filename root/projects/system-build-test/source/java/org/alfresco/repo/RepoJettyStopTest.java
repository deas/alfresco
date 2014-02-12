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

import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;

import junit.framework.TestCase;

/**
 * Stop Alfresco Repository (running on embedded Jetty)
 * 
 * @author janv
 */
public class RepoJettyStopTest extends TestCase
{
    public void testStopJetty() throws Exception
    {
        try
        {
            System.out.println("["+new Date()+"] stopJetty: stopping embedded Jetty server ...");
            
            Socket s = new Socket(InetAddress.getByName(RepoJettyStartTest.JETTY_LOCAL_IP), RepoJettyStartTest.JETTY_STOP_PORT);
            OutputStream out = s.getOutputStream();
            
            out.write(("\r\n").getBytes());
            out.flush();
            s.close();
            
            System.out.println("["+new Date()+"] stopJetty: ... embedded Jetty server stopped !");
        }
        catch (Exception e)
        {
            System.out.println("["+new Date()+"] stopJetty: ... failed to stop embedded Jetty server: "+e);
            throw e;
        }
    }
}
