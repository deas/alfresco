<h2>Welcome to PHPUnit2 : HTML Runner</h2>
<h3>How to use the HTML Runner</h3>
<p>
	The HTML Runner can run multiple TestCases and/or TestSuites within a run and provide results for all TestCases performed.
</p>
<p>
	TestCases and TestSuites are defined by providing the path and name to the file, with each
	test on a new line.
</p>
<h3>Example usage:</h3>

<pre title="example test entry">
util/HistoryTest
fake/fake_pathTest
test/PHPUnit2/Tests/OneTestCase
</pre>

<p>
	In this example the <code>util/HistoryTest.php</code> and <code>fake/fake_pathTest.php</code> TestCases
	which are part of the HTML Runner will be run along with the PHPUnit2 test <code>test/PHPUnit2/Tests/OneTestCase.php</code>
	which is installed in the <code>PEAR/test</code> directory with the standard PHPUnit2 installation.
</p>

<h3>History:</h3>
<p>
	Every time a set of tests is performed that set is added to the history, enabling quick access for recently performed tests.
</p>
<p>
	To enable the history functionality the directory <code>assets</code> (or file <code>assets/testHistory.txt</code>) must be writable (<code>CHMOD 777</code>).
</p>

<h3>More Information:</h3>
<p>
	For more information, bug reports and documentation visit <a href="http://www.defusion.org.uk/code/PHPUnit2-HTMLRunner/">http://www.defusion.org.uk/code/PHPUnit2-HTMLRunner/</a>
</p>