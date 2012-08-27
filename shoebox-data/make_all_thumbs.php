<?
	header("Expires: Mon, 26 Jul 1997 05:00:00 GMT");
	header("Last-Modified: " . gmdate("D, d M Y H:i:s") . " GMT");
	header("Cache-Control: no-store, no-cache, must-revalidate");
	header("Cache-Control: post-check=0, pre-check=0", false);
	header("Pragma: no-cache");
	$noprint = true;
	require_once("../shoebox.php");

	require_once("func.php");
	$list = `find pics/ -type f`;
	$list = explode("\n", $list);
	set_time_limit(0);
	$currdir = "";
	foreach($list as $v) {
		if(!preg_match("/jpg$/i", $v)) continue;
		preg_match("/^(.*\/)([^\/]*)$/", $v, $matches);
		if($matches[1] != $currdir) {
			$currdir = $matches[1];
			print "<br>\n" . $currdir;
		}
		print $matches[2] . " ";
		make_thumb(trim($v));
		flush();
	}
?>
