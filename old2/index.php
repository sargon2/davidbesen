<?
	if(!isset($pos)) { // only the main page changes..
		header("Expires: Mon, 26 Jul 1997 05:00:00 GMT");
		header("Last-Modified: " . gmdate("D, d M Y H:i:s") . " GMT");
		header("Cache-Control: no-store, no-cache, must-revalidate");
		header("Cache-Control: post-check=0, pre-check=0", false);
		header("Pragma: no-cache");
	}
?>
<html><head>
<style type="text/css">
td.t {vertical-align: top; font-size: 12px;}
a {color: black; }
.hidden {display: none;}
</style>
</head>
<body bgcolor="white" style="font-family: monospace; font-size: 12px;">
<?
	// Javascript to cache images after main image is loaded
	// Build list of images
	$file = file("pics.txt");

	$pics = array();
	foreach($file as $line) {
		$line = trim($line);
		if(!preg_match('/^(\d\d\/\d\d\/\d\d) ([^ ]+) (.+)$/', $line, $matches)) {
			continue;
			//die("Error parsing file");
		}
		$r['date'] = $matches[1];
		$r['filename'] = $matches[2];
		$r['title'] = $matches[3];
		$pics[] = $r;
	}

	// Find current place in list
	if(!isset($pos)) $pos = count($pics) - 1;
	$filename = $pics[$pos]['filename'];
	if(!$filename) die("error");

	print "<center>";
	print "<br><table width=\"804px\">";
	print "<tr height=\"580px\" style=\"vertical-align: bottom;\">";
	print "<td colspan=\"3\" style=\"text-align: center; overflow: visible;\">";
	print "<img src=\"pics/{$filename}\" galleryimg=\"no\"><br><br>";
	print "</td></tr><tr>";
	$t = "<a href=\"?pos=" . ($pos + 1) . "\">&lt;&lt;</a>";
	if($pos == count($pics) - 1) $t = "";
	print "<td class=\"t\" style=\"text-align: left; width: 30%;\">$t<br>";
	// display when images are precached?
	for($i=$pos+1;$i<count($pics);$i++) {
//		if(abs($i - $pos) == 1) print "Prev";
		print "<a href=\"?pos=$i\">{$pics[$i]['date']} ({$pics[$i]['title']})</a><br>\n";
	}
	print "</td><td class=\"t\" style=\"text-align: center;\">";
	if(count($pics) == 1) {
		print "{$pics[$pos]['date']} ({$pics[$pos]['title']})";
	} else {
		print "<a href=\"?pos=$pos\">{$pics[$pos]['date']} ({$pics[$pos]['title']})</a>";
	}
	$t = "<a href=\"?pos=" . ($pos - 1) . "\">&gt;&gt;</a>";
	if($pos == 0) $t = "";
	print "</td><td class=\"t\" style=\"text-align: right; width: 30%;\">$t<br>";
	for($i=$pos-1;$i>=0;$i--) {
//		if(abs($i - $pos) == 1) print "Next";
		print "<a href=\"?pos=$i\">{$pics[$i]['date']} ({$pics[$i]['title']})</a><br>\n";
	}
	print "</td></tr></table>";
	print "<br><br>my <a href=\"http://davidbesen.com/shoebox/\">shoebox</a> (<a href=\"http://davidbesen.com/shoebox/2005-05-30-rainy%20day/\">updated 05/30/05</a>)";
	print "</center>";
	if($pos > 0) { print "<img src=\"pics/{$pics[$pos-1]['filename']}\" class=\"hidden\">"; }
	if($pos > 1) { print "<img src=\"pics/{$pics[$pos-2]['filename']}\" class=\"hidden\">"; }
	if($pos < count($pics) - 1) { print "<img src=\"pics/{$pics[$pos+1]['filename']}\" class=\"hidden\">"; }
	if($pos > 2) { print "<img src=\"pics/{$pics[$pos-3]['filename']}\" class=\"hidden\">"; }
?>
</body></html>
