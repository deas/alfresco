<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>PHPUnit2 : HTML Runner</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

<link rel="stylesheet" type="text/css" href="assets/stylesheets/global.css" />
<link rel="stylesheet" type="text/css" href="assets/stylesheets/testResults.css" />
<script type="text/javascript" src="assets/scripts/schedule.js"></script>
<script type="text/javascript" src="assets/scripts/prototype.js"></script>
<script type="text/javascript" src="assets/scripts/history.js"></script>

</head>
<body>

<div id="outerContentWrap">
	<h1>PHPUnit2 : HTML Runner</h1>
	
	<div id="testFormWrap">
		<h2>Enter test(s) to run:</h2>
		<?php include('testForm.php'); ?>
	</div>

	<div id="innerContentWrap">	
		<?php print $output['body']; ?>
	</div>
	
	<div id="footer">
		PHPUnit2 : HTMLRunner v<?php print $version['version'].' ('.$version['state'].'), '.date('d M Y', strtotime($version['date'])); ?>
	</div>
	
</div>

</body>
</html>