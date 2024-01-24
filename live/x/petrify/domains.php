<?
	Header("Content-type: text/plain");
	print `wc -l ../domain/words`;
	print `fgrep "AVAILABLE" ../domain/words`;

?>
