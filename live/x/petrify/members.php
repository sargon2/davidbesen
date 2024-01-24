<?
	require_once('auth.php');
	require_once('header.html');
	$loc = "members";
	require_once('navbar.php');
	require_once('/home/sargon/public_html/inc/db.inc');
	require_once('mysql_func.inc');
	print '<DIV ALIGN=RIGHT>------------------[ members</DIV><hr size="0">';

	$result = mysql_query("select userlevel from auth where user='$PHP_AUTH_USER'");
	$blah = mysql_fetch_array($result);
	if($blah[0] == 1) {
		print "<a href=\"/users.php\">add/del user</a><br>";
		print "<a href=\"/chnews.php\">edit news</a><br>";
	}
	print "<a href=\"/chpass.php\">change password</a><br>";
	print "<a href=\"/chdvds.php\">edit dvds</a><br>";
	// print "<a href=\"/personal.php\">edit personal news</a><br>";
	print "<br><a href=\"/webalizer\">stats</a><br>";
	
	require_once('footer.html');
?>
