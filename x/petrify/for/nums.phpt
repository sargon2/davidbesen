<?
	$rows = getrows($nums, $cols);
	$rows2 = getrows($nums, $cols2);

	function getrows($nums, $cols) {
		if(count($nums) % $cols == 0) {
			$rows = count($nums) / $cols;
		} else {
			$rows = 1 + floor(count($nums) / $cols);
		}
		return $rows;
	}
?>

<table border="1" bgcolor="blue">

	
<? for($i=0;$i<$rows;$i++) { ?>
	<tr>
	<? for($j=0;$j<$cols;$j++) { ?>
		<td><?=$nums[$i+$j*$rows]?></td>
	<? } ?>
	</tr>
<? } ?>

</table>
<br>
<table border="1" bgcolor="red">

<? for($i=0;$i<$rows2;$i++) { ?>
	<tr>
	<? for($j=0;$j<$cols2;$j++) { ?>
		<td><?=$nums[$i+$j*$rows2]?></td>
	<? } ?>
	</tr>
<? } ?>
