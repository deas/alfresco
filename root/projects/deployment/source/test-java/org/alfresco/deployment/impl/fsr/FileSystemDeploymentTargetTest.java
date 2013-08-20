package org.alfresco.deployment.impl.fsr;

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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.deployment.DeploymentReceiverTransport;
import org.alfresco.deployment.FSDeploymentRunnable;
import org.alfresco.deployment.impl.DeploymentException;
import org.alfresco.deployment.impl.server.Deployment;
import org.alfresco.deployment.impl.server.DeploymentTargetRegistry;
import org.alfresco.util.Deleter;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import junit.framework.TestCase;

public class FileSystemDeploymentTargetTest extends TestCase 
{
    private File log = null;
    private File metadata = null;
    private File data = null;
    private File target = null;
    
    private String TEST_USER = "Giles";
    private char[] TEST_PASSWORD = "Watcher".toCharArray();
    private String TEST_TARGET = "sampleTarget";
    
    private DeploymentTargetRegistry registry = null;
    private DeploymentReceiverTransport transport = null; 

	/**
	 * @param name
	 */
	public FileSystemDeploymentTargetTest(String name) 
	{
		super(name);
	}
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {


	    super.setUp();
	    log = new File("deplog");
	    log.mkdir();
	    metadata = new File("depmetadata");
	    metadata.mkdir();
	    data = new File("depdata");
	    data.mkdir();
	    target = new File("sampleTarget");
	    target.mkdir();
	        
	    /**
	      * Start the Standalone Deployment Engine
	      */
	    @SuppressWarnings("unused")
	        ClassPathXmlApplicationContext receiverContext =
	            new ClassPathXmlApplicationContext("application-context.xml");
	        
	        transport = (DeploymentReceiverTransport)receiverContext.getBean("deploymentReceiverEngine");
	        registry = (DeploymentTargetRegistry)receiverContext.getBean("deploymentReceiverEngine");    
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
    	super.tearDown();
    	
        if(log != null)
        {
        	Deleter.Delete(log);
        }
        if(data != null)
        {
        	Deleter.Delete(data);
        }
        if(metadata != null)
        {
        	Deleter.Delete(metadata);
        }
        if(target != null)
        {
        	Deleter.Delete(target);
        }

        File dot = new File(".");
        String[] listing = dot.list();
        for (String name : listing)
        {
             if (name.startsWith("dep-record-"))
             {
                 File file = new File(name);
                 file.delete();
             }
        }
	}
	
	
	public void testAutoFix()
	{
	
	}
	
	public void testValidateMetaData()
	{
	
	}
	
	/**
	 * Test postCommitCallback
	 * the exception should be swallowed and not thrown.
	 */
	
	public void testPostCommit()
	{
	
		FSRunnableTester tester = new FSRunnableTester();
		tester.setThrowException(true);
		
		FileSystemDeploymentTarget t = (FileSystemDeploymentTarget)registry.getTargets().get(TEST_TARGET);
		assertNotNull("sampleTarget null", t);
		List<FSDeploymentRunnable> postCommit = new ArrayList<FSDeploymentRunnable>();
		postCommit.add(tester);
		t.setPostCommit(postCommit);
		String ticket = t.begin(TEST_TARGET, "wibble", 1, TEST_USER, TEST_PASSWORD);
		t.prepare(ticket);
		t.commit(ticket);
		
		assertTrue("isRunCalled", tester.isRunCalled());
		
	}
	
	
	/**
	 * Test the prepare callback 
	 */
	public void testPrepare()
	{
		FSRunnableTester tester = new FSRunnableTester();
		tester.setThrowException(true);
	
		/**
		 * Test with one callback that throws an exception
		 */
		FileSystemDeploymentTarget t = (FileSystemDeploymentTarget)registry.getTargets().get(TEST_TARGET);
		assertNotNull("sampleTarget null", t);
		List<FSDeploymentRunnable> prepare = new ArrayList<FSDeploymentRunnable>();
		prepare.add(tester);
		t.setPrepare(prepare);
			
		String ticket = t.begin(TEST_TARGET, "wibble", 1, TEST_USER, TEST_PASSWORD);
		try 
		{
			t.prepare(ticket);
			fail("deployment exception not thrown");
		}
		catch (DeploymentException de)
		{
			// Should go here
		}
		finally
		{
			t.abort(ticket);	
		}
		
		
		
		
	}
	public void testMultiplePrepare()
	{
		/**
		 * Prepare with multiple callbacks one of which throws an exception
		 */
		FSRunnableTester bomb = new FSRunnableTester();
		bomb.setThrowException(true);

		FileSystemDeploymentTarget t = (FileSystemDeploymentTarget)registry.getTargets().get(TEST_TARGET);
		assertNotNull("sampleTarget null", t);
		List<FSDeploymentRunnable> prepare = new ArrayList<FSDeploymentRunnable>();
		prepare.add(new FSRunnableTester());
		prepare.add(new FSRunnableTester());
		prepare.add(bomb);
		prepare.add(new FSRunnableTester());
		t.setPrepare(prepare);
		
		String ticket = t.begin(TEST_TARGET, "wibble", 1, TEST_USER, TEST_PASSWORD);
		try 
		{
			t.prepare(ticket);
			fail("deployment exception not thrown");
		}
		catch (DeploymentException de)
		{
			// Should go here
			System.out.println(de.toString());
			de.printStackTrace();
		}
		finally
		{
			t.abort(ticket);	
		}
		
		/**
		 * Now turn off the bomb and make sure that deployment works.
		 */
		bomb.setThrowException(false);
		String ticket2 = t.begin(TEST_TARGET, "wibble", 1, TEST_USER, TEST_PASSWORD);
		try 
		{
			t.prepare(ticket2);
		}
		catch (DeploymentException de)
		{
			fail("deployment exception thrown");
		}
		finally
		{
			t.abort(ticket2);	
		}
		
		
		
	}

}
