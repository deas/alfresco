/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
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

package org.springframework.extensions.webscripts;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Random;
import java.util.StringTokenizer;

import junit.framework.TestCase;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.junit.Ignore;
import org.springframework.extensions.webscripts.connector.RemoteClient;
import org.springframework.extensions.webscripts.connector.Response;

/**
 * JUnit test for Remote Store REST API.
 * 
 * Requires that an Alfresco repo is running on http://localhost:8080/alfresco
 * and that the default remote store (an AVM store called 'sitestore') exists.
 * Therefore this test is not auto-run during the build process.
 * 
 * It is suggested that the TestRemoteClient test is executed before this test - 
 * as it requires the RemoteClient to perform the remote calls.
 * 
 * @author Kevin Roast
 */
@Ignore public class TestRemoteStore extends TestCase
{
   String TEST_CONTENT1 = "some test text";
   String TEST_CONTENT2 = "some text text - updated";
   
   public void testRemoteStore()
   {
      RemoteClient remote = new RemoteClient();
      remote.setEndpoint("http://localhost:8080/alfresco/s");
      
      Response response = remote.call("/api/login?u=admin&pw=admin");
      assertEquals(200, response.getStatus().getCode());
      String ticket = null;
      try
      {
          ticket = DocumentHelper.parseText(response.getResponse()).getRootElement().getTextTrim();
      }
      catch (DocumentException de)
      {
          fail("Failed to extract ticket from login API call.");
      }
      remote.setTicket(ticket);
      
      // POST with simple string body
      String filename0 = Long.toString((new Random().nextLong())) + ".txt";
      Response res = remote.call("/remotestore/create/" + filename0, TEST_CONTENT1);
      assertEquals(200, res.getStatus().getCode());
      
      // POST to a random sub-dir path
      String randdir = Long.toString((new Random().nextLong()));
      res = remote.call("/remotestore/create/" + randdir + "/" + filename0, TEST_CONTENT1);
      assertEquals(200, res.getStatus().getCode());
      
      // POST to a random sub-sub-dir path
      String randdir2 = randdir + "/" + Long.toString((new Random().nextLong()));
      res = remote.call("/remotestore/create/" + randdir2 + "/" + filename0, TEST_CONTENT1);
      assertEquals(200, res.getStatus().getCode());
      
      // POST a new file from an inputstream
      String filename = Long.toString((new Random().nextLong())) + ".txt";
      res = remote.call("/remotestore/create/" + filename, new ByteArrayInputStream(TEST_CONTENT1.getBytes()));
      assertEquals(200, res.getStatus().getCode());
      
      // get it back again into a response string
      res = remote.call("/remotestore/get/" + filename);
      assertEquals(200, res.getStatus().getCode());
      assertEquals(TEST_CONTENT1, res.getResponse());
      
      // get it back again into an output stream
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      res = remote.call("/remotestore/get/" + filename, out);
      assertEquals(200, res.getStatus().getCode());
      assertEquals(TEST_CONTENT1, out.toString());
      
      // test 'has' method - for true and false cases
      res = remote.call("/remotestore/has/" + filename);
      assertEquals(200, res.getStatus().getCode());
      assertEquals(res.getResponse(), "true");
      res = remote.call("/remotestore/has/_shouldnotexist.doc");
      assertEquals(200, res.getStatus().getCode());
      assertEquals(res.getResponse(), "false");
      
      // POST to update the file content
      res = remote.call("/remotestore/update/" + filename, new ByteArrayInputStream(TEST_CONTENT2.getBytes()));
      assertEquals(200, res.getStatus().getCode());
      
      // get it back again to confirm update
      out = new ByteArrayOutputStream();
      res = remote.call("/remotestore/get/" + filename, out);
      assertEquals(200, res.getStatus().getCode());
      assertEquals(TEST_CONTENT2, out.toString());
      
      // test 'get' fails with 404 when file does not exist
      res = remote.call("/remotestore/get/_shouldnotexist.doc");
      assertEquals(404, res.getStatus().getCode());
      
      // test listall on root of store
      res = remote.call("/remotestore/listall");
      assertEquals(200, res.getStatus().getCode());
      StringTokenizer t = new StringTokenizer(res.getResponse(), "\n");
      assertTrue(t.countTokens() != 0);
      
      // test list on the dir we made earlier
      res = remote.call("/remotestore/list/" + randdir);
      assertEquals(200, res.getStatus().getCode());
      t = new StringTokenizer(res.getResponse(), "\n");
      assertEquals(t.countTokens(), 1);
      
      // test listall on dir we made earlier
      res = remote.call("/remotestore/listall/" + randdir);
      assertEquals(200, res.getStatus().getCode());
      t = new StringTokenizer(res.getResponse(), "\n");
      assertEquals(t.countTokens(), 2);
      
      // test list fails with 404 when path does not exist
      res = remote.call("/remotestore/list/shouldnotexist");
      assertEquals(404, res.getStatus().getCode());
      
      // test listpattern on files we made earlier
      res = remote.call("/remotestore/listpattern/" + randdir + "?m=*.nomatch");
      assertEquals(200, res.getStatus().getCode());
      t = new StringTokenizer(res.getResponse(), "\n");
      assertTrue(t.countTokens() == 0);
      
      res = remote.call("/remotestore/listpattern/" + randdir + "?m=*.txt");
      assertEquals(200, res.getStatus().getCode());
      t = new StringTokenizer(res.getResponse(), "\n");
      assertTrue(t.countTokens() == 2);
   }
}
