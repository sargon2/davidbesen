<?
	require_once('header.html');
	$loc = "irc grep";
	require_once('navbar.php');
?>
<DIV ALIGN=RIGHT>----------------------[ irc</DIV><hr size="0">
cgi-irc <a href="/cgi-bin/irc.cgi">here</a><br><br>
<?
	if($grep) {
		if($N == 0) { $N = 50; }
		if($N > 1000) { $N = 1000; }
		$grep = str_replace("$(", "\\$(", $grep);
		$grep = str_replace("`", "", $grep);
		//$grep = str_replace("<", "", $grep);
		//$grep = str_replace(">", "", $grep);
		//$grep = str_replace("|", "", $grep);
		$buffer = htmlentities($grep);
		$buffer = str_replace('\\\\', '\\', $buffer);
		print 'Grepped for "'.$buffer.'", ';
		$fp = popen('cd /home/talon/eggdrop/mel/logs; egrep "'.$grep.'" *linux* | grep -v -i "sarg.*smok" | grep -v -i "sarg.*ciga" | grep -v -i "sarg.*dunk" | grep -v -i "sarg.*drunk" | wc -l', "r");
		$buffer = fgets($fp, 4096);
		print $buffer; print " hits<br><hr size=0>";
		pclose($fp);
		$fp = popen('cd /home/talon/eggdrop/mel/logs; egrep "'.$grep.'" *linux* | grep -v -i "sarg.*smok" | grep -v -i "sarg.*ciga" | grep -v -i "sarg.*dunk" | grep -v -i "sarg.*drunk" | tail -' . $N, "r");
		while(!feof($fp))
		{
			$buffer = fgets($fp, 4096);
			print htmlentities($buffer); print "<br>";
		}
		pclose($fp);
	} else {
		print 'The grepulator uses egrep to grep the last year\'s #linux logs.  "$(" is replaced with "\\$(" and "`" is replaced with "".<br>';
		print "<form>";
		print 'Grep for something in the IRC logs:<br>';
		print '<input type="text" name="grep">';
		print '<input type="text" name="N" size="4" value="50">';
		print '<input type="submit" value="submit">';
		print "</form>";
	}
?>
<? require_once('footer.html'); ?>
