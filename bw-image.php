<?
	if(!isset($size)) $size = 300;
	$im = imagecreatefromjpeg("/home/sargon/davidbesen/shoebox-data/pics/$file");

	$wid = imagesx($im);
	$height = imagesy($im);
	if($wid < $height) {
		$new_height = $size;
		$new_width = ($new_height / $height) * $wid;
	} else {
		$new_width = $size;
		$new_height = ($new_width / $wid) * $height;
	}
	$new = imagecreatetruecolor($new_width, $new_height);
	imagecopyresampled($new, $im, 0, 0, 0, 0, $new_width, $new_height, $wid, $height);
	imagetruecolortopalette($new, true, 256);
	ConvertGreyscale($new);

	header("Content-type: image/jpeg");
	ImageJpeg($new);

	function ConvertGreyscale($image){
		# this file outputs a grey version of specified image
		$total = ImageColorsTotal($image);
		for( $i=0; $i<$total; $i++){
			$old = ImageColorsForIndex($image, $i);
			#trying to keep proper saturation when converting
			$commongrey = (int)(($old[red] + $old[green] + $old[blue]) / 3);
			ImageColorSet($image, $i, $commongrey, $commongrey, $commongrey);
		}
	}

?>
