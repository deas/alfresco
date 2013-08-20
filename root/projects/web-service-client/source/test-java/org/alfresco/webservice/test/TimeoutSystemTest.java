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
package org.alfresco.webservice.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.alfresco.webservice.content.Content;
import org.alfresco.webservice.repository.UpdateResult;
import org.alfresco.webservice.types.CML;
import org.alfresco.webservice.types.CMLCreate;
import org.alfresco.webservice.types.ContentFormat;
import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.ParentReference;
import org.alfresco.webservice.types.Predicate;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.ContentUtils;

/**
 * Timeout test for webservices
 * NOTE: set the timeout in web.xml to one minute for these tests to provide meaningful results
 * 
 * @author Roy Wetherall
 */
public class TimeoutSystemTest extends BaseWebServiceSystemTest
{
   public void testContentService() throws Exception
   {
       // Create the parent reference
       ParentReference parentRef = new ParentReference();
       parentRef.setStore(BaseWebServiceSystemTest.store);
       parentRef.setUuid(BaseWebServiceSystemTest.rootReference.getUuid());
       parentRef.setAssociationType(Constants.ASSOC_CHILDREN);
       parentRef.setChildName(Constants.ASSOC_CHILDREN);
       
       // Create the content
       NamedValue[] properties = new NamedValue[]{new NamedValue(Constants.PROP_NAME, false, "test.jpg", null)};
       CMLCreate create = new CMLCreate("1", parentRef, null, null, null, Constants.TYPE_CONTENT, properties);
       CML cml = new CML();
       cml.setCreate(new CMLCreate[]{create});
       UpdateResult[] result = this.repositoryService.update(cml);  
       Reference myCreatedNode = result[0].getDestination();
       
       // Wait
       //Thread.sleep(140*1000);
       
       // Use the download content
       ContentFormat format = new ContentFormat("image/jpeg", "UTF-8");  
       InputStream viewStream = getClass().getClassLoader().getResourceAsStream("org/alfresco/webservice/test/resources/test.jpg");
       byte[] bytes = ContentUtils.convertToByteArray(viewStream);
       Content myContent = this.contentService.write(myCreatedNode, Constants.PROP_CONTENT, bytes, format);
       ContentUtils.copyContentToFile(myContent, File.createTempFile("temp", "jpg"));
       
       // Use the upload content
       InputStream viewStream2 = getClass().getClassLoader().getResourceAsStream("org/alfresco/webservice/test/resources/test.jpg");
       File testFile = File.createTempFile("temp", "jpg");
       FileOutputStream fos = new FileOutputStream(testFile);
       ContentUtils.copy(viewStream2, fos);
       viewStream.close();
       fos.close();
       ContentUtils.putContent(testFile);
       
       // Do stuff with the web service api keeping the session alive and making sure the ticket is not invalidated
       for (int i = 0; i < 5; i++)
       {
           this.repositoryService.get(new Predicate(new Reference[]{myCreatedNode},null, null));
           
           // Wait 30 seconds
           Thread.sleep(30*1000);           
       }
              
       // Wait
       //Thread.sleep(140*1000);
       
       // See if we can still issue a web service request
       //this.repositoryService.get(new Predicate(new Reference[]{myCreatedNode}, null, null));
   }      
}
