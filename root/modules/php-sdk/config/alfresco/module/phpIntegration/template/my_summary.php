<?php

	// Get the details from the passed model
	$person = $_ALF_MODEL["person"];
	$userHome = $_ALF_MODEL["userhome"];
	$companyHome = $_ALF_MODEL["companyhome"];
?>

<!-- Table of some summary details about the current user -->
<table>
   <tr><td><b>Name:</b></td> <td><?php echo $person->cm_firstName.$person->cm_lastName ?></td></tr>
   <tr><td><b>User:</b></td> <td><?php echo $person->cm_userName ?></td></tr>
   <tr><td><b>Home Space Name:</b></td> <td><?php echo $userHome->cm_name ?></td></tr>
   <tr><td><b>Items in Home Space:</b></td> <td><?php echo count($userHome->children) ?></td></tr>
   <tr><td><b>Items in Company Space:</b></td> <td><?php echo count($companyHome->children) ?></td></tr>
</table>
   


