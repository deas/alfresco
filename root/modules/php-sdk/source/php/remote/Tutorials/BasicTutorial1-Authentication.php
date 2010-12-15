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
	 * Basic Tutorial One - Authentication
	 * 
	 * In the tutorial we will authenticate against the repository and create a new session object.
	 * 
	 * A discusion of this tutorial can be found at http://wiki.alfresco.com/wiki/PHP_Tutorial_One.
	 * 
	 * Note: any changes to this file should be uploaded to the wiki
	 */ 
  
    // Use the ALF_AVAILABLE value to make this script work remotely and locally
  	if (isset($_SERVER["ALF_AVAILABLE"]) == false)
    {
		// Include the required Alfresco PHP API objects  
		require_once "Alfresco/Service/Repository.php";
		require_once "Alfresco/Service/Session.php";
    }

	// The web service end point URL for the Alfresco repository
	$repositoryUrl = "http://localhost:8080/alfresco/api";
	
	// The user credentials we are going to use
	$userName = "admin";
	$password = "admin"; 
	
	// Create the repository object baseed on the repository URL.  The repository represents the Alfresco repository we are connecting to.
	// It can be used to authenticate users and as a session factory.
	$repository = new Repository($repositoryUrl);
	
	// Authenticate the user details for the repository we want to connect to.  Assuming authentication is succesful we will be returned
	// a ticket.  The ticket is a string which can be used to create Session's
	$ticket = $repository->authenticate($userName, $password);
	
	// Using the ticket we can now create a session.  In general it is better to create a new session for a new page or page sequence rather 
	// than creating one and using across many pages as this helps to prevent resource usage from creaping up.
	$session = $repository->createSession($ticket);  
?>

<html>

<head>
	<title>Basic Tutorial One - Authentication</title>
</head>

<body>
    <big>Basic Tutorial One - Authentication</big>
	<p>Connected to repository <?php echo $repositoryUrl ?> as user <?php echo $userName ?> with ticket <?php echo $ticket ?></p>
</body>

</html>
