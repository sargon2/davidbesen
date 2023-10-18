<?
	require_once('../header.html');
	$loc = "projects ircd";
	require_once('../navbar.php');
	print '<DIV ALIGN=RIGHT>---------------------[ ircd</DIV><hr size="0">';
?>
My IRCD is a project that I have planned, but haven't started coding on yet.  It will be a fully functional IRCD, with some rather glaring extensions:  <br>
<br>
- More than 1 connection per nick<br>
- Reliable nexts, seen<br>
- Paster will paste using your nick<br>
- Realtime regexes (still not sure if this is fair)<br>
- SQL Logging<br>
- Inability to change your nick to another registered nick (Fascist?)<br>
- Customizable golast -- ignore people or certain urls for your own golast<br>
- Customizable next receiving -- ignore people or nexts that match regexes<br>
- Permissions, instead of a level system, will be a set of toggles<br>
- Ignored nexts will still be stored, and you will be able to proactively check them<br>
- Easily extensible with bot-like scripts (such as a voting engine)<br>
<br>
I still haven't decided if it will force you to ID before you can join a channel or not.. the advantage of this is it makes everything reliable, and the disadvantage is that logging in is annoying.<br>
<br>Update: Development has been aborted on this project.  It's just not worth it.<br>

<?
	require_once('../footer.html');
?>
