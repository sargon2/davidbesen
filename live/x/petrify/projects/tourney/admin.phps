<?
	require_once('auth.php');
	require_once('/home/ramcon/public_html/inc/db.inc');
	require_once('mysql_func.inc');

	print "<a href=\"chpass.php\">change password</a><br>";
	print "<a href=\"users.php\">manage users</a><br><br>";
	print "<a href=\"add.php\">add player</a><br>";
	print "<a href=\"del.php\">delete player</a><br><br>";
	print "<a href=\"tourneys.php\">manage tournaments</a><br><br>";

	$result = mysql_query("select * from TOURNEY_tourneys");
	while($blah = mysql_fetch_array($result)) {
		print "<a href=\"move.php?game=$blah[0]\">advance</a> <a href=\"back.php?game=$blah[0]\">backup</a> <a href=\"finalize.php?game=$blah[0]\">finalize</a> $blah[1]<BR>";
	}
?>
