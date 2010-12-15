<?php

$document = $_ALF_MODEL["document"];

if ($document != null)
{	
?>
   <h4>Current Document Info:</h4>
   <b>Name:</b> <?php echo($document->cm_name); ?><br>
   <b>Ref:</b> <?php echo($document->__toString()); ?><br>   
   <b>Type:</b> <?php echo($document->type); ?><br>	
   <b>DBID:</b> <?php echo($document->sys_node_dbid); ?><br> 
   <b>Content:</b> <a href="<?php echo $document->cm_content->url ?>">Click here to view content</a><br>
   
<?php 
	if ($document->hasAspect("cm_countable") == true)
	{
?>
		<b>Counter:</b> <?php echo($document->cm_counter); ?><br>	
<?php
	}
?>	
   <b>Aspects:</b>
   <table>
<?php
	foreach ($document->aspects as $aspect)
	{
?>
		<tr><td><?php echo($aspect); ?></td></tr>
<?php
	}   
?>
   </table>
   
   <b>Properties:</b>
   <table>
<?php
	foreach ($document->properties as $name=>$value)
	{	
		echo("<tr><td>".$name." = ".$value."</td></tr>");
	}
?>    
   </table>

<?php
}
else
{
	echo "No document found!";
}
?>
   


