<?
	require_once('/home/ramcon/public_html/inc/db.inc');
	require_once('mysql_func.inc');
//	require_once('auth.php');
	print "<form action=\"addplayer.php\" method=\"post\">";

	print "name: <input type=\"text\" name=\"name\"><br>";
	$result = mysql_query("select id, game from TOURNEY_tourneys");
	$sel = "checked";
	while($blah = mysql_fetch_array($result))
	{
		print "<input type=\"radio\" name=\"game\" value=\"$blah[0]\" $sel>$blah[1]<br>";
		$sel = "";
	}
	print "<input type=\"submit\" value=\"submit\">";

	print "</form>";
?>
