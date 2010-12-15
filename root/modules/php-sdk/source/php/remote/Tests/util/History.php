<?php

/**
 * Utility for dealing with a simple history cache of strings.
 * The history is stored with the most recent addition firstmost.
 * 
 * Changelog:
 *  0.5 First created [David Spurr]
 *
 * @version 0.5 2006-01-20
 * @author David Spurr
 */
class util_History {
	/**
	 * The path to the cache file
	 * @var string
	 */
	private $cacheFilePath;
	/**
	 * Whether the cache file is available for writing
	 * @var boolean
	 */
	private $cacheAccessible;
	/**
	 * The current history
	 * @var array
	 */
	private $history;
	/**
	 * The max length of the history
	 * @var int
	 */
	private $historySize;
	
	/**
	 * History constructor
	 * 
	 * @access public
	 * @param string Absolute cache file path
	 * @param int Max length of history - optional - default 10
	 * @return void
	 */
	public function __construct($cacheFilePath, $historySize = 10) {
		$this->historySize = $historySize;
		$this->cacheFilePath = $cacheFilePath;
		// try and create the file if it doesn't exist
		if(is_writable(dirname($this->cacheFilePath)) && !file_exists($this->cacheFilePath)) {
			$fp = fopen($this->cacheFilePath, 'w+');
			fclose($fp);
		}
		$this->cacheAccessible = (file_exists($this->cacheFilePath) && is_readable($this->cacheFilePath) && is_writable($this->cacheFilePath));
		$this->pickupCache();
	}
	
	/**
	 * Returns whether the cache is available or not (either for reading or writing)
	 * 
	 * @access public
	 * @return boolean
	 */
	public function isCacheAvailable() {
		return $this->cacheAccessible;
	}
	
	/**
	 * Returns the current history
	 * 
	 * @access public
	 * @return array History in descending order (most recently performed tests first)
	 */
	public function getHistory() {
		return $this->history;
	}
	
	/**
	 * Saves the provided string to the history cache
	 * 
	 * @access public
	 * @param string 
	 * @return void
	 */
	public function addToCache($str) {
		$str = trim($str);
		if($this->cacheAccessible && strlen($str)) {
			// if there is no history then just cache it
			if(!count($this->history)) {
				array_push($this->history, $str);
			} else {
				// find out if this str is already part of the history
				$foundIndex = -1;
				for($i = 0; $i < count($this->history); $i++) {
					if($this->history[$i] == $str) {
						$foundIndex = $i;
						break;
					}
				}
				if($foundIndex > -1 &&count($this->history) > 0) {
					// if it already exists in the cache bring it to the front of the history
					array_splice($this->history, $foundIndex, 1);
					array_unshift($this->history, $str);
				} else {
					// otherwise just prepend it to the history
					array_unshift($this->history, $str);
				}
				
				// ensure that the history is the right length
				while(count($this->history) > $this->historySize) {
					array_pop($this->history);
				}
			}
			$this->saveCache();
		}
	}
	
	/**
	 * Picks up the history cache from the cache file
	 * 
	 * @access private
	 * @return void
	 */
	private function pickupCache() {
		$history = array();
		if($this->cacheAccessible) {
			$fc = file_get_contents($this->cacheFilePath);
			if(strlen(trim($fc))) {
				try {
					// get the cache
					$history = unserialize($fc);
				} catch(Exception $e) {}
			}
		}
		$this->history = $history;
	}
	
	/**
	 * Saves the history cache to the cache file
	 * 
	 * @access private
	 * @return void
	 */
	private function saveCache() {
		if($this->cacheAccessible) {
			file_put_contents($this->cacheFilePath, serialize($this->history));
		}
	}
}

?>