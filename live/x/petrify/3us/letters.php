<?
	Header("Content-type: text/plain");
	print `egrep -v "[[:digit:]]" /home/sargon/domain/3us.d | grep AVAIL`;
?>
