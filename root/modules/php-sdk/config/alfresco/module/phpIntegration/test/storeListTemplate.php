<?php
    $repository = new Repository();
    $stores = $repository->createSession()->stores;
?>
<html>

<head>
</head>

<body>
	<h1>List of stores</h1>
	<ul>	
<?php	
	foreach ($stores as $store)
    {
    	echo "<li>".$store->scheme."://".$store->address."</li>\n";
    }
?>    
	</ul>

</body>

</html>