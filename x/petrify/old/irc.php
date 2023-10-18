<?
	require_once('header.php');
	require_once('navbar.php');
?>
<DIV ALIGN=RIGHT>----------------------[ irc</DIV><hr size="0">
cgi-irc <a href="/cgi-bin/irc.cgi">here</a><br>
Currently happening in #linux on the spaceballs network:<br>--------------------------------------------------------<br><br>
<?
	$fp = popen("tail -45 /home/talon/eggdrop/mel/logs/#linux.log", "r");
	while(!feof($fp))
	{
		$buffer = fgets($fp, 4096);
		$buffer = str_replace('&', '&amp;', $buffer);
		$buffer = str_replace('<', '&lt;', $buffer);
		$buffer = str_replace('>', '&gt;', $buffer);
		print $buffer; print "<br>";
	}
	pclose($fp);
?>
<? require_once('footer.html'); ?>
