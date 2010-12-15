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
 
require_once('PHPUnit2/Framework/TestCase.php');
require_once('Alfresco/Service/Session.php');
require_once('Alfresco/Service/Repository.php');
require_once('Alfresco/Service/Store.php');

class BaseTest extends PHPUnit2_Framework_TestCase
{
   const USERNAME = "admin";
   const PASSWORD = "admin";
   const URL = "http://localhost:8080/alfresco/api";
  
   private static $_repository;
   private static $_session; 
   private static $_companyHome;  
   
   protected function getStore()
   {
      return new Store($this->getSession(), "SpacesStore");	
   }
   
   protected function getRepository()
   {
      if (self::$_repository == null)
      {
      	 // Create the repository
      	 self::$_repository = new Repository(BaseTest::URL);
      }		
      return self::$_repository;
   }
   
   protected function getSession()
   {
   	  if (self::$_session == null)
   	  {
   	     // Start the Alfresco session
   	     $ticket = $this->getRepository()->authenticate(BaseTest::USERNAME, BaseTest::PASSWORD);
	     self::$_session = $this->getRepository()->createSession($ticket);
   	  }
   	  return self::$_session;
   }
   
   protected function getCompanyHome()
   {
   	   if (self::$_companyHome == null)
   	   {
   	   	  $nodes = $this->getSession()->query($this->getStore(), 'PATH:"app:company_home"');
	           self::$_companyHome = $nodes[0]; 
   	   }
   	   return self::$_companyHome;
   }
   
   protected function createContentNode($content="Some simple content")
   {
   	   try
   	   {
	   		// Create a new content node in the company home
			$fileName = "myDoc_" . time() . ".txt";
			$contentNode = $this->getCompanyHome()->createChild(
															"cm_content", 
															"cm_contains", 
															"app_" .$fileName);
			$contentNode->cm_name = $fileName;
			$contentNode->updateContent("cm_content", "text/plain", "UTF-8", $content);
			
			// Save new content
			$this->getSession()->save();
   	   }
   	   catch (Exception $e)
   	   {
   	      echo "createContentNode Exception: ".$e->getMessage()."<br>";
   	      echo "createContentNode Trace: ".$e->getTraceAsString()."<br>";    
   	      throw $e;		   	   
   	   }	
		
	   return $contentNode;
   	}
}
?>
