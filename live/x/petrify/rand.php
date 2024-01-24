<?
	header("Content-type: text/plain");
	$asdf = 1;
	for($i=0;$i<100;$i++) {
		$asdf = lcg(100,11,37, $asdf);
		print $asdf . "\n";
	}
	function lcg($m, $a, $b, $y) {
		return bcmod(bcadd(bcmul($a, $y), $b), $m);
	}
?>
