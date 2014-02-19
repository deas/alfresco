/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.po.share;

import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups={"unit"})
public class AlfrescoVersionTest
{
    @Test
    public void createFromString()
    {
        AlfrescoVersion enterprise = AlfrescoVersion.fromString("Enterprise");
        Assert.assertEquals(AlfrescoVersion.Enterprise, enterprise);
        AlfrescoVersion enterprise41 = AlfrescoVersion.fromString("Enterprise-41");
        Assert.assertEquals(AlfrescoVersion.Enterprise41, enterprise41);
        AlfrescoVersion enterprise42 = AlfrescoVersion.fromString("Enterprise-42");
        Assert.assertEquals(AlfrescoVersion.Enterprise42, enterprise42);
        AlfrescoVersion cloud = AlfrescoVersion.fromString("Cloud");
        Assert.assertEquals(AlfrescoVersion.Cloud, cloud);
        AlfrescoVersion cloud2 = AlfrescoVersion.fromString("Cloud2");
        Assert.assertEquals(AlfrescoVersion.Cloud2, cloud2);
        
        AlfrescoVersion lowerCasecloud2 = AlfrescoVersion.fromString("cloud2");
        Assert.assertEquals(AlfrescoVersion.Cloud2, lowerCasecloud2);
        
        AlfrescoVersion myAlfresco = AlfrescoVersion.fromString("myalfresco");
        Assert.assertEquals(AlfrescoVersion.MyAlfresco, myAlfresco);
    }
    @Test
    public void isCloud()
    {
        boolean cloud = AlfrescoVersion.Cloud.isCloud();
        boolean notCloud = AlfrescoVersion.Enterprise42.isCloud();
        Assert.assertEquals(true, cloud);
        Assert.assertEquals(false, notCloud);
    }
    @Test
    public void isDojoSupported()
    {
        Assert.assertEquals(true, AlfrescoVersion.Enterprise42.isDojoSupported());
        Assert.assertEquals(false, AlfrescoVersion.Enterprise41.isDojoSupported());
        Assert.assertEquals(false, AlfrescoVersion.Cloud.isDojoSupported());
        Assert.assertEquals(true, AlfrescoVersion.Cloud2.isDojoSupported());
        Assert.assertEquals(true, AlfrescoVersion.MyAlfresco.isDojoSupported());
    } 
    @Test
    public void createFromNull()
    {
        Assert.assertEquals(AlfrescoVersion.fromString(null), AlfrescoVersion.Share);
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void createFromInvalidString()
    {
        AlfrescoVersion.fromString("FakeSite");
    }
}
