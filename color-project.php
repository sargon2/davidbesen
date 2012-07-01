<html>
<body>
Here are the images I'm thinking of including for my color photo project for class.  They're in random order.<br>
The final project will have 15 images on 8x10 paper.<br>
<br><br>
<?
	$files = array();
//	$files[] = "2005-11-13-old%20town/CRW_0665.jpg";
	$files[] = "2005-11-13-old%20town/CRW_0668.jpg";
//	$files[] = "2005-11-13-old%20town/CRW_0671.jpg"; // trucks
	$files[] = "2005-11-13-old%20town/CRW_0722.jpg";
//	$files[] = "2005-11-13-old%20town/CRW_0751.jpg"; // stripped bike
//	$files[] = "2005-11-13-old%20town/CRW_0763.jpg"; // trash1
//	$files[] = "2005-11-17-old%20town/CRW_0831.jpg"; // monkey
//	$files[] = "2005-11-17-old%20town/CRW_0843.jpg"; // lamppost
//	$files[] = "2005-11-17-old%20town/CRW_0852.jpg";
	$files[] = "2005-11-17-old%20town/CRW_0861.jpg"; // orange bricks
	$files[] = "2005-11-13-old%20town/CRW_0698.jpg";
	$files[] = "2005-11-13-old%20town/CRW_0683.jpg";
//	$files[] = "2005-11-17-old%20town/CRW_0819.jpg"; // metal dog
//	$files[] = "2005-12-10-old%20town/CRW_0905.jpg"; // grain thing
//	$files[] = "2005-12-10-old%20town/CRW_0912.jpg"; // statue
//	$files[] = "2005-12-11-old%20town/CRW_1009.jpg"; // bear
	$files[] = "2005-12-11-old%20town/CRW_0997.jpg";
//	$files[] = "2005-12-11-old%20town/CRW_1047.jpg"; // horses
//	$files[] = "2005-12-11-old%20town/CRW_1053.jpg"; // chairs
	$files[] = "2005-12-11-old%20town/CRW_1060.jpg";
//	$files[] = "2005-12-11-old%20town/CRW_1064.jpg"; // trash2
	$files[] = "2005-12-10-old%20town/CRW_0884.jpg";
	$files[] = "2005-12-11-old%20town/CRW_1055.jpg";
	$files[] = "2005-11-13-old%20town/CRW_0704.jpg";
	$files[] = "2005-12-11-old%20town/CRW_0994.jpg";
//	$files[] = "2005-12-11-old%20town/CRW_0995.jpg"; // windows
	$files[] = "2005-11-13-old%20town/CRW_0711.jpg"; // church
	$files[] = "2005-11-15-old%20town/CRW_0771.jpg";
//	$files[] = "2005-11-17-old%20town/CRW_0872.jpg";
//	$files[] = "2005-11-17-old%20town/CRW_0813.jpg";
	$files[] = "2005-12-11-old%20town/CRW_0980.jpg";
//	$files[] = "2005-12-11-old%20town/CRW_1002.jpg"; // boarded window
	$files[] = "2005-12-11-old%20town/CRW_1006.jpg";

	shuffle($files);

	if(!isset($size)) $size = 200;

	foreach($files as $file) {
		print "<a href=\"http://davidbesen.com/shoebox/$file\">";
		print "<img alt=\"$file\" style=\"border: 0; margin: 10px; vertical-align: top\" src=\"color-image.php?file=$file&size=$size\" />";
		print "</a>";
	}
?>
</body>
</html>
