<?php

require_once 'PHPUnit2/Framework/TestCase.php';
require_once 'History.php';

/**
 * Tests for the History Class
 *
 * @category   Testing
 * @package    history
 * @author     David Spurr
 * @version    0.1
 */
class HistoryTest extends PHPUnit2_Framework_TestCase {
	
	protected function setUp() {
		// we'll create a couple of history files for testing with
		// blank one
		$this->blankFilePath = dirname(__FILE__).'/testBlankHistory.txt';
		$fp = fopen($this->blankFilePath, 'w+');
		fclose($fp);
		
		// one with a couple of cached items
		$this->smallCachePath = dirname(__FILE__).'/testSmallHistory.txt';
		$fp = fopen($this->smallCachePath, 'w+');
		$this->smallHistory = array(
			'somefile1',
			'somePath1/somefile2',
			'somePath2/somefile3');
		fwrite($fp, serialize($this->smallHistory));
		fclose($fp);
		
		// one with 10 (default max length) cached items
		$this->fullCachePath = dirname(__FILE__).'/testFullHistory.txt';
		$fp = fopen($this->fullCachePath, 'w+');
		$this->fullHistory = array(
				'somefile0',
				'somefile1',
				'somefile2',
				'somefile3',
				'somefile4',
				'somefile5',
				'somefile6',
				'somefile7',
				'somefile8',
				'somefile9');
		fwrite($fp, serialize($this->fullHistory));
		fclose($fp);
		
		// create a history object for each of the test cache files
		$this->historyBlank = new util_History($this->blankFilePath); 
		$this->historySmall = new util_History($this->smallCachePath); 
		$this->historyFull = new util_History($this->fullCachePath); 
	}
	
	protected function tearDown() {
		// delete all the test history files
		unlink($this->blankFilePath);
		unlink($this->smallCachePath);
		unlink($this->fullCachePath);
	}
	
	// tests the variables setup by the constructor    
    public function testConstructor() {
	   	self::assertTrue($this->historyBlank->isCacheAvailable(), 'Cache should be available');
    	self::assertEquals(0, count($this->historyBlank->getHistory()));
    	self::assertTrue($this->historySmall->isCacheAvailable(), 'Cache should be available');
    	self::assertEquals(3, count($this->historySmall->getHistory()));
    	self::assertTrue($this->historyFull->isCacheAvailable(), 'Cache should be available');
    	self::assertEquals(10, count($this->historyFull->getHistory()));
    	
    	// test the values of the picked up cache
    	self::assertEquals(array(), $this->historyBlank->getHistory());
    	self::assertEquals($this->smallHistory, $this->historySmall->getHistory());
    	self::assertEquals($this->fullHistory, $this->historyFull->getHistory());
    }
    
    // tests the ability to ensure that duplicates are not added to the cache
    public function testAddDuplicatesToCache() {
    	$testCache = array('testFile1');
    	for($i = 0; $i <= 20; $i++) {
    		$this->historyBlank->addToCache('testFile1');
    		self::assertEquals($testCache, $this->historyBlank->getHistory(), 'Cache should be testFile1');
    		self::assertEquals(1, count($this->historyBlank->getHistory()));
    	}
    }
    
    // tests the ability to add new items to cache
    public function testAddNewItemsToCache() {
    	// we'll try the blank one first
    	$testCache = array();
    	array_unshift($testCache, 'testFile1');
    	$this->historyBlank->addToCache('testFile1');
    	self::assertEquals($testCache, $this->historyBlank->getHistory(), 'Cache should be testFile1');
    	
    	array_unshift($testCache, 'testFile2');
    	$this->historyBlank->addToCache('testFile2');
    	self::assertEquals($testCache, $this->historyBlank->getHistory(), 'Cache should be testFile2,testFile1');
    	
    	array_unshift($testCache, 'testFile3');
    	$this->historyBlank->addToCache('testFile3');
    	self::assertEquals($testCache, $this->historyBlank->getHistory(), 'Cache should be testFile3,testFile2,testFile1');
    	
    	array_unshift($testCache, 'testFile4');
    	$this->historyBlank->addToCache('testFile4');
    	self::assertEquals($testCache, $this->historyBlank->getHistory(), 'Cache should be testFile4,testFile3,testFile2,testFile1');
    	
    	array_unshift($testCache, 'testFile5');
    	$this->historyBlank->addToCache('testFile5');
    	self::assertEquals($testCache, $this->historyBlank->getHistory(), 'Cache should be testFile5,testFile4,testFile3,testFile2,testFile1');
    	
    	array_unshift($testCache, 'testFile6');
    	$this->historyBlank->addToCache('testFile6');
    	self::assertEquals($testCache, $this->historyBlank->getHistory(), 'Cache should be testFile6,testFile5,testFile4,testFile3,testFile2,testFile1');
    	
    	array_unshift($testCache, 'testFile7');
    	$this->historyBlank->addToCache('testFile7');
    	self::assertEquals($testCache, $this->historyBlank->getHistory(), 'Cache should be testFile7,testFile6,testFile5,testFile4,testFile3,testFile2,testFile1');
    	
    	array_unshift($testCache, 'testFile8');
    	$this->historyBlank->addToCache('testFile8');
    	self::assertEquals($testCache, $this->historyBlank->getHistory(), 'Cache should be testFile8,testFile7,testFile6,testFile5,testFile4,testFile3,testFile2,testFile1');
    	
    	array_unshift($testCache, 'testFile9');
    	$this->historyBlank->addToCache('testFile9');
    	self::assertEquals($testCache, $this->historyBlank->getHistory(), 'Cache should be testFile9,testFile8,testFile7,testFile6,testFile5,testFile4,testFile3,testFile2,testFile1');
    	
    	array_unshift($testCache, 'testFile10');
    	$this->historyBlank->addToCache('testFile10');
    	self::assertEquals($testCache, $this->historyBlank->getHistory(), 'Cache should be testFile10,testFile9,testFile8,testFile7,testFile6,testFile5,testFile4,testFile3,testFile2,testFile1');
    	
    	array_unshift($testCache, 'testFile11');
    	array_pop($testCache);
    	$this->historyBlank->addToCache('testFile11');
    	self::assertEquals(10, count($this->historyBlank->getHistory()));
    	self::assertEquals($testCache, $this->historyBlank->getHistory(), 'Cache should be testFile11,testFile10,testFile9,testFile8,testFile7,testFile6,testFile5,testFile4,testFile3,testFile2');
    	
    	array_unshift($testCache, 'testFile12');
    	array_pop($testCache);
    	$this->historyBlank->addToCache('testFile12');
    	self::assertEquals(10, count($this->historyBlank->getHistory()));
    	self::assertEquals($testCache, $this->historyBlank->getHistory(), 'Cache should be testFile12,testFile11,testFile10,testFile9,testFile8,testFile7,testFile6,testFile5,testFile4,testFile3');
    	
    	array_unshift($testCache, 'testFile13');
    	array_pop($testCache);
    	$this->historyBlank->addToCache('testFile13');
    	self::assertEquals(10, count($this->historyBlank->getHistory()));
    	self::assertEquals($testCache, $this->historyBlank->getHistory(), 'Cache should be testFile13,testFile12,testFile11,testFile10,testFile9,testFile8,testFile7,testFile6,testFile5,testFile4');
    	
    	array_unshift($testCache, 'testFile14');
    	array_pop($testCache);
    	$this->historyBlank->addToCache('testFile14');
    	self::assertEquals(10, count($this->historyBlank->getHistory()));
    	self::assertEquals($testCache, $this->historyBlank->getHistory(), 'Cache should be testFile14,testFile13,testFile12,testFile11,testFile10,testFile9,testFile8,testFile7,testFile6,testFile5');
    	
    	array_unshift($testCache, 'testFile15');
    	array_pop($testCache);
    	$this->historyBlank->addToCache('testFile15');
    	self::assertEquals(10, count($this->historyBlank->getHistory()));
    	self::assertEquals($testCache, $this->historyBlank->getHistory(), 'Cache should be testFile15,testFile14,testFile13,testFile12,testFile11,testFile10,testFile9,testFile8,testFile7,testFile6');
    }
    
    // tests the functionality when adding items which are already present in the cache
    // should bring them to the front of the cache
    public function testAddExistingItemsToCache() {
    	/*
    		the starting cache
	    	$this->fullHistory = array(
				'somefile0',
				'somefile1',
				'somefile2',
				'somefile3',
				'somefile4',
				'somefile5',
				'somefile6',
				'somefile7',
				'somefile8',
				'somefile9');
		*/
		array_splice($this->fullHistory, 4, 1);
		array_unshift($this->fullHistory, 'somefile4');
		$this->historyFull->addToCache('somefile4');
		self::assertEquals($this->fullHistory, $this->historyFull->getHistory(), 'Cache should be somefile4, somefile0, somefile1, somefile2, somefile3, somefile5, somefile6, somefile7, somefile8, somefile9');
		
		array_splice($this->fullHistory, 9, 1);
		array_unshift($this->fullHistory, 'somefile9');
		$this->historyFull->addToCache('somefile9');
		self::assertEquals($this->fullHistory, $this->historyFull->getHistory(), 'Cache should be somefile9, somefile4, somefile0, somefile1, somefile2, somefile3, somefile5, somefile6, somefile7, somefile8');
		
		// now try adding something new
		array_unshift($this->fullHistory, 'somefile10');
		array_pop($this->fullHistory);
		$this->historyFull->addToCache('somefile10');
		self::assertEquals($this->fullHistory, $this->historyFull->getHistory(), 'Cache should be somefile10, somefile9, somefile4, somefile0, somefile1, somefile2, somefile3, somefile5, somefile6, somefile7');
		
		// try adding something similar to one that already exists
		array_unshift($this->fullHistory, "somefile10\nsomefile11");
		array_pop($this->fullHistory);
		$this->historyFull->addToCache("somefile10\nsomefile11");
		self::assertEquals($this->fullHistory, $this->historyFull->getHistory(), 'Cache should be somefile10\nsomefile11, somefile10, somefile9, somefile4, somefile0, somefile1, somefile2, somefile3, somefile5, somefile6');
		
    }
    
    public function testSaveCache() {
    	// add something to the cache, so that it gets saved
    	array_unshift($this->fullHistory, 'somefile10');
		array_pop($this->fullHistory);
    	$this->historyFull->addToCache('somefile10');
    	
    	// read in the file & compare
    	$stored = @file_get_contents($this->fullCachePath);
    	self::assertEquals(serialize($this->fullHistory), $stored);
    }
}

?>