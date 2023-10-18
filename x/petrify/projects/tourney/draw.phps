<?
	require_once('/home/ramcon/public_html/inc/db.inc');
	require_once('mysql_func.inc');
	if($draw_x) {
		# authenticate
	}
	if($nodraw!=1) Header("Content-type: image/png");
	#print "Question: Why do people have no partner?<br>";
	#print "Answer: This is the most fair way I have found to organize the tournament.  If I just fill names down, ";
	#print "the last person on the list will fight significantly fewer battles than those above him.  If you have ";
	#print "a better way to do it, tell me.<br><br>Note: This is TEST data.  This is NOT the lineup for ramcon.<br><br>";
	#if(!$game) {print "please specify which game<br>"; return;}
	$maxes = mysql_fetch_array(mysql_query("select max(position), max(level) from TOURNEY_users where game=$game"));
	$maxes[2] = floor((log10($maxes[0]+1)/log10(2))+0.5);
	$filename = "/tmp/blah.png";
	if(!$ch) $ch = 20; # cellheight
	$och = $ch; # original cellheight
	if(!$cw) $cw = 180; # cellwidth
	if(!$font) $font = 3; # font
	if(!$ignore) $ignore=0;
	$ch /= pow(2, $ignore);
	if(!$uo) $uo = 12; # underline offset
	if(!$buf) $buf = 30; # how far to the left of the name the split is
	$im = ImageCreate(($maxes[2]+3)*$cw,($maxes[0]+20)*$ch);
	$white = ImageColorAllocate($im, 255, 255, 255);
	$black = ImageColorAllocate($im, 0, 0, 0);
	$grey = ImageColorAllocate($im, 50, 50, 50);
	$red = ImageColorAllocate($im, 255, 0, 0);
	$result = mysql_query("select * from TOURNEY_users where game=$game");
	while($a = mysql_fetch_array($result, MYSQL_ASSOC))
	{
		$MAINARR[$a[position]] = $a;
	}
#	print('<pre>');
#	print_r($MAINARR);
#	print('</pre>');

	for($i=0;$i<=$maxes[0];$i++)
	{
		for($j=$ignore;$j<=$maxes[2];$j+=1)
		{
			if ($MAINARR[$i][level] >= $j) {
				$blah[0] = $MAINARR[$i][id];
				$blah[1] = $MAINARR[$i][user];
			} else {
				$blah = '';	
			}
			$vi = pow(2,$j); # vertical index
			$nvi = pow(2,($j+1)); # next vertical index
			$y = floor($i/$vi) * $ch * $vi;
			$x = ($j-$ignore) * $cw;
			$ny = floor($i/$nvi) * $ch * $nvi; # next y
			$nx = $x + $cw; # next x
			$cvi = ($vi/2) * $ch; # vi in cell height
			$color = $black;
			if($highlight==$blah[0]) $color = $red;
			if($MAINARR[$i+1][user] || $j!=0)
			{
				if($draw_x >= $x-($buf/2) && $draw_y >= $y+$cvi-3 && $draw_x <= $x+$cw-$buf && $draw_y <= $y+$cvi+$och-3)
				{
					if($back)
					{
						mysql_query("update TOURNEY_users set level=TOURNEY_users.level-1 where id=$blah[0] and game=$game");
						$MAINARR[$i][level] -= 1;
						if ($MAINARR[$i][level] >= $j) {
							$blah[0] = $MAINARR[$i][id];
							$blah[1] = $MAINARR[$i][user];
						} else {
							$blah = '';	
						}
					}
					else
					{
						mysql_query("update TOURNEY_users set level=TOURNEY_users.level+1 where id=$blah[0] and game=$game");
						$MAINARR[$i][level] += 1;
					}
				}
				#ImageRectangle($im, $x-($buf/2), $y+$cvi-3, $x+$cw-$buf, $y+$cvi+$och-3, $red);
				ImageString($im, $font, $x, $y+$cvi, $blah[1], $color);
			}
			if ($blah[1] || $j>0) 
			#if(!($j == 0 && $MAINARR[$i][level] > 0))
			if($MAINARR[$i+1][user] || $j!=0)
				ImageLine($im, $x-$buf, $y+$uo+$cvi, $nx-$buf, $y+$uo+$cvi, $grey);
			#if($i%$nvi==0)
			if ($blah[1] || $j>0) 
			if($MAINARR[$i+1][user] || $j!=0)
			if($vi<=$maxes[0]) 
				ImageLine($im, $nx-$buf, $y+$uo+$cvi, $nx-$buf, $ny+$uo+($cvi*2), $grey);
		}
	}
	if($nodraw!=1) ImagePNG($im);
	#print "<img src=\"blah.png\">";
	ImageDestroy($im);
	
?>
