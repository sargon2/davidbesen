<?
	$asdf2 = parse_url($HTTP_REFERER);
	$asdf = $asdf2[query];
	parse_str($asdf, $jkl);
	print $jkl[q];
	
?>
