<?
	require_once('auth.php');
	require_once('/home/ramcon/public_html/inc/db.inc');
	require_once('mysql_func.inc');
	if($DEL)
	{
		$result = mysql_query("delete from TOURNEY_tourneys where id=\"$DEL\"");
		print mysql_error();
		$result = mysql_query("delete from TOURNEY_users where game=\"$DEL\"");
		print mysql_error();
	}
	if($submit)
	{
		$result = mysql_query("select game from TOURNEY_tourneys where game=\"$game\"");
		$blah = mysql_fetch_array($result);
		if(!$blah) {
			$result = mysql_query("insert into TOURNEY_tourneys(game, free) values(\"$game\", $free)");
			print mysql_error();
		} else {
			print "tournament already exists<br>";
		}
	}
	$result = mysql_query("select id, game from TOURNEY_tourneys");
	while($blah = mysql_fetch_array($result)) {
	    	print "<a href=\"tourneys.php?DEL=$blah[0]\">Delete</a> $blah[1]<br>";
	}
	print "<form method=\"POST\">";
	print "<br>";
	print "<input type=\"hidden\" name=\"submit\" value=\"1\">";
	print "Tourney: <input type=\"text\" name=\"game\"><br>";
	print "<input type=\"radio\" name=\"free\" value=\"1\"> Member page signup (non-team games)<br>";
	print "<input type=\"radio\" name=\"free\" value=\"0\"> No member page signup (team games)<br>";
	print "<input type=\"submit\" value=\"add\"><br>";
	print "</form>";
	
?>
