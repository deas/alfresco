<?php
	
	$session = $_ALF_SESSION;
	$space = $_ALF_MODEL["space"];
	$document = $_ALF_MODEL["document"];

	// Find the backup folder - create if it does not already exist
	$backupFolder = null;
	$queryStatement = "+PARENT:\"".$space->__toString()."\" +@cm\:name:\"Backup\"";
	$nodes = $session->query($document->store, $queryStatement);
	
	if (count($nodes) == 0)
	{
		if ($document->hasPermission('CreateChildren') == true)
		{	
			// Create the backup folder if it could not be found
			$backupFolder = $space->createChild("cm_folder", "cm_contains", "cm_Backup");
			$backupFolder->cm_name = "Backup";
			$session->save();
		}
	}
	else
	{
		$backupFolder = $nodes[0];
	}
	
	if ($backupFolder != null && $backupFolder->hasPermission('CreateChildren') == true)
	{
		// Create the backup and place it in the backup folder
		$copy = $document->copy($backupFolder, "cm_contains", "cm_".$document->cm_name);
		$copy->cm_name = "Backup of ".$copy->cm_name;
		$session->save();
	}

?>