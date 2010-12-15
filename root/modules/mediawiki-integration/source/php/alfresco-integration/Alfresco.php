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
 	// Start the session
 	session_start();
 
 	// Create the log
 	$alfLog = new Logger();
 
 	// Debug parameter
	$alfDebug = true;
	$wgShowExceptionDetails = true;
	
	// Disable the cookie check
	$wgDisableCookieCheck = true;
	 
	// Include the alfresco extensions classes
	require_once("extensions/alfresco-integration/includes/AlfrescoHooks.php");
	require_once("extensions/alfresco-integration/includes/ExternalStoreAlfresco.php");	
	require_once("extensions/alfresco-integration/includes/AuthAlfresco.php");
	
	// Configure in some external stores
	$wgDefaultExternalStore = array("alfresco://localhost:8080/alfresco/api");
	$wgExternalStores = array("alfresco");
	
	// Configure in the Alfresco authentication
	$alfLog->debug("Alfresco - Setting authentication implementation");
	$wgAuth = new AuthAlfresco();	
	
	// Get the current mediaWiki space nodeReference
	$alfWikiSpaceNodeRef = null;
	if (isset($_REQUEST["mediaWikiSpace"]) == true)
	{
	 	$alfWikiSpaceNodeRef = $_REQUEST["mediaWikiSpace"];		
	 	$_SESSION["mediaWikiSpace"] = $alfWikiSpaceNodeRef;
	}
	else if (isset($_SESSION["mediaWikiSpace"]) == true)
	{
	 	$alfWikiSpaceNodeRef = $_SESSION["mediaWikiSpace"];	
	}
	
	// Create the login URL
	$loginURL = "extensions/alfresco-integration/login/AlfrescoLogin.php";
	$tempDelim = "?";
	if (isset($_REQUEST["title"]) == true)
	{
		$loginURL .= $tempDelim."title=".urlencode($_REQUEST["title"]);
		$tempDelim = "&";
	}
	if (isset($_REQUEST["action"]) == true)
	{
		$loginURL .= $tempDelim."action=".urlencode($_REQUEST["action"]);		
	}
	
	// Create the repository object
	$alfRepository = new Repository();
	
	// Set the other global values
	$alfTicket = null;
	$alfSession = null;
	$alfMediaWikiNode = null;
	$doLogin = false;
	
	// Check the request to see if we are being provided the ticket
	if (isset($_REQUEST["alfTicket"]) == true && isset($_REQUEST["alfUser"]) == true)
	{
		// Get the passes ticket and user name
		$passedTicket = $_REQUEST["alfTicket"];
		$passedUser = $_REQUEST["alfUser"];
		$doLogin = true;
		
		// Get the ticket from the request
		$alfTicket = $passedTicket;
		
		// Put the ticket into the session for later use
		$_SESSION["alfTicket"] = $alfTicket;
	}	
	else if (isset($_SESSION["alfTicket"]) == true)
	{
		// Get the ticket out of the session
		$alfTicket = $_SESSION["alfTicket"];
	} 
	
	// If we don't have a ticket redirect to the login page somehow
	if ($alfTicket == null)
	{	
		// Redirect to the login page
		header( "Location: ".$loginURL );
		exit;	
	}
	
	// Create an alfresco session that can be used
	$alfSession = $alfRepository->createSession($alfTicket);
 
	// Create a reference to the media wiki node
	if ($alfWikiSpaceNodeRef != null)
	{
		$alfMediaWikiNode = $alfSession->getNodeFromString($alfWikiSpaceNodeRef);
	}
	else
	{
		// Use the default wiki node
		$nodes = $alfSession->query(new SpacesStore($alfSession), "TYPE:\"mw:mediaWiki\"");	
		if (sizeof($nodes) == 0)
		{
			// Redirect to the login page, since we can't find the mediaWiki space (probably means incorrect permissions)
			header( "Location: ".$loginURL );
			exit;	
		}	
		$alfMediaWikiNode = $nodes[0];
		$alfWikiSpaceNodeRef = $alfMediaWikiNode->__toString();
	}

	// Validate the ticket (checks you have correct permissions on the wiki space and that the ticket is valid)
	if (MediaWikiSpace::validate($alfRepository, $alfWikiSpaceNodeRef, "", $alfTicket) == false)
	{
		// Redirect to the login page, since the ticket is knacked or you don't have permissions
		header( "Location: ".$loginURL );
		exit;	
	}
	
	// Set the configuration values
	eval(MediaWikiSpace::getEvaluationString($alfRepository, $alfWikiSpaceNodeRef));	

	// Make sure the setup is not included later
	define(MW_NO_SETUP, true);
	require_once("Setup.php");
	
	// Check to see if we should be doing an 'auto' login
	if ($doLogin == true)
	{		
		// Authenticate the mediawiki user
		$u = User::newFromName($passedUser);
		if (is_null($u) == false && User::isUsableName($u->getName()) == true)
		{
			// Check to see if the user already exists
			if (0 == $u->getID()) 
			{
				if ($wgAuth->autoCreate() == true && $wgAuth->userExists($u->getName()) == true) 
				{
					if ($wgAuth->authenticate($u->getName(), $passedTicket) == true) 
					{
						// Initialise the user
						$u->addToDatabase();
						$wgAuth->initUser( $u );
						$u->saveSettings();
					} 	
					else
					{
						// Can't authenticate the user based on the credentials provided
						$u = null;
					}
				}
				else
				{
					// Unable to auto create the user in media wiki
					$u = null;
				}
			}
			else
			{
				// Load the users details
				$u->load();	
			}
		}
		
		// Assuming we have found a user check the ticket
		if ($u != null && $u->checkPassword($passedTicket) == true) 
		{
			$wgAuth->updateUser( $u );
			$wgUser = $u;
			$wgUser->setCookies();
		}
	}
	
?>
