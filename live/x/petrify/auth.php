<?
	require_once('/home/sargon/public_html/inc/db.inc');
	require_once('mysql_func.inc');
	if(!isset($PHP_AUTH_USER)) {
		Header("WWW-Authenticate: Basic realm=\"petrify.net\"");
		Header("HTTP/1.0 401 Unauthorized");
		echo "<font size=+2>HTTP/1.0 401 Unauthorized</font>\n";
		exit;
	}
	$result = mysql_query("select user from auth where password=PASSWORD('$PHP_AUTH_PW') and user=\"$PHP_AUTH_USER\"");
	$blah = mysql_fetch_array($result);
	if(!$blah) {
		Header("WWW-Authenticate: Basic realm=\"petrify.net\"");
		Header("HTTP/1.0 401 Unauthorized");
		//echo "<font size=+2>HTTP/1.0 401 Unauthorized</font>\n";
		require_once('header.html');
		$loc = unauthorized;
		require_once('navbar.php');
		print '<DIV ALIGN=RIGHT>-------------[ unauthorized</DIV><hr size="0">';
		print "HTTP/1.0 401 Unauthorized<br><br>";
		print "If you would like an account (all it does is let you have a dvd list), please contact me.";
		require_once('footer.html');
		exit;
	}
?>
