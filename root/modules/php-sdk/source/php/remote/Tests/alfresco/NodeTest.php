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
 
require_once('BaseTest.php');
require_once('../Alfresco/Service/NamespaceMap.php');
require_once('../Alfresco/Service/Node.php');

class NodeTest extends BaseTest
{		
	const TITLE = "This is a title";
	const DESCRIPTION = "This is a description";
	
	private static $fileName1;
	private static $fileName2;
	private static $folderName1;
	private static $newNode;
	private static $newNode2;
	private static $newFolderNode;
		
	public function __construct($name) 
	{
		parent :: __construct($name);
		
		if (self::$fileName1 == null)
		{
		   self::$fileName1 = "myDoc_".time().".txt";
		   self::$fileName2 = "myDoc2_".time().".txt";
	 	   self::$folderName1 = "folder1".time();
		}
	}

	public function testCreateNode()
	{
		// Create to root directory by hand
		$rootId = $this->getStore()->rootNode->id;
		$node = $this->getSession()->getNode($this->getStore(), $rootId);
		
		// Check the details of the node
		$this->assertNotNull($node);
		$this->assertEquals($rootId, $node->id);
		
		$this->assertNotNull($node->aspects);
		$this->assertTrue(in_array("{http://www.alfresco.org/model/system/1.0}aspect_root", $node->aspects));		
	}
	
	public function testCreateNewNodes()
	{
		$root = $this->getStore()->rootNode;
				
		// Create content node 		
		self::$newNode = $this->getCompanyHome()->createChild(
							"cm_content", 
							"cm_contains", 
						    "cm_".self::$fileName1);						    
		self::$newNode->cm_name = self::$fileName1;				
		self::$newNode->addAspect("cm_titled");
		self::$newNode->cm_title = NodeTest::TITLE;
		self::$newNode->cm_description = NodeTest::DESCRIPTION;
		
		// Create a new folder node
		self::$newFolderNode = $this->getCompanyHome()->createChild(
							"cm_folder",
							"cm_contains",
							"cm_".self::$folderName1);
		self::$newFolderNode->cm_name = self::$folderName1;
		
		// Create another content node in the folder just created
		self::$newNode2 = $this->getCompanyHome()->createChild(
							"cm_content", 
							"cm_contains", 
						    "cm_".self::$fileName2);						    
		self::$newNode2->cm_name = self::$fileName2;				
		self::$newNode2->addAspect("cm_titled");
		self::$newNode2->cm_title = NodeTest::TITLE;
		self::$newNode2->cm_description = NodeTest::DESCRIPTION;
		
		// Save the newly created nodes
		$this->getSession()->save();		
		
		// Do a couple of sanity checks to ensure that the nodes have been created correctly
		$this->assertEquals(NodeTest::TITLE, self::$newNode->cm_title);
		// TODO add more tests ...
		
	}
	
	public function testGetSetProperties()
	{
		$properties = self::$newNode->properties;
		$this->assertNotNull($properties);
		$this->assertEquals(NodeTest::TITLE, $properties[$this->getSession()->namespaceMap->getFullName("cm_title")]);
		$this->assertEquals(NodeTest::DESCRIPTION, $properties[$this->getSession()->namespaceMap->getFullName("cm_description")]);
	
		$properties[$this->getSession()->namespaceMap->getFullName("cm_title")] = "updatedTitle";
		$properties[$this->getSession()->namespaceMap->getFullName("cm_description")] = "updatedDescription";
		self::$newNode->properties = $properties;
		$this->getSession()->save();
				
		$properties = self::$newNode->properties;	
		$this->assertNotNull($properties);
		$this->assertEquals("updatedTitle", $properties[$this->getSession()->namespaceMap->getFullName("cm_title")]);
		$this->assertEquals("updatedDescription", $properties[$this->getSession()->namespaceMap->getFullName("cm_description")]);	
		
		$this->assertEquals("updatedTitle", self::$newNode->cm_title);
		$this->assertEquals("updatedDescription", self::$newNode->cm_description);
		
		self::$newNode->cm_title = NodeTest::TITLE;
		self::$newNode->cm_description = NodeTest::DESCRIPTION;
		$this->getSession()->save();
		
		$this->assertEquals(NodeTest::TITLE, self::$newNode->cm_title);
		$this->assertEquals(NodeTest::DESCRIPTION, self::$newNode->cm_description);
		
		// Check system properties
		//echo "Node dbid: ".self::$newNode->sys_node_dbid."<br>";
		$this->assertNotNull(self::$newNode->sys_node_dbid);
		$this->assertNotNull(self::$newNode->sys_node_uuid);
		$this->assertNotNull(self::$newNode->sys_store_identifier);
		
	}
	
	public function testAddRemoveAspect()
	{
		$aspects = self::$newNode->aspects;
		$this->assertNotNull($aspects);
		$this->assertTrue(in_array($this->getSession()->namespaceMap->getFullName("cm_titled"), $aspects));	
		$this->assertFalse(in_array($this->getSession()->namespaceMap->getFullName("cm_versionable"), $aspects));

		self::$newNode->addAspect("cm_versionable", array("cm_autoVersion" => "false", "cm_initialVersion" => "false"));
		$this->getSession()->save();
		
		$aspects = self::$newNode->aspects;
		$this->assertNotNull($aspects);
		$this->assertTrue(in_array($this->getSession()->namespaceMap->getFullName("cm_titled"), $aspects));
		$this->assertTrue(in_array($this->getSession()->namespaceMap->getFullName("cm_versionable"), $aspects));
		$this->assertEquals("false", self::$newNode->cm_autoVersion);
		$this->assertEquals("false", self::$newNode->cm_initialVersion);
		
		$aspects = self::$newNode->removeAspect("cm_versionable");
		$this->getSession()->save();
		
		$aspects = self::$newNode->aspects;
		$this->assertNotNull($aspects);
		$this->assertTrue(in_array($this->getSession()->namespaceMap->getFullName("cm_titled"), $aspects));	
		$this->assertFalse(in_array($this->getSession()->namespaceMap->getFullName("cm_versionable"), $aspects));
	}
	
	public function testAddRemoveChildren()
	{
		
	}
	
	public function testAssociations()
	{
		$associations = self::$newNode->associations;
		$this->assertNotNull($associations);
		$this->assertEquals(0, count($associations));
		
		self::$newNode->addAspect("cm_referencing");
		self::$newNode->addAssociation(self::$newNode2, "cm_references");	
		$associations = self::$newNode->associations;
		$this->assertNotNull($associations);
		$this->assertEquals(1, count($associations));
		$association = $associations[self::$newNode2->__toString()];
		$this->assertNotNull($association);
		$this->assertEquals(self::$newNode->__toString(), $association->from->__toString());
		$this->assertEquals(self::$newNode2->__toString(), $association->to->__toString());
		$this->assertEquals("{http://www.alfresco.org/model/content/1.0}references", $association->type);
		
		$this->getSession()->save();
		
		$associations = self::$newNode->associations;
		$this->assertNotNull($associations);
		$this->assertEquals(1, count($associations));
		$association = $associations[self::$newNode2->__toString()];
		$this->assertNotNull($association);
		$this->assertEquals(self::$newNode->__toString(), $association->from->__toString());
		$this->assertEquals(self::$newNode2->__toString(), $association->to->__toString());
		$this->assertEquals("{http://www.alfresco.org/model/content/1.0}references", $association->type);
		
	}
	
	// NOTE: make sure this test occures last as it clears the session and re-gets the node
	public function testNodeRefesh()
	{
		$id = self::$newNode->id;
		$this->getSession()->clear();
		$node = $this->getSession()->getNode($this->getStore(), $id);
		
		$aspects = $node->aspects;
		$this->assertNotNull($aspects);
		$this->assertTrue(in_array($this->getSession()->namespaceMap->getFullName("cm_titled"), $aspects));	
		$this->assertFalse(in_array($this->getSession()->namespaceMap->getFullName("cm_versionable"), $aspects));
		
		$associations = $node->associations;
		$this->assertNotNull($associations);
		$this->assertEquals(1, count($associations));
		$association = $associations[self::$newNode2->__toString()];
		$this->assertNotNull($association);
		$this->assertEquals($node->__toString(), $association->from->__toString());
		$this->assertEquals(self::$newNode2->__toString(), $association->to->__toString());
		$this->assertEquals("{http://www.alfresco.org/model/content/1.0}references", $association->type);
	}
}

?>

