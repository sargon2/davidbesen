<?

function output_iptc_data( $image_path ) {   
    $size = getimagesize ( $image_path, $info);       
     if(is_array($info)) {   
        $iptc = iptcparse($info["APP13"]);
        foreach (array_keys($iptc) as $s) {             
            $c = count ($iptc[$s]);
            for ($i=0; $i <$c; $i++)
            {
                echo $s.' = '.$iptc[$s][$i]."\n";
            }
        }                 
    }            
}
	print_r(exif_read_data("_MG_3710.jpg"));
	output_iptc_data("_MG_3710.jpg");

    ob_start();
    readfile("_MG_3710.jpg");
    $source = ob_get_contents();
    ob_end_clean();
    $xmpdata_start = strpos($source,"<x:xmpmeta");
    $xmpdata_end = strpos($source,"</x:xmpmeta>");
    $xmplenght = $xmpdata_end-$xmpdata_start;
    $xmpdata = substr($source,$xmpdata_start,$xmplenght+12);
	print_r($xmpdata);
?>
