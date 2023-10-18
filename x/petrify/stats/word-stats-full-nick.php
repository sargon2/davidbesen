<?
	require_once('/home/sargon/public_html/inc/supplebot.inc');
	print "<form><input type=text name=str><input type=submit value=\"submit regex\"></form>";
	if($str)
 	{
		$i=1;
		print "People who said $str:<br>";
		$result = mysql_query("select nick as asdf, count(*) as asdf2 from log where contents rlike \"$str\" group by asdf order by asdf2 desc;");
		while($blah = mysql_fetch_array($result)) {
			print "$i: $blah[0]: $blah[1]<br>";
			$i++;
		}
 	} else {
		$i=1;
		$result = mysql_query("select nick as asdf, count(*) as asdf2 from log group by asdf order by asdf2 desc;");
		while($blah = mysql_fetch_array($result)) {
			print "$i: $blah[0]: $blah[1]<br>";
			$i++;
		}
	}
?>
