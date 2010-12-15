<?php
	
	// Tests the global values set my the script/template engine, both those added by the
	// engine and those added from the model
	
	// Check the repository value
	assertNotNull($_ALF_REPOSITORY, "_ALF_REPOSITORY was found to be null");  
	$newSession = $_ALF_REPOSITORY->createSession();
	assertNotNull($newSession, "newSession was found to be null");
	
	// Check the session value
	assertNotNull($_ALF_SESSION, "_ALF_SESSION was found to be null");
	assertNotNull($_ALF_SESSION->stores);
	assertNotNull($_ALF_SESSION->ticket);
	
	// Check that the model is present
	assertNotNull($_ALF_MODEL, "_ALF_MODEL was found to be null");
	
	// Check the node value set in the model and passed through
	assertNotNull($_ALF_MODEL["testNode"], "testNode was found to be null");
	assertEquals($_ALF_MODEL["nodeId"], $_ALF_MODEL["testNode"]->id);
	
	// Check the store ref value passed through
	assertNotNull($_ALF_MODEL["testStore"], "testStore was found to be null");
	assertEquals($_ALF_MODEL["storeId"], $_ALF_MODEL["testStore"]->address);
	
	// Check the other values set in the model
	assertEquals("testString", $_ALF_MODEL["testString"]);
	assertEquals(1.0, $_ALF_MODEL["testNumber"]);
		  
?>
