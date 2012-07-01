<html>
<body>
Here are the images I'm thinking of including for my B+W photo project for class.  They're in random order.<br>
The final project will have 15 images on 8x10 paper.<br>
<br><br>
<?
	$files = array();
	$files[] = "2005-09-10-class/CRW_9926.jpg";
	$files[] = "2005-10-13-river%20bend/CRW_0307.jpg";
//	$files[] = "2005-10-13-west%20harmony/CRW_0195.jpg";
	$files[] = "2005-09-10-class/CRW_9986.jpg";
	$files[] = "2005-09-17-various/CRW_0051.jpg";
//	$files[] = "2005-10-13-river%20bend/CRW_0240.jpg";
//	$files[] = "2005-10-13-river%20bend/CRW_0251.jpg";
//	$files[] = "2005-09-14-old%20high%20school/CRW_9993.jpg";
	$files[] = "2005-10-04-poudre/CRW_0138.jpg";
//	$files[] = "2005-09-10-class/CRW_9983.jpg";
	$files[] = "2005-09-10-class/CRW_9972.jpg";
	$files[] = "2005-09-10-class/CRW_9924.jpg";
//	$files[] = "2005-10-06-arapaho%20bend/CRW_0151.jpg";
//	$files[] = "2005-10-13-river%20bend/CRW_0243.jpg";
	$files[] = "2005-10-13-river%20bend/CRW_0257.jpg";
	$files[] = "2005-10-13-river%20bend/CRW_0276.jpg";
	$files[] = "2005-10-06-arapaho%20bend/CRW_0158.jpg";
//	$files[] = "2005-10-04-poudre/CRW_0082.jpg";
	$files[] = "2005-10-18-poudre/CRW_0396.jpg";
//	$files[] = "2005-09-10-class/CRW_9952.jpg";
//	$files[] = "2005-09-17-various/CRW_0047.jpg";
//	$files[] = "2005-10-27-poudre/CRW_0603.jpg";
	$files[] = "2005-09-17-various/CRW_0018.jpg";
	$files[] = "2005-10-27-poudre/CRW_0515.jpg";
	$files[] = "2005-09-17-various/CRW_0009.jpg";
	$files[] = "2005-10-04-poudre/CRW_0086.jpg";
//	$files[] = "2005-10-04-poudre/CRW_0114.jpg";
//	$files[] = "2005-10-06-arapaho%20bend/CRW_0154.jpg";

	shuffle($files);

	if(!isset($size)) $size = 200;

	foreach($files as $file) {
		print "<a href=\"http://davidbesen.com/shoebox/$file\">";
		print "<img alt=\"$file\" style=\"border: 0; margin: 10px; vertical-align: top\" src=\"bw-image.php?file=$file&size=$size\" />";
		print "</a>";
	}
?>
</body>
</html>
