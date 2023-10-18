<?
	require_once('inc/ramcon-db.inc');
	require_once('mysql_func.inc');
//	require_once('auth.php');
	if(!$name) {print "please specify name and game<br>";return;}
	if(!$game) {print "please specify name and game<br>";return;}
	mysql_query("insert into TOURNEY_users (user, level, position, game) values (\"$name\", 0, 0, $game)");
	print mysql_error();
	print "if you didn't see any errors, the add was completed successfully.";
?>
