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
