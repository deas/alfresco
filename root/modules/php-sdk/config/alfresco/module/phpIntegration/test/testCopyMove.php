<?php

	// Get the test nodes
	$node = $_ALF_MODEL["testNode"];
	assertNotNull($node, "testNode model value was found to be null");
	$folder = $_ALF_MODEL["testFolder"];
	assertNotNull($folder, "testFolder model value was found to be null");
		
	// Preliminary test
	assertTrue($node->hasAspect("cm_titled"));
	assertNotNull($node->cm_title, "title property is null");
	
	// Make a copy
	$copy = $node->copy($folder, "cm_contains", "cm_copy1", true);
	assertNotNull($copy);	
	assertEquals($node->cm_title, $copy->cm_title);
	
	// Make a change and try and copy
	$node->cm_title = "My Other Title";
	// TODO .. need to figure out how exceptions can be caught and handled if required
	//try
	//{
	//	$copy2 = $node->copy($folder, "cm_contains", "cm_copy2", true);
//		fail("Should not be allowed to copy a node if there are outstanding changes.");
	//}
	//catch (Exception $e)
	//{
		// Expected
	//}
	
	// Save the changes and make sure we can now copy the node
	$_ALF_SESSION->save();
	$copy3 = $node->copy($folder, "cm_contains", "cm_copy3", true);
	assertNotNull($copy3);
	assertEquals($node->cm_title, $copy3->cm_title);
	
	// Create another folder to move the node into
	$destination = $folder->createChild("cm_folder", "cm_contains", "cm_destination");
	$destination->cm_name = "destination";
	$_ALF_SESSION->save();
	
	// Do the move
	assertEquals($folder->__toString(), $copy->primaryParent->__toString(), "Copy has unexpected initial primary parent");
	$copy->move($destination, "cm_contains", "cm_move1");
	assertEquals($destination->__toString(), $copy->primaryParent->__toString(), "Moved copy does not have the expected primary parent");
		
?>