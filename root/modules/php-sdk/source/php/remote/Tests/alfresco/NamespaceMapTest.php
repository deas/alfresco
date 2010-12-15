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
 
require_once('Alfresco/Service/NamespaceMap.php');

class NamespaceMapTest extends PHPUnit2_Framework_TestCase
{	
   	public function testGetShortName()
   	{
   		$namespaceMap = new NamespaceMap();
   		$shortName1 = $namespaceMap->getShortName("{http://www.alfresco.org/model/content/1.0}name");
		$this->assertEquals("cm_name", $shortName1);
		$shortName2 = $namespaceMap->getShortName("{http://www.alfresco.org/model/system/1.0}uuid");
		$this->assertEquals("sys_uuid", $shortName2);
		$shortName3 = $namespaceMap->getShortName("{junk}name");
		$this->assertEquals("{junk}name", $shortName3);
		$shortName4 = $namespaceMap->getShortName("{junk}");
		$this->assertEquals("{junk}", $shortName4);
		$shortName5 = $namespaceMap->getShortName("blar_blar");
		$this->assertEquals("blar_blar", $shortName5);
   	}

	public function testIsShortName()
	{
   		$namespaceMap = new NamespaceMap();
		$this->assertTrue($namespaceMap->isShortName("cm_name"));
		$this->assertTrue($namespaceMap->isShortName("sys_uuid"));
		$this->assertFalse($namespaceMap->isShortName("bob_name"));
		$this->assertFalse($namespaceMap->isShortName("bob"));
		$this->assertTrue($namespaceMap->isShortName("sys_some_prop"));
		$this->assertFalse($namespaceMap->isShortName("sys_"));
	}
	
	public function testGetFullName()
	{
   		$namespaceMap = new NamespaceMap();	   	
   		$fullName1 = $namespaceMap->getFullName("cm_name");
		$this->assertEquals("{http://www.alfresco.org/model/content/1.0}name", $fullName1);
		$fullName2 = $namespaceMap->getFullName("sys_uuid");
		$this->assertEquals("{http://www.alfresco.org/model/system/1.0}uuid", $fullName2);
		$fullName3 = $namespaceMap->getFullName("bob_name");
		$this->assertEquals("bob_name", $fullName3);
		$fullName4 = $namespaceMap->getFullName("bob");
		$this->assertEquals("bob", $fullName4);
		$fullName5 = $namespaceMap->getFullName("sys_some_prop");
		$this->assertEquals("{http://www.alfresco.org/model/system/1.0}some-prop", $fullName5);
		$fullName6 = $namespaceMap->getFullName("sys_");
		$this->assertEquals("sys_", $fullName6);		
	}
}

?>

