<?
	require_once('auth.php');
if($nodraw!=1) print "This is test data.  It has nothing to do with the lineup for any tournament at RamCon.<br>";
if($nodraw!=1) print "<form method=\"POST\" action=\"move.php\">";
foreach ($HTTP_GET_VARS as $key => $value) {
	if($nodraw!=1) echo "<input type='hidden' name='$key' value='$value'>";
	$querystring .= "&$key=$value";
	if($key!="draw_x"&&$key!="draw_y"&&$key!="nodraw") $reloadstring .= "&$key=$value";
}
foreach ($HTTP_POST_VARS as $key => $value) {
	if($nodraw!=1) echo "<input type='hidden' name='$key' value='$value'>";
	$querystring .= "&$key=$value";
	if($key!="draw_x"&&$key!="draw_y"&&$key!="nodraw") $reloadstring .= "&$key=$value";
}

echo "<input type=\"hidden\" name=\"back\" value=\"1\">";
$querystring .= "&back=1";


#if($nodraw!=1) print "<a href=\"move.php?$reloadstring\">reload</a> (please don't use your browser's reload button, it will advance people in the tournament)";
if($nodraw!=1) echo "<input type=\"hidden\" name=\"nodraw\" value=\"1\">";
if($nodraw!=1) echo "<input type=\"image\" border=\"0\" name=\"draw\" alt=\"\" src=\"draw.php?$querystring\">";
if($nodraw==1) require_once("draw.php");
if($nodraw==1) { Header( "Location: move.php?$reloadstring\n\n"); exit;}
if($nodraw!=1) echo "</form>";
?>
