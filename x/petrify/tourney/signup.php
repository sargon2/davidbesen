<?
	require_once('inc/ramcon-db.inc');
	require_once('mysql_func.inc');
	#require_once('/home/ramcon/public_html/inc/member_auth.inc');
	print "<form action=\"addplayer.php\" method=\"post\">";

	print "name: $PHP_AUTH_USER<br>";
	print "<input type=\"hidden\" name=\"name\" value=\"$PHP_AUTH_USER\">";
	$result = mysql_query("select id, game from TOURNEY_tourneys where free=1");
	$sel = "checked";
	while($blah = mysql_fetch_array($result))
	{
		print "<input type=\"radio\" name=\"game\" value=\"$blah[0]\" $sel>$blah[1]<br>";
		$sel = "";
	}
	print "<input type=\"submit\" value=\"submit\">";

	print "</form>";
?>
