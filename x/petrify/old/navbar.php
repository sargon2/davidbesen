
<table width="100%" cellspacing="0" cellpadding="0" border="0" bgcolor="#FFFFFF">
<tr><td width="1%" valign="top">
<table width="100" cellspacing="0" cellpadding="0" border="0" bgcolor="#FFFFFF">
<tr><td>

<a href="/">home</a><br>
<a href="/news.php">news</a><br>
<a href="/links.php">links</a><br>
<a href="/members.php">members</a><br>
<a href="/dvds.php">DVDs</a><br>
<a href="/irc.php">IRC</a><br>
mail:<br>
&nbsp;&nbsp;<a href="/aeromail">aeromail</a><br>
&nbsp;&nbsp;<a href="/twig">twig</a><br>
<br>Members:<br>

<?
	require_once('/home/sargon/public_html/inc/db.inc');
	require_once('mysql_func.inc');
	$result = mysql_query("select user from auth");
	while($blah = mysql_fetch_array($result))
		print "&nbsp;&nbsp;<a href=\"/news.php?name=$blah[0]\">$blah[0]</a><br>";
?>


</td></tr></table>
<hr size="0" width="75%" align="left">

<br><br>
&nbsp;|<br>
<br>
&nbsp;p<br>
&nbsp;e<br>
&nbsp;t<br>
&nbsp;r<br>
&nbsp;i<br>
&nbsp;f<br>
&nbsp;y<br>
&nbsp;.<br>
&nbsp;n<br>
&nbsp;e<br>
&nbsp;t<br>
<br>
&nbsp;|<br>
</td>
<td valign="top">
