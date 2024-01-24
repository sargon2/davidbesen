<?
# header("Content-type: application/octet-stream");
$fp = fopen('/dev/random', 'r');
while(1){
print(fgetc($fp));
}
?>
