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
require_once ('Alfresco/Service/Version.php');

class VersionTest extends BaseTest 
{
	public function testVersion() 
	{
		// First create a new content node
		$node = $this->createContentNode("origional content");
		$node->addAspect("cm_titled");
		$node->cm_title = "origional title";
		$node->cm_description = "origional description";
		$this->getSession()->save();
		
		// Try and version the content
		$version = $node->createVersion();
		
		// Do some checks!
		$this->assertTrue($node->hasAspect("cm_versionable"));
		$this->assertEquals("1.0", $node->cm_versionLabel);
		
		// Make some more modifications 
		$node->title = "changed title";
		$node->description = "changed description";
		$node->cm_content->content = "changed content";
		$this->getSession()->save();
		
		// Check that we can still retireve the versioned content and property values
		$this->assertEquals("origional title", $version->cm_title);
		$this->assertEquals("origional description", $version->cm_description);	
		echo "content: ".$version->cm_content->content."<br>";
		echo "content: ".$node->cm_content->content."<br>";
	}

	public function testVersionHistory()
	{
		// First create a new content node
		$node = $this->createContentNode("origional content");
		$node->addAspect("cm_titled");
		$node->cm_title = "origional title";
		$node->cm_description = "origional description";
		$this->getSession()->save();
		
		// Version the content a couple of times ...	
		$version1 = $node->createVersion();
		$version2 = $node->createVersion();
		$version3 = $node->createVersion();
		
		// Sanity check
		$this->assertTrue($node->hasAspect("cm_versionable"));
		$this->assertEquals("1.2", $node->cm_versionLabel);
		
		// Get the version history
		$versionHistory = $node->versionHistory;
	}
	
}
?>

