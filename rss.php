<?
	header("Expires: Mon, 26 Jul 1997 05:00:00 GMT");
	header("Last-Modified: " . gmdate("D, d M Y H:i:s") . " GMT");
	header("Cache-Control: no-store, no-cache, must-revalidate");
	header("Cache-Control: post-check=0, pre-check=0", false);
	header("Pragma: no-cache");
	header("Content-type: text/xml");
?>
<? print '<?xml version="1.0" ?>' ?>
<rss version="2.0">
<channel>
<title>David K. Besen's blog</title>
<description>Thoughts on photography and life in the IT world</description>
<link>http://davidbesen.com</link>
<?
$files = glob("text/*"); rsort($files);
foreach($files as $filename) {
	$title = "Unknown title";

	$date = substr(basename($filename), 0, -4);
	$anchor = $date;
	if(!preg_match("/(\d+)-(\d+)-(\d+)/", $date, $matches)) die("Invalid date");
	$y = $matches[1];
	$m = $matches[2];
	$d = $matches[3];
	$date = "$m/$d/$y";

	$file = file($filename);
	foreach($file as $line) {
		if(strcasecmp(trim($line), "publish: no") == 0) {
			if(!isset($_REQUEST['d']))
				continue 2;
			else
				continue;
		}
		if(strncasecmp($line, "shoeboximage:", 13) == 0) {
			continue;
		}
		if(strncasecmp($line, "title:", 6) == 0) {
			$title = trim(substr($line, 6));
			continue;
		}
		if($line{0} == '#') continue;
	}
?><item>
<title><?=$title?></title>
<description><?=$title?> (<?=$date?>)</description>
<link>http://davidbesen.com/#<?=$anchor?></link>
</item>
<? } ?>
</channel>
</rss>
