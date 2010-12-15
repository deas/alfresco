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
 
	/**
	 * Basic Tutorial Five - Create Content
	 * 
	 * In the tutorial we will build a simple form that will allow us to create new content nodes in the 
	 * Guest Home space.
	 * 
	 * A discusion of this tutorial can be found at http://wiki.alfresco.com/wiki/PHP_Tutorial_Five
	 * 
	 * Note: any changes to this file should be uploaded to the wiki
	 */ 
  
	// Include the required Alfresco PHP API objects  
	if (isset($_SERVER["ALF_AVAILABLE"]) == false)
    {
    	require_once "Alfresco/Service/Repository.php";
		require_once "Alfresco/Service/Session.php";
		require_once "Alfresco/Service/SpacesStore.php";
    }

	// Specify the connection details
	$repositoryUrl = "http://localhost:8080/alfresco/api";
	$userName = "admin";
	$password = "admin"; 
	
	// Authenticate the user and create a session
	$repository = new Repository($repositoryUrl);
	$ticket = $repository->authenticate($userName, $password);
	$session = $repository->createSession($ticket);
	
	// Create a reference to the 'SpacesStore'
	$spacesStore = new SpacesStore($session);
	
	// Use a serach to get the guest home space we will use to place the new content in
	$nodes = $session->query($spacesStore, "PATH:\"app:company_home/app:guest_home\"");
	$guestHome = $nodes[0]; 
	
	// Update the property if the form has been posted with the correct details
	if (isset($_REQUEST["create"]) == true)
	{
		// Get the name of the new node
		$name = $_REQUEST["name"];
		
		// Create the new content node
		//  - parameter one is the type of node we want to create 
		//  - parameter two is the association type, this should be contains for normal files and folders
		//  - parameter three is the association name, this should be the name of the file or folder under normal
		//    circumstances
		$contentNode = $guestHome->createChild("cm_content", "cm_contains", "cm_".$name);
		
		// Add the titles aspect to the new node so that the title and description properties can be set
		$contentNode->addAspect("cm_titled", null);
		
		// Set the name, title and description property values
		$contentNode->cm_name = $name;
		$contentNode->cm_title = $_REQUEST["title"];
		$contentNode->cm_description = $_REQUEST["description"];
		
		// Set the content onto the standard content property for nodes of type cm:content.
		// We are going to assume the mimetype and encoding for ease
		$contentNode->updateContent("cm_content", "text/plain", "UTF-8", $_REQUEST["text"]);
		
		// Save the new node
		$session->save();
	}
?>

<html>

<head>
	<title>Basic Tutorial Five - Create Content Node</title>
</head>

<body>
    <big>Basic Tutorial Five - Create Content Node</big><br><br>
    
    <form action="BasicTutorial5-CreateContentNode.php" method="post"">
    
    	<input type=hidden name=create value="true">
        
	    <table border=0 cellpadding=2 cellspacing=3> 
	    	<tr>
	    		<td>Name:</td>
	    		<td><input name="name" type=edit size=50></td>
	    	</tr>
	    	<tr>
	 			<td>Title:</td>
	 			<td><input name="title" type=edit size=50></td>
	 		</tr> 		
	    	<tr>
	 			<td>Description:</td>
	 			<td><input name="description" type=edit size=50></td>
	 		</tr> 			
	    	<tr>
	 			<td>Text:</td>
	 			<td><textarea name="text" cols="50" rows="5" maxlength="250"></textarea></td>
	 		</tr> 	
	    	<tr>
	 			<td></td>
	 			<td align=right><input type=submit value="Create Node"></td>
	 		</tr> 	 	
	 	</table>
	
	</form>
	
<?php    
   if (isset($_REQUEST["create"]) == true)
   {
	  // Calculate the url to the properties of the new node
	  $uiUrl = "http://".$repository->host.":".$repository->port."/alfresco/n/showDocDetails/workspace/SpacesStore/".$contentNode->id;
	
?>
	  <p style="color:red">The new content node has been created.  Go to <a target="new" href="<?php echo $uiUrl ?>">here</a> to view its properties.</p>
<?php
   }    
?>	
	
</body>

</html>
