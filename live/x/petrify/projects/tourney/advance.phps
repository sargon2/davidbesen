<?
	require_once('/home/ramcon/public_html/inc/db.inc');
	require_once('mysql_func.inc');
	require_once('auth.php');
	# authenticate
	mysql_query("update TOURNEY_users set level=TOURNEY_users.level+1 where id=$id and game=$game");
	print mysql_error();
foreach ($HTTP_GET_VARS as $key => $value) {
	echo "<input type='hidden' name='$key' value='$value'>";
	$querystring .= "&$key=$value";
	if($key!="draw_x"&&$key!="draw_y") $reloadstring .= "&$key=$value";
}
foreach ($HTTP_POST_VARS as $key => $value) {
	echo "<input type='hidden' name='$key' value='$value'>";
	$querystring .= "&$key=$value";
	if($key!="draw_x"&&$key!="draw_y") $reloadstring .= "&$key=$value";
}
	print "Location: "move.php?$reloadstring";
?>
