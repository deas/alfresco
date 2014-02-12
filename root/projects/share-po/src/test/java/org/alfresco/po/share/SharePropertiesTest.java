package org.alfresco.po.share;

import org.testng.Assert;
import org.testng.annotations.Test;

public class SharePropertiesTest 
{
    @Test
    public void createProperties()
    {
        ShareProperties prop = new ShareProperties();
        Assert.assertNotNull(prop);
        Assert.assertEquals(prop.getVersion(), "alfresco-share");
        Assert.assertEquals(prop.getLocale(), "en");
    }
    @Test
    public void createPropertiesWithFrenchLocale()
    {
        ShareProperties prop = new ShareProperties("Enterprise41","fr");
        Assert.assertNotNull(prop);
        Assert.assertEquals(prop.getVersion(), "Enterprise41");
        Assert.assertEquals(prop.getLocale(), "fr");
    }
    @Test
    public void createSharePropertiesVersion41()
    {
        ShareProperties prop = new ShareProperties("Enterprise41");
        Assert.assertNotNull(prop);
        Assert.assertEquals(prop.getVersion(), "Enterprise41");
    }
    @Test
    public void createSharePropertiesVersion42()
    {
        ShareProperties prop = new ShareProperties("Enterprise42");
        Assert.assertNotNull(prop);
        Assert.assertEquals(prop.getVersion(), "Enterprise42");
    }
    @Test
    public void createSharePropertiesWithNull()
    {
        ShareProperties prop = new ShareProperties(null);
        Assert.assertNotNull(prop);
        Assert.assertEquals(prop.getVersion(), AlfrescoVersion.Share);
    }
    @Test(expectedExceptions=IllegalArgumentException.class)
    public void createSharePropertiesWithNull2()
    {
        new ShareProperties("",null);
    }
    @Test(expectedExceptions=IllegalArgumentException.class)
    public void createSharePropertiesWithBlanks()
    {
        new ShareProperties("","");
    }
}
