/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of the Alfresco Web Quick Start module.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.alfresco.wcm.client.util.impl;

import org.alfresco.wcm.client.util.CmisSessionPool;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.commons.pool.ObjectPool;

/**
 * Facade for CMIS collection pool implementations
 * @author Chris Lack
 */
public class CmisSessionPoolImpl implements CmisSessionPool 
{
	private ObjectPool guestSessionPool;

	public CmisSessionPoolImpl(ObjectPool guestSessionPool)
	{
		this.guestSessionPool = guestSessionPool;
	}
	
	/**
	 * @see org.alfresco.wcm.client.util.CmisSessionPool#closeSession(Session)
	 */	
	@Override
	public synchronized void closeSession(Session session) throws Exception
	{
		guestSessionPool.returnObject(session);
	}

	/**
	 * @see org.alfresco.wcm.client.util.CmisSessionPool#getGuestSession()
	 */	
	@Override
	public synchronized Session getGuestSession() throws Exception
	{
		return (Session)guestSessionPool.borrowObject();		
	}

	/**
	 * @see org.alfresco.wcm.client.util.CmisSessionPool#getSession(String, String)
	 */		
	@Override
	public synchronized Session getSession(String username, String password)
	{
		throw new UnsupportedOperationException("Repository authenticated sessions not yet supported by this class");
	}
	
	
}
