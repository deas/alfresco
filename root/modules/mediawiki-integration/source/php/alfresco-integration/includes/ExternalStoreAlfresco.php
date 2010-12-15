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
 * External Alfresco content store.
 * 
 * This store retrieves and stores content from MediWiki into a space in a given Alfresco repository.
 */
class ExternalStoreAlfresco 
{
	public function __construct()
	{		
	}
	
	/**
	 * Fetch the content from the Alfresco repository.
	 * 
	 * @param	$url	the URL to the alfresco content
	 */
	public function fetchFromURL($url) 
	{
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
		global $alfSession, $alfMediaWikiNode;
		
		$url = $_SESSION["lastVersionUrl"];
		$node = null;
		
		$isNormalText = (strpos($url, 'alfresco://') === false);
		
		if ($url != null && $isNormalText == false)
		{
			$node = $this->urlToNode($url);	
		}
		else
		{
			$node = $alfMediaWikiNode->createChild("cm_content", "cm_contains", "cm_".$_SESSION["title"].".mw");
			$node->cm_name = $_SESSION["title"].".mw";
		
			$node->addAspect("cm_versionable", null);
			$node->cm_initialVersion = false;
			$node->cm_autoVersion = false;
		}
		
		// Set the content and save
		$node->updateContent("cm_content", "text/mediawiki", "UTF-8", $data);		
		$alfSession->save();
		
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
		global $alfSession, $alfMediaWikiNode;
		
		$values = explode("/", substr($url, 11));		
		return $alfSession->getNode($alfMediaWikiNode->store, $values[2]);	
	}
	
	/**
	 * Convert the url to the version it relates to
	 */
	private function urlToVersion($url)
	{
		global $alfSession;
		
		$values = explode("/", substr($url, 11));		
		$store  = $alfSession->getStore($values[4], $values[3]);
		return new Version($alfSession, $store, $values[5]);	
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
