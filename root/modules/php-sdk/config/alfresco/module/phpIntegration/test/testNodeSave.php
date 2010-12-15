<?php

	// Get the test nodes
	$node = $_ALF_MODEL["testNode"];
	assertNotNull($node, "testNode model value was found to be null");
	$folder = $_ALF_MODEL["testFolder"];
	assertNotNull($folder, "testFolder model value was found to be null");
	
	// Test changing the properties of the node
	$node->cm_name = "changed.txt";
	$node->cm_author = "Mr Trouble";
	
	// Add aspect
	$node->addAspect("cm_titled", array("cm_title" => "my title", "cm_description" => "my description"));
	
	// Remove aspect 
	$node->removeAspect("cm_versionable");
	
	// Create a child node
	$newNode1 = $folder->createChild("cm_content", "cm_contains", "cm_file.txt");
	$newNode1->cm_name = "file.txt";
	$newNode1->addAspect("sys_referenceable", array());
	
	// Create another child node
	$newNode2 = $folder->createChild("cm_folder", "cm_contains", "cm_testFolder");
	$newNode2->cm_name = "testFolder";
	
	// Add a non-primary child
	$newNode2->addChild($node, "cm_contains", "cm_myNode");
	
	// Associate the two nodes together
	$newNode1->addAssociation($newNode2, "cm_references");
	
	// Save the changes made
	$_ALF_SESSION->save();
	
	// Check the node have the correct values after the save
	assertEquals("changed.txt", $node->cm_name);
	assertEquals("Mr Trouble", $node->cm_author);
	assertTrue($node->hasAspect("cm_titled"));
	assertFalse($node->hasAspect("cm_versionable"));
	assertEquals("my title", $node->cm_title);
	assertEquals("my description", $node->cm_description);
	
	
	
?>