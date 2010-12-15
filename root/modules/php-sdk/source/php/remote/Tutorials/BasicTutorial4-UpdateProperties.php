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
	 * Basic Tutorial Four - Update Properties
	 * 
	 * In the tutorial we will build a simple form that updates the title and description properties of a 
	 * content node.
	 * 
	 * A discusion of this tutorial can be found at http://wiki.alfresco.com/wiki/PHP_Tutorial_Four
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
	
	// Use a serach to get the content node we are updating
	$nodes = $session->query($spacesStore, "PATH:\"app:company_home/app:guest_home/cm:Alfresco-Tutorial.pdf\"");
	$contentNode = $nodes[0]; 
	
	// Update the property if the form has been posted with the correct details
	if (isset($_REQUEST["update"]) == true)
	{
		// Set the updated title and decription values
		$contentNode->cm_title = $_REQUEST["title"];
		$contentNode->cm_description = $_REQUEST["description"];
		
		// Save the session.  This ensures that the updates are presisted back to the repository.
		// If this save call is not made the changes made will be lost when the session object is destroyed.
		$session->save();
	}
?>

<html>

<head>
	<title>Basic Tutorial Four - Updating Properties</title>
</head>

<body>
    <big>Basic Tutorial Four - Updating Properties</big><br><br>
    
    <form action="BasicTutorial4-UpdateProperties.php" method="post"">
    
    	<input type=hidden name=update value="true">
        
	    <table border=0 cellpadding=2 cellspacing=3> 
	    	<tr>
	    		<td>Name:</td>
	    		<td><?php echo $contentNode->cm_name ?></td>
	    	</tr>
	    	<tr>
	 			<td>Title:</td>
	 			<td><input name=title type=edit size=50 value="<?php echo $contentNode->cm_title ?>"></td>
	 		</tr> 		
	    	<tr>
	 			<td>Description:</td>
	 			<td><input name=description type=edit size=50 value="<?php echo $contentNode->cm_description ?>"></td>
	 		</tr> 		
	    	<tr>
	 			<td></td>
	 			<td align=right><input type=submit value="Update Node"></td>
	 		</tr> 	 	
	 	</table>
	
	</form>
	
</body>

</html>
