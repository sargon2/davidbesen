<?
	require_once('header.html');
	$loc = blog;
	require_once('navbar.php');
	print '<DIV ALIGN=RIGHT>---------------------[ blog</DIV><hr size="0">';
	print "asdf";
	$result = mysql_query("select * from news order by dtime desc");
	while($blah = mysql_fetch_array($result))
	//	print '<table width="100%"><tr><td nowrap bgcolor="#333333"><div align=right>'.$blah[1].' - ['.date("l d/m/y H:i:s",$blah[0])."]".'</div></td></tr><tr><td>'.$blah[2].'</td></tr></table><br>';
	require_once('footer.html');
?>
