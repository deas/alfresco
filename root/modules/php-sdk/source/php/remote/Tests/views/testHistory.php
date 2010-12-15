<?php
// pickup the history cache
$history = $historyUtil->getHistory();

if($historyUtil->isCacheAvailable()) {
?>

	<div id="testHistory" class="closed">
		<h3 id="testHistoryToggle"><a href="#">Test history</a></h3>
	    <div id="historyWrap" class="closed">
	    	<ul id="history">
				<?php
				for($i = 0; $i < count($history); $i++) {
					if($i == 0) $class = 'first';
					else if($i == count($history) -1) $class = 'last';
					print '<li class="'.$class.'">'.$history[$i].'</li>'."\n";
				}
				?>
			</ul>
		</div>
	</div>

<?php
} else {
	?>
		<span style="float: left; width: auto;">History not available - ensure <code>/assets/</code> is writable</span>
	<?
}
?>