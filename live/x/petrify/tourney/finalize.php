<?
	require_once('/home/ramcon/public_html/inc/db.inc');
	require_once('mysql_func.inc');
	require_once('auth.php');
	$blah = mysql_fetch_array(mysql_query("select count(id) from TOURNEY_users where game=$game"));
	$num = 1;
	while($num < $blah[0]) $num *= 2;
	print $num."<br>";
	$j = 0;
	mysql_query("update TOURNEY_users set level=0 where game=$game");
	$result = mysql_query("select id from TOURNEY_users  where game=$game order by rand()");
	while($blah = mysql_fetch_array($result))
	{
		mysql_query("update TOURNEY_users set position=$j where id=$blah[0] and game=$game");
		print ("$blah[0]: $j<br>");
		print mysql_error();
		if($j+2 < $num) $j+=2; else $j=1;
	}
	$j--;
	while($j <= $num)
	{
		mysql_query("update TOURNEY_users set level=1 where position=$j and game=$game");
		$j+=2;
	}
?>
