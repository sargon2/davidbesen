<?
	require_once('/home/ramcon/public_html/inc/db.inc');
	require_once('mysql_func.inc');
	require_once('auth.php');
	print "<form action=\"delplayer.php\" method=\"post\">";

	$result = mysql_query("select id, game from TOURNEY_tourneys");
	while($blah = mysql_fetch_array($result))
	{
		print "<input type=\"radio\" name=\"game\" value=\"$blah[0]\">$blah[1]<br>";
	}
	print "<hr>";
	$result = mysql_query("select id, user from TOURNEY_users");
	while($blah = mysql_fetch_array($result))
	{
		print "<input type=\"radio\" name=\"name\" value=\"$blah[0]\">$blah[1]<br>";
	}
	print "<input type=\"submit\" value=\"submit\">";

	print "</form>";
?>
