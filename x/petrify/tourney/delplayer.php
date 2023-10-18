<?
	require_once('/home/ramcon/public_html/inc/db.inc');
	require_once('mysql_func.inc');
	require_once('auth.php');
	if(!$name) {print "please specify name and game<br>";return;}
	if(!$game) {print "please specify name and game<br>";return;}
	mysql_query("delete from TOURNEY_users where game=$game and id=$name");
	print mysql_error();
	print "If you did not see any errors, the delete was completed successfully.";
?>
