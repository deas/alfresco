<?php

	// Get the test nodes
	$node = $_ALF_MODEL["testNode"];
	assertNotNull($node, "testNode model value was found to be null");
	$folder = $_ALF_MODEL["testFolder"];
	assertNotNull($folder, "testFolder model value was found to be null");
		
	// Get all properties
	$properties = $node->properties;
	assertNotNull($properties);	
	echo "Properties:\n";
	foreach ($properties as $fullName=>$value)
	{
		echo "   - ".$fullName."=>".$value."\n";	
	}
	assertEquals("testNode.txt", $properties["{http://www.alfresco.org/model/content/1.0}name"]);
	assertEquals("Roy Wetherall", $properties["{http://www.alfresco.org/model/content/1.0}author"]);
	// TODO checks on nodeRefs, dates, etc ....
	
	// Check the dynamic property read
	assertEquals("testNode.txt", $node->cm_name);
	assertEquals("Roy Wetherall", $node->cm_author);
	assertNull($node->cm_junk);
	assertNull($node->junk);
	assertNull($node->cm_title);	
	
	// Check the system properties
	assertNotNull($node->sys_node_dbid);
	assertNotNull($node->sys_node_uuid);
	assertNotNull($node->sys_store_identifier);
	
	// Check aspects and hasAspect
	$aspects = $node->aspects;
	assertNotNull($aspects);
	echo "Aspects: \n";
	foreach ($aspects as $aspect)
	{
		echo "   - ".$aspect."\n";
	}
	assertTrue($node->hasAspect("{http://www.alfresco.org/model/content/1.0}versionable"));
	assertTrue($node->hasAspect("{http://www.alfresco.org/model/content/1.0}classifiable"));
	assertTrue($node->hasAspect("cm_versionable"));
	assertTrue($node->hasAspect("cm_classifiable"));
	assertFalse($node->hasAspect("{http://www.alfresco.org/model/content/1.0}titled"));
	assertFalse($node->hasAspect("cm_titled"));
	
	// Check children
	$children = $folder->children;
	assertNotNull($children, "children was not expected to be null");
	assertEquals(2, count($children));
	echo "Children: \n";
	foreach ($children as $childAssoc)
	{
		assertNotNull($childAssoc->parent, "the parent of the association was unexpectedly null");
		assertNotNull($childAssoc->child, "the child of the association was unexpectedly null");
		assertEquals($folder->id, $childAssoc->parent->id);
		assertEquals("{http://www.alfresco.org/model/content/1.0}contains", $childAssoc->type);
		echo "   - type:".$childAssoc->type."; parent:".$childAssoc->parent->id."; child:".$childAssoc->child->id."; isPrimary=".$childAssoc->isPrimary."\n";
	}
	
	// Check parents
	$parents = $node->parents;
	assertNotNull($parents, "parents was not expected to be null");
	assertEquals(1, count($parents));
	echo "Parents: \n";
	foreach ($parents as $childAssoc)
	{
		assertNotNull($childAssoc->parent, "the parent of the association was unexpectedly null");
		assertNotNull($childAssoc->child, "the child of the association was unexpectedly null");
		assertEquals($node->id, $childAssoc->child->id);
		assertEquals("{http://www.alfresco.org/model/content/1.0}contains", $childAssoc->type);
		echo "   - type:".$childAssoc->type."; parent:".$childAssoc->parent->id."; child:".$childAssoc->child->id."; isPrimary=".$childAssoc->isPrimary."\n";	
	}
	
	// Check associations
	$associations = $node->associations;
	assertNotNull($associations, "associations was not expected to be null");
	assertEquals(1, count($associations));
	echo "Associations: \n";
	foreach ($associations as $assoc)
	{
		assertNotNull($assoc->from, "the from of the association was unexpectedly null");
		assertNotNull($assoc->to, "the to of the association was unexpectedly null");
		assertEquals($node->id, $assoc->from->id);
		assertEquals("{http://www.alfresco.org/model/content/1.0}references", $assoc->type);
		echo "   - type:".$assoc->type."; from:".$assoc->from->id."; to:".$assoc->to->id."\n";
	}
	
	// Check content property
	$contentData = $node->cm_content;
	assertNotNull($contentData, "contentData was unexpectedly null");
	assertEquals("UTF-8", $contentData->encoding);
	assertEquals("text/plain", $contentData->mimetype);	
	assertNotNull($contentData->url , "url was unexpectedly null");
	assertNotNull($contentData->guestUrl, "guestUrl was unexpectedly null");
	echo "Content Data: \n";
	echo "   - url: ".$contentData->url."\n";
	echo "   - guest Url: ".$contentData->guestUrl."\n";
	
	// Check set property
	$props = $node->properties;
	$props["{http://www.alfresco.org/model/content/1.0}name"] = "test1.txt";
	$props["{http://www.alfresco.org/model/content/1.0}author"] = "Mr Blobby";
	$node->properties = $props;
	assertEquals("test1.txt", $node->cm_name);
	assertEquals("Mr Blobby", $node->cm_author);
		
	// Check setPropertyValues
	$propertyValues = array("{http://www.alfresco.org/model/content/1.0}name" => "test2.txt",
							"{http://www.alfresco.org/model/content/1.0}author" => "Mr Cod");
	$node->setPropertyValues($propertyValues);
	assertEquals("test2.txt", $node->cm_name);
	assertEquals("Mr Cod", $node->cm_author);	
	
	// Check setting properties using dynamic attributes
	$node->cm_name = "test3.txt";
	$node->cm_author = "Ms Sunshine";
	assertEquals("test3.txt", $node->cm_name);
	assertEquals("Ms Sunshine", $node->cm_author);
	
	// Check add and remove aspect
	$node->addAspect("cm_titled", array("cm_title" => "my title",
										"cm_description" => "my description"));
	assertTrue($node->hasAspect("cm_titled"));										
	assertTrue(in_array("{http://www.alfresco.org/model/content/1.0}titled", $node->aspects));										
	assertEquals("my title", $node->cm_title); 
	assertEquals("my description", $node->cm_description);
	$node->removeAspect("cm_versionable");
	assertFalse($node->hasAspect("cm_versionable"));										
	assertFalse(in_array("{http://www.alfresco.org/model/content/1.0}versionable", $node->aspects));	
	
	// Check create node
	$newNode = $folder->createChild("cm_folder", "cm_contains", "{http://www.alfresco.org/model/content/1.0}My Test Folder");
	$newNode->cm_name = "My Test Folder";
	
	assertNotNull($newNode);
	assertEquals(3, count($folder->children));
	assertEquals(1, count($newNode->parents));
	$newChildAssoc = $folder->children[2];
	assertEquals($folder->__toString(), $newChildAssoc->parent->__toString());
	assertEquals($newNode->__toString(), $newChildAssoc->child->__toString());
	assertEquals("{http://www.alfresco.org/model/content/1.0}contains", $newChildAssoc->type);
	assertEquals("{http://www.alfresco.org/model/content/1.0}My Test Folder", $newChildAssoc->name);
	assertTrue($newChildAssoc->isPrimary);
	assertEquals("{http://www.alfresco.org/model/content/1.0}folder", $newNode->type);
	assertEquals("My Test Folder", $newNode->cm_name);
	assertTrue(strpos($newNode->id, 'new_') !== false);
	
	// Add child
	$folder->addChild($node, "cm_contains", "cm_summertOrNuffin");
	
	assertEquals(4, count($folder->children));
	assertEquals(2, count($node->parents));
	$addedChildAssoc = $node->parents[1];
	assertEquals($folder->__toString(), $addedChildAssoc->parent->__toString());
	assertEquals($node->__toString(), $addedChildAssoc->child->__toString());
	assertEquals("{http://www.alfresco.org/model/content/1.0}contains", $addedChildAssoc->type);
	assertEquals("{http://www.alfresco.org/model/content/1.0}summertOrNuffin", $addedChildAssoc->name);
	assertFalse($addedChildAssoc->isPrimary);
	
	// Remove child
	$folder->removeChild($addedChildAssoc);
	
	assertEquals(3, count($folder->children));
	assertEquals(1, count($node->parents));
	
	// Add association
	$folder->addAssociation($node, "cm_summertOrNuffin");
	
	assertEquals(1, count($folder->associations));
	$associationOne = $folder->associations[0];
	assertEquals($folder, $associationOne->from);
	assertEquals($node, $associationOne->to);
	assertEquals("{http://www.alfresco.org/model/content/1.0}summertOrNuffin", $associationOne->type);
		
	// Remove association
	$association = $node->associations[0];
	$node->removeAssociation($association);
	
	assertEquals(0, count($node->associations));
	
	// Check isSubTypeOf
	assertTrue($node->isSubTypeOf("cm_content"));
	assertTrue($folder->isSubTypeOf("cm_folder"));
	assertTrue($newNode->isSubTypeOf("cm_folder"));
	assertFalse($folder->isSubTypeOf("cm_content"));
		
?>