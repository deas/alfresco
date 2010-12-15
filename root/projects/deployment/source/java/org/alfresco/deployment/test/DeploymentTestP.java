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

package org.alfresco.deployment.test;

import java.io.OutputStream;

import org.alfresco.deployment.DeploymentReceiverService;
import org.alfresco.deployment.impl.DeploymentException;
import org.alfresco.util.GUID;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import junit.framework.TestCase;

/**
 * Some test for the deployment receiver.
 * @author britt
 */
public class DeploymentTestP extends TestCase
{
    private static ApplicationContext fContext = null;
    
    private static DeploymentReceiverService fService;
    
    @Override
    protected void setUp() throws Exception
    {
        if (fContext == null)
        {
            fContext = new FileSystemXmlApplicationContext("config/application-context.xml");
            fService = (DeploymentReceiverService)fContext.getBean("deploymentReceiverService");
        }
    }

    @Override
    protected void tearDown() throws Exception
    {
    }
    
//    public void testSimple()
//    {
//        try
//        {
//            String ticket = fService.begin("sampleTarget", "Giles", "Watcher");
//            System.out.println(fService.getListing(ticket, "/"));
//            OutputStream out = fService.send(ticket, "/foo.dat", GUID.generate());
//            out.write("I'm naming all the stars.\n".getBytes());
//            fService.finishSend(ticket, out);
//            fService.commit(ticket);
//            ticket = fService.begin("sampleTarget", "Giles", "Watcher");
//            fService.delete(ticket, "/build.xml");
//            fService.delete(ticket, "/lib");
//            out = fService.send(ticket, "/src", GUID.generate());
//            out.write("I used to be a directory.\n".getBytes());
//            fService.finishSend(ticket, out);
//            fService.commit(ticket);
//            ticket = fService.begin("sampleTarget", "Giles", "Watcher");
//            out = fService.send(ticket, "/test/glory.txt", GUID.generate());
//            out.write("This town has too many vampires and not enough retail outlets.\n".getBytes());
//            fService.finishSend(ticket, out);
//            fService.delete(ticket, "/example_run.xml");
//            fService.abort(ticket);
//            ticket = fService.begin("sampleTarget", "Giles", "Watcher");
//            try
//            {
//                fService.getListing(ticket, "/foo/bar");
//                fail();
//            }
//            catch (DeploymentException e)
//            {
//                // This should happen.
//            }
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//            fail();
//        }
//    }
}
