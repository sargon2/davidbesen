<?
	require_once('inc/ramcon-db.inc');
	require_once('mysql_func.inc');
	if($game)
	{
		$result = mysql_query("select user from TOURNEY_users where game=$game order by user");
		while($blah = mysql_fetch_array($result))
			print "$blah[0]<br>";
	}
	else
	{
		$result = mysql_query("select user, TOURNEY_tourneys.game from TOURNEY_users left join TOURNEY_tourneys on TOURNEY_users.game=TOURNEY_tourneys.id order by TOURNEY_tourneys.game");
		while($blah = mysql_fetch_array($result))
			print "$blah[1]: $blah[0]<br>";
	}
?>
