<?
	header("Content-type: image/png");

	$im = imagecreate(101,101);
	$white = imagecolorallocate ($im, 255, 255, 255);
	$black = imagecolorallocate ($im, 0, 0, 0);
	$red = imagecolorallocate($im, 255, 0, 0);

	imageellipse($im, 50, 50, 100, 100, $black);
	imageline($im, 50, 50, 100, 50, $black);
	imageline($im, 50, 50, 50, 100, $black);
	imagefill($im, 51, 51, $red);

	imagepng($im);

	imagedestroy($im);

 
?>
