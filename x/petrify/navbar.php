<table width="100%" cellspacing="0" cellpadding="0" border="0">
<tr><td width="1%" valign="top">
<table width="175" cellspacing="0" cellpadding="0" border="0">
<tr><td nowrap>

<?
require_once('/home/sargon/public_html/inc/db.inc');
if($loc == "blog") { print "-["; } else { print "&nbsp;&nbsp;"; } print '&nbsp;<a href="/">blog</a><br>';
if($loc == "projects") { print "-["; } else { print "&nbsp;&nbsp;"; } print '&nbsp;<a href="/projects/">projects</a><br>';
if(substr($loc, 0, 8) == "projects") {
	if($loc == "projects ircbots") print "---["; else print "&nbsp;&nbsp;&nbsp;&nbsp;";
	print '&nbsp;<a href="/projects/ircbots.php">Irc Bots</a><br>';
	if(substr($loc, 0, 16) == "projects ircbots") {
		if($loc == "projects ircbots thumper") print "-----["; else print "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
		print '&nbsp;<a href="/projects/botsthumper.php">thumper</a><br>';
		if($loc == "projects ircbots paster") print "-----["; else print "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
		print '&nbsp;<a href="/projects/botspaster.php">paster</a><br>';
		if($loc == "projects ircbots roshambot") print "-----["; else print "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
		print '&nbsp;<a href="/projects/botsroshambot.php">RoShamBot</a><br>';
	}
	if($loc == "projects elicitum") print "---["; else print "&nbsp;&nbsp;&nbsp;&nbsp;";
	print '&nbsp;<a href="/projects/elicitum.php">elicitum</a><br>';
	if($loc == "projects elicitum") print '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="http://www.elicitum.com">elicitum.com</a><br>';
	if($loc == "projects ircd") print "---["; else print "&nbsp;&nbsp;&nbsp;&nbsp;";
	print '&nbsp;<a href="/projects/ircd.php">IRCD</a><br>';
	if($loc == "projects tourney") print "---["; else print "&nbsp;&nbsp;&nbsp;&nbsp;";
	print '&nbsp;<a href="/projects/tourney.php">Tourney engine</a><br>';
}
if($loc == "dvds") { print "-["; } else { print "&nbsp;&nbsp;"; } print '&nbsp;<a href="/dvds.php">dvds</a><br>';
if(substr($loc, 0, 4) == "dvds") {
	$result = mysql_query("select distinct name from dvds");
	while($blah = mysql_fetch_array($result)) {
		if($loc == "dvds $blah[0]") print "---["; else print "&nbsp;&nbsp;&nbsp;&nbsp;";
		print "&nbsp;<a href=\"/dvds.php?name=$blah[0]\">$blah[0]</a><br>"; }
	}
if($loc == "irc") { print "-["; } else { print "&nbsp;&nbsp;"; } print '&nbsp;<a href="/irc.php">irc</a><br>';
if(substr($loc, 0, 3) == "irc") { print '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="/cgi-bin/irc.cgi">cgi-irc</a><br>'; }
/*if(substr($loc, 0, 3) == "irc") {
	if($loc == "irc grep") print "---["; else print "&nbsp;&nbsp;&nbsp;&nbsp;";
	print '&nbsp;<a href="/grepulator.php">grepulator</a><br>';
}*/
if($loc == "hardware") { print "-["; } else { print "&nbsp;&nbsp;"; } print '&nbsp;<a href="/hardware/">hardware</a><br>';
if(substr($loc, 0, 8) == "hardware") {
	if($loc == "hardware l2") print "---["; else print "&nbsp;&nbsp;&nbsp;&nbsp;";
	print '&nbsp;<a href="/hardware/l2.php">libretto</a><br>';
	if(substr($loc, 0, 11) == "hardware l2") {
		if($loc == "hardware l2 linux") print "-----["; else print "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
		print '&nbsp;<a href="/hardware/l2linux.php">linux</a><br>';
		if($loc == "hardware l2 windows") print "-----["; else print "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
		print '&nbsp;<a href="/hardware/l2windows.php">windows</a><br>';
	}
	if($loc == "hardware portege") print "---["; else print "&nbsp;&nbsp;&nbsp;&nbsp;";
	print '&nbsp;<a href="/hardware/portege.php">portege</a><br>';
	if($loc == "hardware hoss") print "---["; else print "&nbsp;&nbsp;&nbsp;&nbsp;";
	print '&nbsp;<a href="/hardware/hosstop.php">hosstop</a><br>';
	if($loc == "hardware tv") print "---["; else print "&nbsp;&nbsp;&nbsp;&nbsp;";
	print '&nbsp;<a href="/hardware/tv.php">tv server</a><br>';
	if($loc == "hardware misc") print "---["; else print "&nbsp;&nbsp;&nbsp;&nbsp;";
	print '&nbsp;<a href="/hardware/misc.php">miscellaneous</a><br>';
}
if($loc == "members") { print "-["; } else { print "&nbsp;&nbsp;"; } print '&nbsp;<a href="/members.php">members</a><br>';
?>

</td></tr></table>
<hr size="0" width="90%" align="left">

<td valign="top">
