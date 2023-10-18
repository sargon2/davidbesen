<html>
<table border="1"><tr><td>i</td><td>i*2</td></tr>

<?  foreach($nums as $num): ?>
        <tr><td><?= $num ?></td><td><?= $num*2 ?></td></tr>
<?  endforeach; ?>

</table>

<br>Hash with multiple elements example:<br>

<table border="1"><tr><td>i</td><td>i^2</td></tr>

<?  foreach($squares as $num): ?>
        <tr><td><?= $num['num'] ?></td><td><?= $num['square'] ?></td></tr>
<?  endforeach; ?>

</table>

<br>It also works with the {} syntax instead of the ": end" syntax:<br>

<table border="1"><tr><td>i</td><td>i^2</td></tr>

<?  foreach($squares as $num) { ?>
        <tr><td><?= $num['num'] ?></td><td><?= $num['square'] ?></td></tr>
<?  } ?>

</table>

<br>There you have it.  Proper separation of layout content from actual content; no annoying quote escaping; and extremely flexible template syntax, all without a single line of "template engine" code.

</html>
