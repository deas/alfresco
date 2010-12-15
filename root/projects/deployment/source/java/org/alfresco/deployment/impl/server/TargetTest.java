package org.alfresco.deployment.impl.server;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.deployment.FSDeploymentRunnable;
import org.alfresco.deployment.impl.DeploymentException;

import junit.framework.TestCase;

public class TargetTest extends TestCase 
{
	/**
	 * @param name
	 */
	public TargetTest(String name) 
	{
		super(name);
	}
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testGetFileForPath()
	{

	}
	
	public void testGetListing()
	{

	}
	
//	public void testConstructor()
//	{
//		String name="testTarget";
//		String root="hellmouth";
//		String metadata="metadata";
//		String user="Master";	
//		String password="vampire";
//		
//		Target t = new FileSystemDeploymentTarget(name, metadata);
//		
//		assertTrue("name not equal", t.getName().equals(name));
//		assertTrue("meta not equal", t.getMetaDataDirectory().equals(metadata));
////		assertTrue("user not equal", t.getUser().equals(user));
////		assertTrue("password not equal", t.getPassword().equals(password));
//			
//	}
	
	public void testAutoFix()
	{
	
	}
	
	public void testValidateMetaData()
	{
	
	}
	
	
}
