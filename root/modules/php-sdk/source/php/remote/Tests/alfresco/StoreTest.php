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
require_once('Alfresco/Service/Store.php');
require_once('Alfresco/Service/SpacesStore.php');

class StoreTest extends BaseTest
{	
	public function testCreate()
	{	
		$store = new Store($this->getSession(), "SpacesStore");
		
		$this->assertEquals("workspace", $store->scheme);
		$this->assertEquals("SpacesStore", $store->address);
		$this->assertEquals("workspace://SpacesStore", $store->__toString());		
	}
	
	public function testRootNode()
	{
		$store = new Store($this->getSession(), "SpacesStore");
		$root = $store->rootNode;
		$this->assertNotNull($root, "root node unexpectedly null");
		$this->assertNotNull($root->id, "root node id unexpectedly null");
		$this->assertEquals($store->__toString(), $root->store->__toString());	
	}
	
	public function testSpacesStore()
	{
		$store = new SpacesStore($this->getSession());
		$this->assertNotNull($store->companyHome);
		$this->assertEquals("Company Home", $store->companyHome->cm_name);
	}
	
	public function testFromString()
	{
		$storeRef = "workspace://SpacesStore";
		$store = $this->getSession()->getStoreFromString($storeRef);
		
		$this->assertNotNull($store);
		$this->assertEquals("workspace", $store->scheme);
		$this->assertEquals("SpacesStore", $store->address);
	}
	
	public function testCreateNewStore()
	{
		$storeName = "testStore".time();
		$store = $this->getSession()->createStore($storeName);
		$this->assertNotNull($store);
		$this->assertEquals($storeName, $store->address);
		$this->assertEquals("workspace", $store->scheme);		
		
		$bHasStore = false;
		foreach ($this->getSession()->stores as $temp)
		{
			if ($temp->address == $storeName)
			{
				$bHasStore = true;
			}
		}
		if ($bHasStore == false)
		{
			$this->fail("Store not found in list.");
		}
		
		$storeName2 = "testStore".time()."1";
		$store2 = $this->getSession()->createStore($storeName2);
		$this->assertNotNull($store2);
		$this->assertEquals($storeName2, $store2->address);
		$this->assertEquals("workspace", $store2->scheme);		
		
		$bHasStore2 = false;
		foreach ($this->getSession()->stores as $temp2)
		{
			if ($temp2->address == $storeName2)
			{
				$bHasStore2 = true;
			}
		}
		if ($bHasStore2 == false)
		{
			$this->fail("Store not found in list.");
		}
	}
}

?>

