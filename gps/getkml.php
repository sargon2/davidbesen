<?
	header("Cache-Control: no-cache, must-revalidate"); // HTTP/1.1
	header("Expires: Mon, 26 Jul 1997 05:00:00 GMT"); // Date in the past
	header("Content-type: application/xml");
//	header("Content-type: text/plain");
//	$url = "http://free.3dtracking.net/live.kmz?guid=6af102b0-a5fd-4815-9a9c-2ebfdee219d2";
	$date = date("%d %M %Y");
	$date = urlencode($date);
	$url = "http://free.3dtracking.net/history.kml?Guid=6af102b0-a5fd-4815-9a9c-2ebfdee219d2&UnitId=22716&startdate=$date&hour_starttime=00&min_starttime=00&enddate=$date&hour_endtime=23&min_endtime=55";
	$data = `wget -o /dev/null -O- '$url' | gunzip`;
//	$data = str_replace("<Icon><href>http://free.3dtracking.net/i/3dtracking-logo-no-slogan.gif</href></Icon>", "", $data);
//	$data = str_replace("<Style><IconStyle><Icon><href>http://free.3dtracking.net/i/man41.png</href></Icon></IconStyle></Style>", "", $data);
	$data = str_replace("<Style><IconStyle><Icon><href>http://free.3dtracking.net/i/greenflag1.png</href></Icon></IconStyle></Style>", "", $data);
	$data = str_replace("<Style><IconStyle><Icon><href>http://free.3dtracking.net/i/checkeredflag2.png</href></Icon></IconStyle></Style>", 
		"<Style><IconStyle><Icon><href>http://www.google.com/intl/en_us/mapfiles/ms/micons/red-dot.png</href></Icon></IconStyle></Style>", $data);
	$data = str_replace("<color>ff000000</color>", "<color>8800ff00</color>", $data);
	// If a <coordinates> tag has more than two commas in it, replace every third comma with space :/
	$data = fix_coords_data($data);
	print $data;

	function fix_coords_data($data) {
		$data = split("<coordinates>", $data);
		$first = true;
		$ret = "";
		foreach($data as $part) {
			if($first) {
				$ret .= $part;
				$first = false;
			}
			else {
				$regex = "/^([^<]+)</i";
				if(!preg_match($regex, $part, $matches)) die("Error matching $regex to $part");
				$part = str_replace($matches[1], "<coordinates>" . fix_coords($matches[1]), $part);
				$ret .= $part;
			}
		}
		return $ret;
	}

	function fix_coords($coords) {
		$parts = split(",", $coords);
		$ret = "";
		$m = 0;
		foreach($parts as $part) {
			if($m > 0) {
				if($m % 3 != 0) $ret .= ",";
				else $ret .= " ";
			}
			$ret .= $part;
			$m++;
		}
		return $ret;
	}
?>
