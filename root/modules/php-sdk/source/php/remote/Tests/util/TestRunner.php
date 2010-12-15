<?php

require_once('PHPUnit2/Framework/TestListener.php');
require_once('PHPUnit2/Framework/TestResult.php');
require_once('PHPUnit2/Framework/TestSuite.php');

class util_TestRunner {

    function __construct(PHPUnit2_Framework_TestSuite $suite) {
        $this->suite = $suite;
    }

    function addFormatter(PHPUnit2_Framework_TestListener $formatter) {
        $this->formatter = $formatter;
    }

    function run() {
        $result = new PHPUnit2_Framework_TestResult();
        $result->addListener( $this->formatter );
        $this->suite->run($result);
        return $result;
    }
}

?>