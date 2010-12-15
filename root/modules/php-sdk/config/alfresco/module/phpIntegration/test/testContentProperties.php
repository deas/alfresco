<?php

	// Get the test nodes
	$folder = $_ALF_MODEL["testFolder"];
	assertNotNull($folder, "testFolder model value was found to be null");
	
	$node = $folder->createChild("cm_content", "cm_contains", "cm_myFile.txt");
	$node->cm_name = "myFile.txt";
	
	$contentData = $node->cm_content;
	assertNull($contentData, "No content data has been set so should be null");
	
	$contentData2 = $node->updateContent("cm_content", "text/plain", "UTF-8", null);
	assertNotNull($contentData2, "The newly created content data is unexpectedly null");
	assertEquals("text/plain", $contentData2->mimetype);
	assertEquals("UTF-8", $contentData2->encoding);
	assertNull($contentData2->content);
	
	$contentData2->content = "this is some content";
	
	$contentData3 = $node->cm_content;
	assertNotNull($contentData3, "content data 3 was null");
	assertEquals("this is some content", $contentData3->content, "content was incorrect before save");
	
	$_ALF_SESSION->save();
	
	$contentData4 = $node->cm_content;
	assertNotNull($contentData4, "content data 4 was null");	
	assertEquals("this is some content", $contentData4->content, "content was incorrect after save");
			
?>