<?
	require_once('auth.php');
	require_once('header.html');
	require_once('navbar.php');
	require_once('/home/sargon/public_html/inc/db.inc');
	require_once('mysql_func.inc');
	print '<DIV ALIGN=RIGHT>------------------[ members</DIV><hr size="0">';
	if($DEL)
	{
		$result = mysql_query("delete from dvds where name=\"$PHP_AUTH_USER\" and dvd=\"".urldecode($DEL)."\"");
		print mysql_error();
	}
	if($submit)
	{
		$result = mysql_query("select dvd from dvds where name=\"$PHP_AUTH_USER\" and dvd=\"$dvd\"");
		$blah = mysql_fetch_array($result);
		if(!$blah) {
			$result = mysql_query("insert into dvds values(\"$PHP_AUTH_USER\", \"$dvd\")");
			print mysql_error();
		} else {
			print "dvd already exists<br>";
		}
	}
	$result = mysql_query("select dvd from dvds where name=\"$PHP_AUTH_USER\"");
	while($blah = mysql_fetch_array($result)) {
 		print "<a href=\"/chdvds.php?DEL=".urlencode($blah[0])."\">Delete</a> $blah[0]<br>";
	}
	print "<form>";
	print "<br>";
	print "<input type=\"hidden\" name=\"submit\" value=\"1\">";
	print "dvd: <input type=\"text\" name=\"dvd\"><br>";
	print "<input type=\"submit\" value=\"add\"><br>";
	print "</form>";
	
	require_once('footer.html');
?>
