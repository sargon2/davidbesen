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
		$result = mysql_query("delete from news where title=\"".urldecode($DEL)."\"");
		print mysql_error();
	}
	if($submit)
	{
		$result = mysql_query("select title from news where title=\"$title\"");
		$blah = mysql_fetch_array($result);
		if(!$blah) {
			$body = str_replace("\n", "<br>", $body);
			$result = mysql_query("insert into news values(\"".time()."\", \"$title\", \"$body\")");
			print mysql_error();
		} else {
			print "news item already exists<br>";
		}
	}
	$result = mysql_query("select title from news");
	while($blah = mysql_fetch_array($result)) {
 		print "<a href=\"/chnews.php?DEL=".urlencode($blah[0])."\">Delete</a> $blah[0]<br>";
	}
	print "<form>";
	print "<br>";
	print "<input type=\"hidden\" name=\"submit\" value=\"1\">";
	print "title: <input type=\"text\" name=\"title\" size=\"50\"><br>";
	print "body: <br><textarea name=\"body\" cols=\"80\" rows=\"25\"></textarea><br>";
	print "<input type=\"submit\" value=\"submit\"><br>";
	print "</form>";
	
	require_once('footer.html');
?>
