<?
print '$min = pow(floor($i/7),2) + $i<br>';
print '$max = pow(floor($i/5),2) + $i<br>';
print "<center>";
print "<table border=\"1\" cellpadding=\"0\" cellspacing=\"0\">";
print "<tr><td>Intelligence</td><td>Min matk</td><td>Max matk</td>";
print "<td>Min diff</td><td>Max diff</td><td>Range</td><td>Average</td>";
print "</tr>";
$min = 0;
$max = 0;
for($i=1;$i<=150;$i++) {
	$currmin = $min;
	$currmax = $max;
	$min = pow(floor($i/7),2) + $i;
	$max = pow(floor($i/5),2) + $i;
	$mindiff = $min - $currmin;
	$maxdiff = $max - $currmax;
	print "<tr><td>";
	print $i;
	print "</td><td>";
	if($mindiff > 1) print "<b>";
	print $min;
	if($mindiff > 1) print "</b>";
	print "</td><td>";
	if($maxdiff > 1) print "<b>";
	print $max;
	if($maxdiff > 1) print "</b>";
	print "</td><td>";
	if($mindiff > 1) print "<b>";
	print $mindiff;
	if($mindiff > 1) print "</b>";
	print "</td><td>";
	if($maxdiff > 1) print "<b>";
	print $maxdiff;
	if($maxdiff > 1) print "</b>";
	print "</td><td>";
	print $max - $min;
	print "</td><td>";
	print ($max + $min) / 2;
	print "</td></tr>";
}
print "</table></center>";
