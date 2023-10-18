<?
	require_once('auth.php');
	require_once('/home/ramcon/public_html/inc/db.inc');
	require_once('mysql_func.inc');
	if($DEL)
	{
		$result = mysql_query("delete from TOURNEY_auth where user=\"$DEL\"");
		print mysql_error();
	}
	if($submit)
	{
		$result = mysql_query("select user from TOURNEY_auth where user=\"$user\"");
		$blah = mysql_fetch_array($result);
		if(!$blah) {
			$result = mysql_query("insert into TOURNEY_auth(user, password) values(\"$user\", PASSWORD(\"$password\"))");
			print mysql_error();
		} else {
			print "user already exists<br>";
		}
	}
	$result = mysql_query("select user from TOURNEY_auth");
	while($blah = mysql_fetch_array($result)) {
	    	print "<a href=\"users.php?DEL=$blah[0]\">Delete</a> $blah[0]<br>";
	}
	print "<form method=\"POST\">";
	print "<br>";
	print "<input type=\"hidden\" name=\"submit\" value=\"1\">";
	print "username: <input type=\"text\" name=\"user\"><br>";
	print "password: <input type=\"password\" name=\"password\"><br>";
	print "<input type=\"submit\" value=\"add\"><br>";
	print "</form>";
	
?>
