<?php

define('RUNTESTS_NO_CLASSES', -1);
define('RUNTESTS_SUCCESS', 1);

if(isSet($_POST['testClasses']) && strlen(trim($_POST['testClasses']))) {
	$testClasses = explode("\n", trim($_POST['testClasses']));
	
	if(count($testClasses)) {
		require_once 'util/TestRunner.php';
		require_once 'util/Formatter.php';
		
		// create the test suite
		$suite = new PHPUnit2_Framework_TestSuite();
		
		foreach($testClasses as $testClass) {
			if(file_exists($testClass).'.php') {
				// include the class
				@include_once trim($testClass).'.php';
				// figure out the name of the class
				$path_parts = pathinfo(trim($testClass));
				$suite->addTestSuite($path_parts['basename']);
			}
		}
		
		$runner = new util_TestRunner($suite);
		$formatter = new util_Formatter();
		$runner->addFormatter($formatter);
		// @todo : catch the runner errors, warnings and try and match them with the appropriate suite 
		$result = $runner->run();
		$suiteResults = $formatter->getSuiteResults();
		
		$runStatus = RUNTESTS_SUCCESS;
		
	} else $runStatus = RUNTESTS_NO_CLASSES;	
	
} else $runStatus = RUNTESTS_NO_CLASSES;

?>