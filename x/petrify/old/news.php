<?
	require_once('header.php');
	require_once('navbar.php');
	require_once('/home/sargon/public_html/inc/db.inc');
	require_once('mysql_func.inc');
	print '<DIV ALIGN=RIGHT>---------------------[ news</DIV><hr size="0">';
	if($name) {
		$result = mysql_query("select * from personal where user='$name' order by dtime desc");
		while($blah = mysql_fetch_array($result))
			print '<TABLE width="100%"><TR><TD BGCOLOR="#BBBBFF"><DIV ALIGN=RIGHT>['.date("l d F Y h:i:s A",$blah[1])."] - ".$blah[2].'</DIV></TD></TR><TR><TD>'.$blah[3].'</TD></TR></TABLE><br>';
	} else {
		$result = mysql_query("select * from news order by dtime desc");
		while($blah = mysql_fetch_array($result))
			print '<TABLE width="100%"><TR><TD BGCOLOR="#BBFFFF"><DIV ALIGN=RIGHT>['.date("l d F Y h:i:s A",$blah[0])."] - ".$blah[1].'</DIV></TD></TR><TR><TD>'.$blah[2].'</TD></TR></TABLE><br>';
	}

	
	require_once('footer.html');
?>
