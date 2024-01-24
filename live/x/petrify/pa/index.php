<?
	$time = time();
	$date = date("Ymd", $time);
	while(date("w", $time) % 2 == 0) { $time -= 86400; }
	$date = date("Ymd", $time);
	if(!file_exists("/home/sargon/public_html/pa/comix/" . $date . "l.gif")) {
		# print "Don't got it";
		$blah = shell_exec("wget -O /home/sargon/public_html/pa/comix/" . $date . "l.gif http://www.penny-arcade.com/images/2002/" . $date . "l.gif");
		# $blah = str_replace("\xa", "<BR>", $blah);
		# echo $blah;
	}
	header("Location: http://www.petrify.net/pa/comix/". $date . "l.gif");
?>
