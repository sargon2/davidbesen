<?
	require_once('auth.php');
	require_once('/home/ramcon/public_html/inc/db.inc');
	require_once('mysql_func.inc');

	if($newpass && $newpass2)
	{
		if($newpass != $newpass2)
			print "passwords do not match, try again";
		else {
			$result = mysql_query("update TOURNEY_auth set password=PASSWORD(\"$newpass\") where user=\"$PHP_AUTH_USER\"");
			print mysql_error();
			print "password successfully changed<br>";
			exit();
		}
	}
	print "<form method=\"POST\">";
	print "new password: <input type=\"password\" name=\"newpass\"><BR>";
	print "enter again:&nbsp; <input type=\"password\" name=\"newpass2\"><BR>";
	print "<input type=\"submit\" value=\"submit\">";
	print "</form>";
	
?>
