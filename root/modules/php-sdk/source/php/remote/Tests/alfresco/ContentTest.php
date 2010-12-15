<?php
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
 
require_once ('BaseTest.php');
require_once ("Alfresco/Service/SpacesStore.php");
require_once ('Alfresco/Service/ContentData.php');

class ContentTest extends BaseTest 
{
	const CONTENT = "this is some test content.  And this is some more content.";

	private static $_contentNode;
	
	private function getContentNode()
	{
		if (self::$_contentNode == null)
		{
			// Create a new content node in the company home
			$this->fileName = "1myDoc_" . time() . ".txt";
			self::$_contentNode = $this->getCompanyHome()->createChild(
																"cm_content", 
																"cm_contains", 
																"app_" .$this->fileName);
			self :: $_contentNode->cm_name = $this->fileName;
	
			// Add titled aspect for UI				
			self::$_contentNode->addAspect("cm_titled");
			self::$_contentNode->cm_title = "This is my new document.";
			self::$_contentNode->cm_description = "This describes what is in the document.";
	
			// Save new content
			$this->getSession()->save();	
		}
		return self::$_contentNode;
	}

	public function checkCreatedNode() 
	{
		$this->assertNotNull($this->getContentNode());
		// TODO ... maybe should have a standard check for a newly created node!	
	}

	public function testCheckNullContent() 
	{
		$this->assertNull($this->getContentNode()->cm_content);
	}

	public function testSetContent() 
	{
		$this->getContentNode()->updateContent("cm_content", "text/plain", "UTF-8", ContentTest :: CONTENT);		
		$this->assertNotNull($this->getContentNode()->cm_content);
		
		try
		{
			$this->getSession()->save();
		}
		catch (Exception $e)
		{
			echo $e->getTraceAsString();
			throw $e;
		}
	}

	public function testReadContentDetails() 
	{
		$this->assertEquals("text/plain", $this->getContentNode()->cm_content->mimetype);
		$this->assertEquals("UTF-8", $this->getContentNode()->cm_content->encoding);
		$this->assertEquals(strlen(ContentTest::CONTENT), $this->getContentNode()->cm_content->size);
	}

	public function testGetUrls() 
	{
		$url = $this->getContentNode()->cm_content->url;
		$this->assertNotNull($url);
		if (strpos($url, "ticket") === false) 
		{
			$this->fail("Invalid content URL");
		}

		$guestUrl = $this->getContentNode()->cm_content->guestUrl;
		$this->assertNotNull($guestUrl);
		if (strpos($guestUrl, "guest") === false) 
		{
			$this->fail("Invalid guest URL");
		}
	}

	public function testGetContent() 
	{
		$content = $this->getContentNode()->cm_content->content;
		$this->assertNotNull($content);
		$this->assertEquals(strlen($content), $this->getContentNode()->cm_content->size);
		$this->assertEquals(ContentTest::CONTENT, $content);
	}
	
	public function testWriteContentFromFile()
	{
		$contentData = $this->getContentNode()->cm_content;
		$contentData->mimetype = "image/jpeg";
		$contentData->encoding = "UTF-8";
		$contentData->writeContentFromFile("alfresco/resources/quick.jpg");
		
		$this->getContentNode()->cm_content = $contentData;
		$this->getContentNode()->cm_name = "1myDoc_" . time() . ".jpg";
		$this->getSession()->save();
	}
	
	public function testReadContentToFile()
	{
		$contentData = $this->getContentNode()->cm_content;
		$contentData->readContentToFile("alfresco/resources/temp.jpg");	
	}
	
	public function testContentCreationAndPopulation()
	{
		$session = $this->getSession();
		$store = new SpacesStore($session);
		$fileName = "2myDoc_" . time() . ".txt";
		
		$node = $store->getCompanyHome()->createChild(
									"cm_content", 
									"cm_contains", 
									"cm_" .$fileName);
	    $node->cm_name = $fileName;
	    $node->updateContent("cm_content", "text/plain", "UTF-8", "testTESTtest");
	    
	    $session->save();	    
	    $nodeId = $node->id;
	    
	    // Create a new session and get the same node back
	    $ticket = $this->getRepository()->authenticate("admin", "admin");
	    $session2 = $this->getRepository()->createSession($ticket);
	    
	    $store2 = new SpacesStore($session2);
	    $node2 = $session2->getNode($store2, $nodeId);
	    
	    //echo "node: ".$node2->__toString()."<BR>";
	    
	    $contentData = $node2->cm_content;
		$this->assertNotNull($contentData);
	    $this->assertEquals("text/plain", $contentData->mimetype);
	    $this->assertEquals("UTF-8", $contentData->encoding);
	    $this->assertEquals(12, $contentData->size);
	    $this->assertEquals("testTESTtest", $contentData->content);		
	}
}
?>

