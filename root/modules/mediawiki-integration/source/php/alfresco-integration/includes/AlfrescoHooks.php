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
 
 // Register the various event hooks
$wgHooks['ArticleSave'][] = 'alfArticleSave';
$wgHooks['TitleMoveComplete'][] = 'alfTitleMoveComplete';
$wgHooks['UnknownAction'][] = 'alfCustomActions';
$wgHooks['PersonalUrls'][] = 'NoLogout';

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

function alfCustomActions($action, $article)
{
	if ($action == "createPage")
	{		
		$pageContent = "";
		if (isset($_REQUEST["pageContent"]) == true)
		{
			$pageContent = $_REQUEST["pageContent"];
		}
		
		// Create the new page with the given title		
		$editor = new EditPage( $article );
		$editor->save = true;
		$editor->textbox1 = urldecode($pageContent);
		$editor->attemptSave();
		
		return false;
	}
	else
	{
		return true;
	}
}

function NoLogout(&$personal_urls, $title) 
{
	$personal_urls['logout'] = null;
}
 
?>
