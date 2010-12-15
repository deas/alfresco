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
require_once('Alfresco/Service/Session.php');

class SessionTest extends BaseTest
{	
	public function testSessionDetails()
	{
		$session = $this->getSession();
		
		$this->assertNotNull($session->repository, "repository was unexpectedly null");
		$this->assertNotNull($session->ticket, "ticket was unexpectedly null");
		$this->assertNotNull($session->namespaceMap, "namespaceMap was unexpectedly null");
	}
	
	public function testStores()
	{
		$stores = $this->getSession()->stores;
		$this->assertNotNull($stores);
		$this->assertTrue(count($stores) > 1);
		
		$foundSpacesStore = false;
		foreach ($stores as $store)
		{
			if ($store->address == "SpacesStore")
			{
				$foundSpacesStore = true;
			}
		}
		
		if ($foundSpacesStore == false)
		{
			$this->fail("The spaces store was not found when querying the stores of a perticular session.");
		}
	}
	
	public function testQuery()
	{
		$nodes = $this->getSession()->query($this->getStore(), 'TEXT:"Alfresco"');
		$this->assertNotNull($nodes);
		// TODO we don't know how many results to expect!
		// TODO maybe some additional tests to ensure the nodes are correctly formed
		
		$nodes2 = $this->getSession()->query($this->getStore(), 'PATH:"app:company_home"');
		$this->assertNotNull($nodes2);
		$this->assertTrue(1 == count($nodes2));
		
		$nodes3 = $this->getSession()->query($this->getStore(), 'PATH:"app:junk"');
		$this->assertNotNull($nodes3);
		$this->assertTrue(0 == count($nodes3));
	}
	
	public function testClear()
	{
		// We've just a load of queries so the node cache should have some stuff in it
		$this->getSession()->clear();
		
		// Do another query
		$nodes = $this->getSession()->query($this->getStore(), 'PATH:"app:company_home"');
		
		// Get a propery value to ensure the node can be populated
		$this->assertEquals("Company Home", $nodes[0]->cm_name);
	}

}

?>

