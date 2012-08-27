<?
	// settings

	$page_title = "David K. Besen - Shoebox";

	// todo: thumbnail surroudn color, individual image color separate from thumbnail, link colors
	$bgcolor = "#dddddd";

	$thumb_quality = 40; // 1-100, jpeg compression quality
	$header_prefix = "<a href=\"http://davidbesen.com\">David K. Besen</a> - <a href=\"http://davidbesen.com/shoebox\">Shoebox</a> - ";

	// Do multiviews work? (if yes, path will be ../shoebox/.., if no it will be ../shoebox.php/..)
	$multiviews = true;

	// Perform "find . | grep -i "jpg$" | wc -l" in the top level directory?
	// If yes, displays total count of pictures on main page, makes main page load slowly if you have a lot of pictures.
	$top_find = false;

	// end settings


	// Please don't edit anything below here. -----------------------------------------------------------------
	$full_dir = $_SERVER['SCRIPT_FILENAME'];
	$full_dir = dirname($full_dir);
	$path = $_SERVER['SCRIPT_NAME'];
	$path = dirname($path);
	chdir($full_dir);
	if(is_dir("shoebox-data")) chdir("shoebox-data");
	if($multiviews) $shoebox = "shoebox"; else $shoebox = "shoebox.php";
        if(!isset($noprint)) require_once("./print.php");
?>
