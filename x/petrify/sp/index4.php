<TABLE cellpadding="0" cellspacing="3">


<?

   $dir = ".";
   $hdir = opendir($dir);
   $COLS = 4;
   $EXTENSIONS = "jpg png gif";
  
   $i = 0; 
   while ($file = readdir($hdir) ) {
      $fname = substr($file, 0, strlen($file) - 4);
      $ext = substr($file, -3);
      if (!stristr($EXTENSIONS, $ext)) continue;
     
      $infFile = $fname . ".inf"; 
      $name = @fread( fopen($infFile, "r") , filesize($infFile) );
      $name = trim($name);

      if ($i % $COLS == 0) {
         echo "\n</TR>\n<TR>\n";
      }
      echo "<TD align=\"center\">";
      echo "<img src=\"$file\"><BR>\n";
      echo "<button name=\"$fname\" value=\"Give up?\" onClick=\"javascript:". $fname . ".value='" . $name . "'\" >\n";
      echo "</TD> ";
     
      $i++; 
   }

   closedir($hdir);
?>

</TABLE>
