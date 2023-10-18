<?
	Header("Content-type: text/plain");
	print `egrep "^testing .[aeiou]" /home/sargon/domain/3us.d | grep AVAIL | grep -v "[0123456789]"`;
?>
