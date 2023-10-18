
Sorry, this was hastily thrown together.... <br><br>

<?
	require_once('/home/ramcon/public_html/inc/db.inc');
	require_once('mysql_func.inc');
	$result = mysql_query("select * from TOURNEY_tourneys");
	while($blah = mysql_fetch_array($result)) {
		print "<a href=\"draw.shtml?game=$blah[0]\">draw</a> <a href=\"list.php?game=$blah[0]\">list</a> $blah[1]<BR>";
	}
	print "<br><a href=\"list.php\">list all registered</a><br>";
	print "<a href=\"admin.php\">admin</a><br><br>";
	print "<a href=\"signup.php\">Sign Up!!</a> (uses your ramcon login)";
?>
