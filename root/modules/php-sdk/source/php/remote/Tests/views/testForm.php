<form action="index.php" method="post" id="testForm">
	<div>
		<input type="hidden" name="action" value="runTests" />
		<label for="testClasses">Test class(es):</label>
		<textarea name="testClasses" id="testClasses" rows="5" cols="80"><?php if(isSet($_POST['testClasses'])) print $_POST['testClasses']; ?></textarea>
	</div>
	<?php include('testHistory.php'); ?>
	<div class="actionRow">
		<input type="submit" name="formSubmit" value="Run tests" /> 
	</div>
</form>