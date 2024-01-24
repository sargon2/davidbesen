<?
	require_once('auth.php');
	require_once('header.html');
	require_once('navbar.php');
	require_once('/home/sargon/public_html/inc/db.inc');
	require_once('mysql_func.inc');
	print '<DIV ALIGN=RIGHT>------------------[ members</DIV><hr size="0">';
	# check for userlevel=1
	$result = mysql_query("select user from auth where user=\"$PHP_AUTH_USER\" and userlevel=1");
	$blah = mysql_fetch_array($result);
	if(!$blah) {print "access denied<br>"; require_once('footer.html');exit(); }
	if($DEL)
	{
		$result = mysql_query("delete from auth where user=\"$DEL\"");
		print mysql_error();
		$result = mysql_query("delete from dvds where name=\"$DEL\"");
		print mysql_error();
		$result = mysql_query("delete from personal where user=\"$DEL\"");
		print mysql_error();
	}
	if($submit)
	{
		$result = mysql_query("select user from auth where user=\"$user\"");
		$blah = mysql_fetch_array($result);
		if(!$blah) {
			$result = mysql_query("insert into auth values(\"$user\", PASSWORD(\"$password\"), 0)");
			print mysql_error();
		} else {
			print "user already exists<br>";
		}
	}
	$result = mysql_query("select user,userlevel from auth");
	while($blah = mysql_fetch_array($result)) {
	    if($blah[1] == 0) {
	    	print "<a href=\"/users.php?DEL=$blah[0]\">Delete</a> $blah[0]<br>";
		}
	}
	print "<form>";
	print "<br>";
	print "<input type=\"hidden\" name=\"submit\" value=\"1\">";
	print "username: <input type=\"text\" name=\"user\"><br>";
	print "password: <input type=\"password\" name=\"password\"><br>";
	print "<input type=\"submit\" value=\"add\"><br>";
	print "</form>";
	
	require_once('footer.html');
?>
