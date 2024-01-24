<HTML><HEAD>
<style>
td { font-family: courier; font-size: 8pt; }
a { font-family: courier; font-size: 8pt; }
.content { font-family: courier; font-size: 8pt; }
.bigger { font-family: courier; font-size: 12pt; }
</style><TITLE>petrify.net</TITLE></HEAD>
<BODY BGCOLOR="#FFFFFF" TEXT="#666666" LINK="#666666" VLINK="#666666" ALINK="#666666" marginheight="0" topmargin="0" class="content">
<BR><CENTER>
<TABLE cellspacing="0" cellpadding="0" border="0" valign="center"><tr><td nowrap>----------[&nbsp;</td><td nowrap>
<a href="/"><img src="/petrify_small.jpg" width="300" height="83" border=0></a></td><td nowrap>&nbsp;]----------</td></tr></table>

<?
	$quotes = file('/home/sargon/data/quotes.txt');
	print $quotes[array_rand($quotes)];

?>
</CENTER>
<hr size="0">
