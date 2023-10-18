<?
	require_once('/home/ramcon/public_html/inc/db.inc');
	require_once('mysql_func.inc');
	if(!isset($PHP_AUTH_USER)) {
		Header("WWW-Authenticate: Basic realm=\"Ramcon Tournaments\"");
		Header("HTTP/1.0 401 Unauthorized");
		echo "<font size=+2>HTTP/1.0 401 Unauthorized</font>\n";
		exit;
	}
	$result = mysql_query("select user from TOURNEY_auth where password=PASSWORD('$PHP_AUTH_PW') and user=\"$PHP_AUTH_USER\"");
	$blah = mysql_fetch_array($result);
	if(!$blah) {
		Header("WWW-Authenticate: Basic realm=\"Ramcon Tournaments\"");
		Header("HTTP/1.0 401 Unauthorized");
		echo "<font size=+2>HTTP/1.0 401 Unauthorized</font>\n";
		exit;
	}
?>
