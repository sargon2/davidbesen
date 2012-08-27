<?
	header("Expires: Mon, 26 Jul 1997 05:00:00 GMT");
	header("Last-Modified: " . gmdate("D, d M Y H:i:s") . " GMT");
	header("Cache-Control: no-store, no-cache, must-revalidate");
	header("Cache-Control: post-check=0, pre-check=0", false);
	header("Pragma: no-cache");

	$cellsize = 160;

	if(!isset($dir)) $dir = $_SERVER['PATH_INFO'];

	if($dir == "") $dir = "/";

	set_time_limit(0);
?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<style type="text/css">
img { border: 1px solid black; }
hr { height: 1px; }
.i { height: <?=$cellsize?>px; width: <?=$cellsize?>px; text-align: center; background-color: white; }
.b { border: 1px solid black; margin: 5px 5px 5px 5px; } 
.f { float: left; }
.n { border: none; margin: 0px 0px 0px 0px; }
.bouter {border: solid 20px black; padding: 1px; margin: 0; }
.binner {border: solid 8px black; margin: 0; padding: 0; vertical-align: bottom; }
.hidden {display:none;}
</style>
<title><?=$page_title?></title>
</head><body bgcolor="<?=$bgcolor?>">
<?
	chdir($full_dir . "/shoebox-data");

	require_once("func.php");
	if(!isset($dir)) $dir = "";
	$dir = stripslashes($dir);
	$dir = str_replace("..", "", $dir);
	$dir = str_replace("~", "", $dir);

	$rdir = realpath("pics/" . $dir);

        $a = escapeshellarg($rdir);

	if(($top_find == false) && ($dir == "/"))
		$count = false;
	else
                $count = `find $a -type f | egrep -i "jpg$" | wc -l`;



//	$favs = null;
//	if(file_exists($rdir . "/favs.txt")) {
//		$favs = file($rdir . "/favs.txt");
//	}

        $favs = get_favs($rdir);

	$file = "";
	if(file_exists($rdir . "/text.txt")) {
		$file = file($rdir . "/text.txt");
		$file = implode("<br>\n", $file);
	}
	$c = $dir;
	if($c == "") $c = "shoebox";
	print $header_prefix;
	if($count == false || $count <= 1)
		print "Current directory: <b>$c</b><br>\n";
	else
		print "Current directory: <b>$c</b> - $count images<br>\n";
	if($file != "") {
		print "<hr><div style=\"background-color: white; border: 1px solid black; padding: 4px; width: 500px; display: block;\">$file</div>";
	}
	print "<hr>";

	if(!file_exists($rdir)) {
		die("404 - Not found");
        }

	if(!is_dir($rdir)) {
		$dirs = array("..");
		// we only do this in order to get next/back.. :/
		$dh = opendir(dirname($rdir));
		$files = array();
		$found = -1;
		while (($file = readdir($dh)) !== false) {
			if(!is_file(dirname($rdir) . "/" . $file)) continue;
			if(!preg_match("/jpg$/i", $file)) continue;
			$files[] = $file;
		}
		sort($files); // not asort since we reference it as +1/-1 later
		foreach($files as $k=>$file) {
			if($file == basename($rdir)) $found = $k;
		}
		$pics = "";
		if($found > 0) { $dirs['prev'] = $files[$found-1]; }
		if($found < count($files)-1) { $dirs['next'] = $files[$found+1]; }
		if($found < count($files)-2) { $dirs['next2'] = $files[$found+2]; }
	} else {
                $dh = opendir($rdir);
		$dirs = array();
		$pics = array();
                while (($file = readdir($dh)) !== false) {
			if($file == ".") continue;
			if($file == "..") continue;
			if(is_dir($rdir . "/" . $file)) {
				// print it..
				$dirs[] = $file;
			} else {
				// assume it's a picture
				if(!preg_match("/jpg$/i", $file)) continue;
				$pics[] = $file;
			}
                }
		closedir($dh);
		arsort($dirs);
//		asort($pics);
		uasort($pics, "fcmp");
                array_unshift($dirs, "..");
	}

	function fcmp($a, $b) {
		$a = substr($a, 1);
		$b = substr($b, 1);
		return strcmp($a, $b);
	}

        function print_favs($favs) {
		if(is_array($favs)) {
			print "Jump to favorites:<br>\n";
			foreach($favs as $v) {
				print_pic($v, true);
			}
			print "<hr style=\"clear: left\">All pictures:<br>";
		}
	}

	if(!count($pics)) print_favs($favs);

	$next_image = "";
	$next2_image = "";
        $prev_image = "";
        foreach($dirs as $k=>$v) {
		if($v == "..") {
			$loc = preg_replace("/\/?[^\/]+\/*$/", "", $dir);
		} else {
			$dir2 = $dir;
			if($dir2 == "/") $dir2 = "";
			if($k === "prev" || $k === "next" || $k === "next2") $loc = dirname($dir) . "/" . $v;
			else $loc = $dir2 . "/" . $v;
		}
		if($v == "..") $v = "Parent directory";

		$loc = str_replace(" ", "%20", $loc);
		if($dir == "/" && $v == "Parent directory") continue;
		if($k === 'prev') {
			print strip_double("<a href=\"/$path/$shoebox/$loc\">previous image</a>&nbsp;&nbsp;|&nbsp;&nbsp;");
			$prev_image = strip_double("/$path/shoebox-data/pics/$loc");
		}
		else if($k === 'next') {
			print strip_double("<a href=\"/$path/$shoebox/$loc\">next image</a><br>\n");
			$next_image = strip_double("/$path/shoebox-data/pics/$loc");
		}
		else if($k === 'next2') {
			$next2_image = strip_double("/$path/shoebox-data/pics/$loc");
			continue;
		}
		else {
			if($v == "Parent directory") print strip_double("<img class=\"n\" src=\"/$path/shoebox-data/back.gif\">&nbsp;&nbsp;");
			else print "<img class=\"n\" src=\"" . strip_double("/$path/shoebox-data/folder.gif") . "\">&nbsp;&nbsp;";
			print strip_double("<a href=\"/$path/$shoebox/$loc\">");
			print "$v</a>";
			print "<br>\n";
		}
	}
	print "<hr>";

	if(count($pics)) print_favs($favs);

	function print_pic($p, $f = false) {
		$p = trim($p);
		global $dir;
		global $path;
		global $shoebox;
		global $loc;
		// we have to make the thumb before giving the client the image location
		if(!make_thumb("pics/" . $dir . "/" . $p)) return;
		if(!$f) print "<div class=\"i b f\">";
		else print "<div class=\"i b f\">";
		print "<table class=\"i n\" width=\"100%\" height=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">";
//			$loc = "pics/$dir/$v";
		$loc = "/$path/$shoebox/$dir/$p";
		$loc = strip_double($loc);
		print "<tr><td align=\"center\" valign=\"middle\"><a href=\"$loc\">";
		$loc = "/$path/shoebox-data/thumbs/pics/$dir/$p";
		$loc = strip_double($loc);
		print "<img src=\"$loc\" alt=\"\">";
		print "</a></td></tr></table></div>\n";
		flush(); // in case make_thumb is slow
	}

	if(is_array($pics)) {
		foreach($pics as $v) {
			print_pic($v);
		}
	} else {
		$loc = "pics/$dir";
		$loc = strip_double($loc);
		$exif = exif_read_data($loc);
//		print "<pre>"; print_r($exif);
		$html = $exif['COMPUTED']['html'];
		print strip_double("<br><center><table style='background-color: black;' cellspacing='0' cellpadding='0'><tr><td class='bouter' style='background-color: white;'><img class='binner' src=\"/$path/shoebox-data/$loc\" $html></img></td></tr></table></center><br><hr>");
		if($exif['ExposureTime']) {
			print $exif['Model'] . "<br>\n";
			print $exif['ExposureTime'] . " sec at ";
			print $exif['COMPUTED']['ApertureFNumber'] . ", ISO ";
			print $exif['ISOSpeedRatings'] . ", ";
			$len = $exif['FocalLength'];
			$len = str_replace("/1", "", $len);
			$len2 = $len * 1.6;
			print $len . "mm ({$len2}mm equiv)<br>\n";
			$exposuretime = $exif['DateTimeOriginal'];
			preg_match('/^(\d+):(\d+):(\d+) (\d+):(\d+):(\d+)$/', $exposuretime, $matches);
			// if it's a raw, convert it from GMT to MDT
			if(strstr($loc, "CRW")) {
				$utime = mktime($matches[4]+6, $matches[5], $matches[6], $matches[2], $matches[3], $matches[1]);
			} else {
				$utime = mktime($matches[4], $matches[5], $matches[6], $matches[2], $matches[3], $matches[1]);
			}
			//$exposuretime = strftime("%Y:%m:%d %H:%M:%S", $utime);
			$exposuretime = strftime("%m/%d/%Y %I:%M:%S %p", $utime);
			//$exposuretime = strftime("%D %r", $utime);
			print "Exposed at " . $exposuretime;
		}
		if($next_image != "") {
			print "<img src=\"$next_image\" class=\"hidden\">";
		}
		if($next2_image != "") {
			print "<img src=\"$next2_image\" class=\"hidden\">";
		}
		if($prev_image != "") {
			print "<img src=\"$prev_image\" class=\"hidden\">";
		}
//		print "<pre>";
//		print_r($exif);
	}

	function get_favs($rdir) {
		$r = escapeshellarg($rdir);
		$favs = explode("\n", `find $r -name favs.txt | sort -r`);
		$pics = array();
		foreach($favs as $f) {
			if(trim($f) == "") continue;
			$d = file($f);
			$dir = dirname($f);
			foreach($d as $pic) {
				$pic = trim($pic);
				if($pic == "") continue;
				if($dir == $rdir) $pics[] = $pic;
				else $pics[] = substr($dir, strlen($rdir)) . "/" . $pic;
			}
		}
		if(!count($pics)) return null;
		return $pics;
	}

?>
</body></html>
