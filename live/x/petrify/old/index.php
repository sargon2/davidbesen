<?
	require_once('header.php');
	require_once('navbar.php');
	print "<DIV ALIGN=RIGHT>--------------[ petrify.net</DIV>";
	print '<hr size="0">';
?>
                        <!-- Search Google -->
                        <FORM method=GET action="http://www.google.com/search">
                        <TABLE bgcolor="#FFFFFF"><tr><td>
                        <A HREF="http://www.google.com/">
                        <IMG SRC="http://www.google.com/logos/Logo_40wht.gif" border="0" 
                         ALT="Google" align="absmiddle"></A>
                        <INPUT TYPE=text name=q size=25 maxlength=255 value="">
                        <INPUT type=submit name=btnG VALUE="Google Search">
                        </td></tr></TABLE>
                        </FORM>
                        <!-- Search Google -->
<?
	print 'site design suggestions to <a href="mailto:sargon@petrify.net">sargon</a>';
	require_once('footer.html');
?>
