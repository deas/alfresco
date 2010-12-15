<?php

	// Get the namespace map
	$namespaceMap = $_ALF_SESSION->namespaceMap;
	assertNotNull($namespaceMap, "The namespace map was unexpectedly null");
	
	// Try converting short names
	$fullName1 = $namespaceMap->getFullName("cm_name");
	assertEquals("{http://www.alfresco.org/model/content/1.0}name", $fullName1);
	$fullName2 = $namespaceMap->getFullName("sys_uuid");
	assertEquals("{http://www.alfresco.org/model/system/1.0}uuid", $fullName2);
	$fullName3 = $namespaceMap->getFullName("bob_name");
	assertEquals("bob_name", $fullName3);
	$fullName4 = $namespaceMap->getFullName("bob");
	assertEquals("bob", $fullName4);
	$fullName5 = $namespaceMap->getFullName("sys_some_prop");
	assertEquals("{http://www.alfresco.org/model/system/1.0}some-prop", $fullName5);
	$fullName6 = $namespaceMap->getFullName("sys_");
	assertEquals("sys_", $fullName6);
	
	// Try getting short names
	$shortName1 = $namespaceMap->getShortName("{http://www.alfresco.org/model/content/1.0}name");
	assertEquals("cm_name", $shortName1);
	$shortName2 = $namespaceMap->getShortName("{http://www.alfresco.org/model/system/1.0}uuid");
	assertEquals("sys_uuid", $shortName2);
	$shortName3 = $namespaceMap->getShortName("{junk}name");
	assertEquals("{junk}name", $shortName3);
	$shortName4 = $namespaceMap->getShortName("{junk}");
	assertEquals("{junk}", $shortName4);
	$shortName5 = $namespaceMap->getShortName("blar_blar");
	assertEquals("blar_blar", $shortName5);
	
	// Test for short names
	assertTrue($namespaceMap->isShortName("cm_name"));
	assertTrue($namespaceMap->isShortName("sys_uuid"));
	assertFalse($namespaceMap->isShortName("bob_name"));
	assertFalse($namespaceMap->isShortName("bob"));
	assertTrue($namespaceMap->isShortName("sys_some_prop"));
	assertFalse($namespaceMap->isShortName("sys_"));

?>