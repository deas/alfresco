/*
 * Copyright (C) 2009-2010 Alfresco Software Limited.
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
package org.alfresco.deployment.impl.dmr;

import junit.framework.TestCase;

public class StoreNameMapperImplTest extends TestCase
{
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
    
    /**
     * 
     */
    public void testMapProjectNames()
    {
        StoreNameMapperImpl impl = new StoreNameMapperImpl();
        
        impl.setConsolidate(true);
        
        assertEquals("test", impl.mapProjectName("test--admin"));
        assertEquals("test", impl.mapProjectName("test--admin--edchkjeh"));
        assertEquals("test", impl.mapProjectName("test"));
        
        impl.setConsolidate(false);
        assertEquals("test--admin", impl.mapProjectName("test--admin"));   
    }
}
