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
 
require_once("AlfrescoConfig.php"); 

if (isset($_SERVER["ALF_AVAILABLE"]) == false)
{
	require_once("Alfresco/Service/Session.php");
	require_once("Alfresco/Service/SpacesStore.php");
	require_once("Alfresco/Service/Node.php");
	require_once("Alfresco/Service/Version.php");
}

// TODO .. for now remove this as it it not available when running within Quercus
//require_once("Alfresco/Service/Logger/Logger.php");

// Register the various event hooks
$wgHooks['ArticleSave'][] = 'alfArticleSave';
$wgHooks['TitleMoveComplete'][] = 'alfTitleMoveComplete';

/**
 * Hook function called before content is saved.  At this point we can extract information about the article
 * and store it on the session to be used later.
 */
function alfArticleSave(&$article, &$user, &$text, &$summary, $minor, $watch, $sectionanchor, &$flags)
{
	// Execute a query to get the previous versions URL, we can use this later when we save the content
	// and want to update the version history.
	$url = null;
	$fieldName = "old_text";
	$revision = Revision::newFromId($article->mLatest);
	if (isset($revision) == true)
	{
		$dbw =& $article->getDB();
		$row = $dbw->selectRow( 'text',
					array( 'old_text', 'old_flags' ),
					array( 'old_id' => $revision->getTextId() ),
					"ExternalStoreAlfresco::alfArticleSave");
		$url = $row->$fieldName;
	}
	
	// Store the details of the article in the session
	$_SESSION["title"] = ExternalStoreAlfresco::getTitle($article->getTitle());	
	$_SESSION["description"] = $summary;
	$_SESSION["lastVersionUrl"] = $url;
	
	// Returning true ensures that the document is saved
	return true;
}

function alfTitleMoveComplete(&$title, &$newtitle, &$user, $pageid, $redirid)
{
	//$logger = new Logger("integration.mediawiki.ExternalStoreAlfresco");
	
	//if ($logger->isDebugEnabled() == true)
	//{
	//	$logger->debug("Handling title move event");
	//	$logger->debug(	  "title=".ExternalStoreAlfresco::getTitle($title).
	//				    "; newTitle=".ExternalStoreAlfresco::getTitle($newtitle).
	//					"; user=".$user->getName().
	//					"; pageid=".$pageid.		// is page_id on page table
	//					"; redirid=".$redirid);
	//}
	
	// Do summert :D
}

/**
 * External Alfresco content store.
 * 
 * This store retrieves and stores content from MediWiki into a space in a given Alfresco repository.
 */
class ExternalStoreAlfresco 
{
	//private $logger;
	private $session;
	private $store;
	private $wikiSpace;
	
	public function __construct()
	{
		global $alfURL, $alfUser, $alfPassword, $alfWikiStore, $alfWikiSpace;
		
		//$this->logger = new Logger("integration.mediawiki.ExternalStoreAlfresco");
		
		// Create the session
		$repository = new Repository($alfURL);
		$ticket = $repository->authenticate($alfUser, $alfPassword);
		$this->session = $repository->createSession($ticket);
		
		// Get the store
		$this->store = $this->session->getStoreFromString($alfWikiStore);
		
		// Get the wiki space
		$results = $this->session->query($this->store, 'PATH:"'.$alfWikiSpace.'"');
	    $this->wikiSpace = $results[0];
	}
	
	/**
	 * Fetch the content from the Alfresco repository.
	 * 
	 * @param	$url	the URL to the alfresco content
	 */
	public function fetchFromURL($url) 
	{
		//$session = $this->getSession();
		$version = $this->urlToVersion($url);		
		return $version->cm_content->content;
	}

	/**
	 * Stores the provided content in the Alfresco repository
	 * 
	 * @param	$store	the external store
	 * @param	$data	the content
	 */
	public function &store($store, $data) 
	{
		$url = $_SESSION["lastVersionUrl"];
		$node = null;
		
		$isNormalText = (strpos($url, 'alfresco://') === false);
		
		if ($url != null && $isNormalText == false)
		{
			$node = $this->urlToNode($url);	
		}
		else
		{
			$node = $this->wikiSpace->createChild("cm_content", "cm_contains", "cm_".$_SESSION["title"]);
			$node->cm_name = $_SESSION["title"];
		
			$node->addAspect("cm_versionable", null);
			$node->cm_initialVersion = false;
			$node->cm_autoVersion = false;
		}
		
		// Set the content and save
		$node->updateContent("cm_content", "text/plain", "UTF-8", $data);		
		$this->session->save();
		
		$description = $_SESSION["description"];
		if ($description == null)
		{
			$description = "";
		}
		
		// Create the version
		$version = $node->createVersion($description);
		
		$result = "alfresco://".$node->store->scheme."/".$node->store->address."/".$node->id."/".$version->store->scheme."/".$version->store->address."/".$version->id;		
		return $result;		
	}
	
	/**
	 * Convert the url to the the node it relates to
	 */
	private function urlToNode($url)
	{
		$values = explode("/", substr($url, 11));		
		return $this->session->getNode($this->store, $values[2]);	
	}
	
	/**
	 * Convert the url to the version it relates to
	 */
	private function urlToVersion($url)
	{
		$values = explode("/", substr($url, 11));		
		$store  = $this->session->getStore($values[4], $values[3]);
		return new Version($this->session, $store, $values[5]);	
	}
	
    public static function getTitle($titleObject)
    {
    	// Sort out the namespace of this article so we can figure out what the title is
		$title = $titleObject->getText();
		$ns = $titleObject->getNamespace();
		if ($ns != NS_MAIN)
		{
			// lookup the display name of the namespace
			$title = Namespace::getCanonicalName($ns)." - ".$title;
		}	
		return $title;
    }
}

?>
