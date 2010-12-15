<?php

$document = $_ALF_MODEL["document"];

if ($document->hasPermission("Write") == true)
{
	if ($document->cm_content->mimetype == "text/plain")
	{
		$document->cm_content->content .= "\r\n\r\nCopyright (C) 2006";
	}
	else if ($document->cm_content->mimetype == "text/html")
	{
		$document->cm_content->content .= "<br><br><small>Copyright &copy; 2006</small>";
	}
	
	$_ALF_SESSION->save();
}

?>