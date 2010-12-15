<?php

$version = array(
	'version'=>'0.5.1',
	'state'=>'beta',
	'date'=>'2006-03-12');

$output = array();
$action = '';
if(isSet($_POST['action'])) $action = $_POST['action'];
else if(isSet($_GET['action'])) $action = $_GET['action'];

// create the history util
include_once('util/History.php');
$historyUtil = new util_History(dirname(__FILE__).'/assets/testHistory.txt');

ob_start();
switch($action) {
	case('runTests') :
		include_once('actions/runTests.php');
		$historyUtil->addToCache($_POST['testClasses']);
		if($runStatus == RUNTESTS_SUCCESS) include_once('views/testResults.php');
		else {
			include_once('views/testFail.php');
			include_once('views/welcome.php');
		}
		break;
	default :
		include_once('views/welcome.php');
}
$output['body'] = ob_get_clean();

include_once('views/wrapper.php');

?>