<?
	$images = array(
		"the-school.jpg",
		"travel.jpg",
		"pink.jpg",
		"simplicity.jpg",
//		"family.jpg", // obsoleted!
		"ant.jpg",
		"cold.jpg",
		"friend.jpg",
		"swans.jpg",
		"sleep.jpg",
		"uniform.jpg",
		"winter-lilac.jpg",
		"white.jpg",
//		"autumn.jpg", // obsoleted!
		"flight.jpg",
		"wet.jpg",
		"the-tree.jpg",
		"dry.jpg",
		"flower.jpg",
		"fisherman.jpg",
		"bee.jpg",
		"boat.jpg"
	);

	$numimages = count($images);

        $id = $_REQUEST['id'];
	if(isset($id)) {
		$image = "sized/" . $images[$id-1];
?>
<html>
<head>

<style type="text/css">
img {border: 0}
</style>

</head>

<body bgcolor="white">
<center>
<table height="100%" cellpadding="0" cellspacing="0" border="0"><tr><td valign="center">
<table cellpadding="0" cellspacing="0" border="0"><tr><td colspan="3">
<center><b><a href="/old">up</a></b></center></td></tr>
<tr><td><b><?if($id>1) { ?><a href="?id=<?=$id-1?>">back</a><? } else { ?><a href="?id=<?=$numimages?>">back</a><? } ?></b></td><td>
<img src="<?=$image?>">
</td><td><b><?if($id < $numimages) { ?><a href="?id=<?=$id+1?>">next</a><? } else { ?><a href="?id=1">next</a><? } ?></b></td></tr>
</table>
</td></tr></table>
</center>

</body>
</html>
<?
	} else {
?>
<html>
<head>
<title>David K. Besen - Digital Photography</title>
<meta name="description" content="David K. Besen - Digital Photography">
<meta name="keywords" content="digital photography, photography, david besen, david k. besen, besen">
<style type="text/css">
img {border: 0}
.smaller-txt {font-size: 10px; }
.txt {font-size: 12px; }
.bigger-txt {font-size: 14px; }
</style>
</head>
<body bgcolor="white">
<center>
<table height="100%" cellpadding="0" cellspacing="0" border="0"><tr><td valign="center"><center>
<a href="/"><img src="myname.gif"></a><hr width="450">
<?
                $about = $_REQUEST['about'];
		if(isset($about)) {
?>
<table width="400"><tr><td>
<img src="d.gif"><span class="smaller-txt">AVID</span>
<span class="bigger-txt">
is an amateur photographer whose goal is to travel the world in search of that elusive perfect shot.<br>
Growing up, he hand-carved hiking trails into the wondrous face of the Rocky Mountains.
&nbsp;These years gave him both a unique appreciation of what nature has to offer and a strong view of the impression humanity makes on its surroundings.<br>
In his free time he is a full-time IT professional for a research firm somewhere in northern Colorado.<br>
You may contact him <a href="mailto:besen@softhome.net">here</a>.<br>
<br>
go <a href="/old/">back</a>
</span>
</td></tr></table>
<?		} else {
?>
<table cellpadding="0" cellspacing="0" border="0"><tr>
<?
			$c = 0;
			foreach($images as $k=>$v) {
				if($c % 4 == 0 && $c > 0) print "</tr><tr>\n";
				$c++;
?><td><a href="?id=<?=$k+1?>"><img width="100" height="100" src="thumbs/<?=$v?>"></a></td><?
			}
?>
</tr></table>
<?		}
?>
<hr width="450"><div class="txt">
digital photography<br>copyright &copy; 2003-2004<br>
<a href="?about">about david</a> &nbsp;|&nbsp; <a href="/shoebox">the shoebox</a><br>
</div>
</center>
</td></tr></table>
</center>
</body>
<?
	}
?>
