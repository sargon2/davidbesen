<?
	require_once('header.php');
	require_once('navbar.php');
	require_once('/home/sargon/public_html/inc/db.inc');
	require_once('mysql_func.inc');
	print '<DIV ALIGN=RIGHT>---------------------[ dvds</DIV><hr size="0">';
	if($name)
	{
		$result = mysql_query("select count(dvd) from dvds where name=\"$name\"");
		$blah = mysql_fetch_array($result);
		print "$name's dvd collection ($blah[0] total): <br>";
		print "----------------------<br>";
		#$result = mysql_query("select dvd from dvds where name=\"$name\" order by dvd");
		$result = mysql_query("select dvd, if (substring(dvd, 1, 4) like 'The ', concat(substring(dvd, 5),', The'), dvd) as orderdvd from dvds where name=\"$name\" order by orderdvd");

		while($blah = mysql_fetch_array($result))
			print "$blah[0]<br>";
	}
	else
	{
		$result = mysql_query("select distinct name from dvds");
		while($blah = mysql_fetch_array($result))
			print "<a href=\"dvds.php?name=$blah[0]\">$blah[0]</a><br>";
	}
	
	require_once('footer.html');
?>
