<div id="testResults">
	<?php
	
	if($result->wasSuccessful()) $verboseStatus = 'success';
	else $verboseStatus = 'unsuccessful';
	
	print '<div id="resultsHeader">' .
			'<h2 class="'.$verboseStatus.'">Test results : '.$verboseStatus.'</h2>' .
	        '<h3><span>Tests : '.$result->runCount().'</span> | <span>Failures : '.$result->failureCount().'</span> | <span>Errors : '.$result->errorCount().'</span></h3>' .
	      '</div>'; 
	?>
	<table>
		<thead>
			<tr>
				<th>Test case</th>
				<th>Test name</th>
				<th>Time</th>
				<th>Status</th>
				<th>Message</th>
			</tr>
		</thead>
		<tbody>
	<?php
		foreach($suiteResults['suites'] as $suite) {
			$suiteStatus = 'success';
			if($suite['failures'] > 0) $suiteStatus = 'failure';
			else if($suite['errors'] > 0) $suiteStatus = 'error';
			 
			print '<tr>' .
					'<th colspan="5" class="suiteDetails '.$suiteStatus.'"><span class="suiteName">'.$suite['suite']->getName().'</span> <span>Tests : '.count($suite['tests']).' | Failures : '.$suite['failures'].' | Errors : '.$suite['errors'].'</span></th>'.
				  '</tr>';
			foreach($suite['tests'] as $test) {
				switch($test['status']) {
					case(TEST_FAILURE) :
						$verboseStatus = 'failure';
						break;
					case(TEST_ERROR) :
						$verboseStatus = 'error';
						break;
					case(TEST_SUCCESS) :
						$verboseStatus = 'success';
						break;
					case(TEST_INCOMPLETE) :
						$verboseStatus = 'incomplete';
						break;
				}
				
				print '<tr class="'.$verboseStatus.'">' . "\n".
						'<td>'.$suite['suite']->getName().'</td>' . "\n".
						'<td>'.$test['name'].'</td>' . "\n".
						'<td class="time">'.sprintf("%01.4f", $test['timeElapsed']).'</td>' . "\n".
						'<td class="status"><span>'.$verboseStatus.'</span></td>'. "\n";
				if($test['status'] != TEST_SUCCESS) print '<td><a href="#'.$test['uid'].'">'.$test['message'].'</a></td>'."\n";
				else print '<td>'.$test['message'].'</td>'. "\n";
				print '</tr>' . "\n";
			}
		}
	?>
		</tbody>
	</table>
	
	
	<?php
		ob_start();
	
		foreach($suiteResults['suites'] as $suite) {
			
			if($suite['failures'] > 0 || $suite['errors'] > 0) {
				
				print '<table class="testDetail">' .
				        '<tr>' .
				          '<th colspan="2" class="suiteDetails">'.$suite['suite']->getName().'</th>' .
				        '</tr>';
					
				foreach($suite['tests'] as $test) {
					if($test['status'] != TEST_SUCCESS) {
						?>
						<tr>
							<th class="lbl"><a name="<?php print $test['uid']; ?>"></a>Test name</th>
							<td><?php print $test['name']; ?></td>
						</tr>								
						<tr>
							<th class="lbl">Line</th>
							<td><?php print $test['exception']['line']; ?></td>
						</tr>
						<tr>
							<th class="lbl">Function</th>
							<td><?php print $test['exception']['function']; ?></td>
						</tr>
						<tr>
							<th class="lbl">Message</th>
							<td><?php print $test['message']; ?></td>
						</tr>
						<?php 
							$args = $test['exception']['args'];
							if(count($args) >= 2) {
						?>
								<tr class="last">
									<th class="lbl">Exception</th>
									<td><?php print $test['exception']['args'][1]; ?></td>
								</tr>							
						<?php				
							} else {
								print '<tr class="last"><td colspan="2"></tr>'; 
							}
					}
				}
				
				print '</table>';
			}
		}
		
		$extendedDetail = ob_get_clean();
		
		if(strlen($extendedDetail)) print '<div id="extendedDetail"><h2>Extended Detail</h2>'.$extendedDetail.'</div>';
	?>		
	
</div>