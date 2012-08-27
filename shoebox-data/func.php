<?
//	clear_thumbs();
	// It's important that this be called before the path to the thumb is sent to the client
	$noprint = true;
	require_once("../shoebox.php");

	function make_thumb($filename) {
		global $thumb_quality;
		if($thumb_quality == 0) die("thumb_quality not set");
		$maxsize = 150;
		$filename = stripslashes($filename);

		if(file_exists(thumbdir($filename))) {
			$sorig = stat($filename);
			$sthumb = stat(thumbdir($filename));
			if($sorig['mtime'] < $sthumb['mtime']) return true;
		}

		// Read the image
                @$im = imagecreatefromstring(file_get_contents($filename));
		if($im === false) return false;
		$w = imagesx($im);
		$h = imagesy($im);
		if($w > $h) {
			$x = $maxsize;
			$y = $maxsize * $h / $w;
		} else {
			$y = $maxsize;
			$x = $maxsize * $w / $h;
		}
		// Make the thumb
		$thumb = imagecreatetruecolor($x, $y);
		imagecopyresampled($thumb, $im, 0, 0, 0, 0, $x, $y, $w, $h);
		// Cache the thumb
		$thumbdir = thumbdir(dirname($filename));
		mkdir_p($thumbdir);
		imagejpeg($thumb, thumbdir($filename), $thumb_quality);
		chmod(thumbdir($filename), 0755);
		return true;
	}

	function thumbdir($path) {
		return "thumbs/" . $path;
	}

	function mkdir_p($dir) {
		$curr = "";
		$chunks = preg_split("/\/+/", $dir);
		foreach($chunks as $v) {
			if(!file_exists($curr . $v)) mkdir($curr . $v, 0755);
			$curr .= $v . "/";
		}
	}

	function clear_thumbs() {
		global $full_dir;
		chdir($full_dir);
		$command = "rm -vfr thumbs/* 2>&1";
		print `$command`;
		print "<br>\nDone.<br>\n";
	}

	function strip_double($str) {
		$ret = preg_replace("/\/+/", "/", $str);
		return $ret;
	}
?>
