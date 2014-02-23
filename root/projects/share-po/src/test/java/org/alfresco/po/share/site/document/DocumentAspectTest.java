/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.po.share.site.document;

import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

@Test(groups="unit")
public class DocumentAspectTest 
{
    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void getAspectWithNull() throws Exception
    {
        DocumentAspect.getAspect(null);
    }
    
    @Test(dependsOnMethods="getAspectWithNull", expectedExceptions=UnsupportedOperationException.class)
    public void getAspectWithEmptyName() throws Exception
    {
        DocumentAspect.getAspect("");
    }
    @Test(dependsOnMethods="getAspectWithEmptyName", expectedExceptions=Exception.class)
    public void getAspectWithWrongName() throws Exception
    {
        DocumentAspect.getAspect("Alfresco");
    }
    @Test(dependsOnMethods="getAspectWithWrongName", expectedExceptions=Exception.class)
    public void getAspect() throws Exception
    {
        assertEquals(DocumentAspect.getAspect("Alfresco"), DocumentAspect.AUDIO);
    }
}
