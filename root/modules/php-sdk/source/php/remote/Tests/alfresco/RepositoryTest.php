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
require_once('Alfresco/Service/Repository.php');

class RepositoryTest extends BaseTest
{	

	public function testRepositoryCreate()
	{
		$repository = new Repository();
		$this->assertEquals("http://localhost:8080/alfresco/api", $repository->connectionUrl);	
		$this->assertEquals("localhost", $repository->host);
		$this->assertEquals(8080, $repository->port);	
		
		$repository = new Repository("http://myserver:1001/alfresco/api");
		$this->assertEquals("http://myserver:1001/alfresco/api", $repository->connectionUrl);
		$this->assertEquals("myserver", $repository->host);
		$this->assertEquals(1001, $repository->port);	
	}

	public function testAuthentication()
	{
		$repository = new Repository();
		
		$ticket = $repository->authenticate("admin", "admin");
		$this->assertNotNull($ticket);
		
		try
		{
			$ticket2 = $repository->authenticate("bad", "bad");
			$this->fail("Expected exception when bad credentials provided");		
		}
		catch (Exception $exception)
		{
			//echo "the exception message: ".$exception->getTraceAsString();
		}
	}
	
	public function testCreateSession()
	{
		$repository = new Repository();
		$ticket = $repository->authenticate("admin", "admin");
		
		$session = $repository->createSession($ticket);
		$this->assertNotNull($session);
		$this->assertEquals("http://localhost:8080/alfresco/api", $session->repository->connectionUrl);
		$this->assertEquals($ticket, $session->ticket);
		
		// TODO for now if no ticket is provided a null session is returned
		$session2 = $repository->createSession();
		$this->assertNull($session2);		
	}
}

?>

