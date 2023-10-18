<?
	require_once('auth.php');
	require_once('header.html');
	require_once('navbar.php');
	require_once('/home/sargon/public_html/inc/db.inc');
	require_once('mysql_func.inc');
	print '<DIV ALIGN=RIGHT>------------------[ members</DIV><hr size="0">';

	if($newpass && $newpass2)
	{
		if($newpass != $newpass2)
			print "passwords do not match, try again";
		else {
			$result = mysql_query("update auth set password=PASSWORD(\"$newpass\") where user=\"$PHP_AUTH_USER\"");
			print mysql_error();
			print "password successfully changed<br>";
			print "<a href=\"/\">go home</a>";
			require_once('footer.html');
			exit();
		}
	}
	print "<form>";
	print "new password: <input type=\"password\" name=\"newpass\"><BR>";
	print "enter again:&nbsp; <input type=\"password\" name=\"newpass2\"><BR>";
	print "<input type=\"submit\" value=\"submit\">";
	print "</form>";
	
	require_once('footer.html');
?>
