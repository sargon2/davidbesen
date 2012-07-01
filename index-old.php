<?
	header("Expires: Mon, 26 Jul 1997 05:00:00 GMT");
	header("Last-Modified: " . gmdate("D, d M Y H:i:s") . " GMT");
	header("Cache-Control: no-store, no-cache, must-revalidate");
	header("Cache-Control: post-check=0, pre-check=0", false);
	header("Pragma: no-cache");

	// Temporarily disabled:
	header("Location: http://davidbesen.com/shoebox"); die();
?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN" "http://www.w3.org/TR/REC-html40/loose.dtd">
<html>
<head>
<style type="text/css">
body {
	font: 0.75em Verdana, sans-serif;
	margin: 0;
	padding: 0;
	background-color: white;
}
.entry a:link {
	border-bottom: 1px solid #7bf;
	color: #23f;
	text-decoration: none;
}
.entry a:visited {
	border-bottom: 1px solid #b7f;
	color: #52f;
	text-decoration: none;
}
.entry {
	text-align: left;
	padding: 0 2em 0.5em 2em;
	text-indent: 7ex;
	line-height: 1.4em;
}
.entry p {
	padding: 0 0 1em 0;
	margin: 0;
}
.insetimage {
	float: left;
	border: 1px solid black;
	margin: 0.3em 2em 0.5em 0em;
}
.boxed {
	border: 1px solid #aaa;
	margin: 0 0 3em 0;
	position: relative;
}
.boxed h1 {
	display: inline;
	border-left: 1px solid #aaa;
	border-right: 1px solid #aaa;
	position: relative;
	top: -0.75em;
	left: 4em;
	text-decoration: none;
	font-weight: normal;
	background: white;
	font-size: 1em;
	text-align: left;
	padding: 0 0.3em;
}
.outer {
	margin: 4em 10% 0 10%;
}
.banner {
	margin-top: 3em;
	margin-left: 10%;
	text-align: right; /* puts the image on the right */
	border-right: 1px solid black;
	border-bottom: 1px solid black;
	position: relative;
	width: 80%; /* so bannertext displays right in ie (hack) */
}
.bannertext {
	position: absolute;
	bottom: 0;
	left: 2.5em;
	font-size: 1.4em;
	font-weight: bold;
	white-space: nowrap;
}
.bannernav {
	font: 1em "Courier New", monospace;
	position: absolute;
	right: 0;
	top: 59px;
	display: inline;
	white-space: nowrap;
}
.subbanner {
	font: 1em "Courier New", monospace;
	position: absolute;
	left: 2em;
	top: 59px;
	display: inline;
}
.bannernav a:link {
	color: #23f;
}
.bannernav a:visited {
	color: #52f;
}
.clear { /* so the image doesn't overlap the bounding box when there isn't much text */
	clear: both;
}
.signature {
	float: right;
	padding: 0;
	margin: 0;
	position: relative;
	top: -0.5em;
	right: 8em;
}
</style>
<title>David K. Besen</title>
</head>
<body>
<div class="banner"><div class="bannertext">david k. besen</div><?
?><div class="subbanner">thoughts on photography and life in the IT world</div><?
?><div class="bannernav">[ <a href="http://davidbesen.com/">words</a> &amp; <a href="http://davidbesen.com/shoebox/">pictures</a> ]</div><img src="banner.gif" alt=""></div>
<div class="outer">
<? $files = glob("text/*"); rsort($files); ?>
<? foreach($files as $filename) {
	$image = null;
	$title = "Unknown title";

	$date = substr(basename($filename), 0, -4);
	$anchor = $date;
	if(!preg_match("/(\d+)-(\d+)-(\d+)/", $date, $matches)) die("Invalid date");
	$y = $matches[1];
	$m = $matches[2];
	$d = $matches[3];
	$date = "$m/$d/$y";

	$file = file($filename);
	$out = "";
	foreach($file as $line) {
		if(strcasecmp(trim($line), "publish: no") == 0) {
			if(!isset($_REQUEST['d']))
				continue 2;
			else
				continue;
		}
		if(strncasecmp($line, "shoeboximage:", 13) == 0) {
			$image = trim(substr($line, 13));
			continue;
		}
		if(strncasecmp($line, "title:", 6) == 0) {
			$title = trim(substr($line, 6));
			continue;
		}
		if($line{0} == '#') continue;
		$out .= $line;
	}
	$out = str_replace("  ", "&nbsp ", $out);
	$out = str_replace("\n\n", "\n", $out);
	$out = rtrim($out);
	$out = "<p>" . str_replace("\n", "</p><p>", $out) . "</p>"; // notice the missing </p> here
?>
<a name="<?=$anchor?>">
<div class="boxed">
<h1><b><?=$title?></b> - <?=$date?></h1>
<div class="entry">
<? if($image != null) { ?>
<a href="http://davidbesen.com/shoebox/<?=$image?>"><img class="insetimage" alt="" src="http://davidbesen.com/shoebox-data/thumbs/pics/<?=$image?>"></a>
<?}?>
<?=$out?>
<div class="clear"></div>
<div class="signature"> -- dave</div>
<div class="clear"></div>
</div>
</div>
<? } ?>
<center>[next page]<br><br>[<a href="mailto:dbesen@gmail.com">email</a>] [<a href="http://davidbesen.com/rss.php">rss</a>]</center>
<br>
</div>
</body>
</html>
