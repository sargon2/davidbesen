<TABLE cellpadding="0" cellspacing="3">


<?

   $dir = ".";
   $hdir = opendir($dir);
   $COLS = 3;
   $EXTENSIONS = "jpg png";
  
   $i = 0; 
   while ($file = readdir($hdir) ) {
      $ext = substr($file, -3);
      if (!stristr($EXTENSIONS, $ext)) continue;

      if ($i % $COLS == 0) {
         echo "\n</TR>\n<TR>\n";
      }
      echo "<TD><img src=\"$file\"><BR><center>$file</center></TD> ";
     
      $i++; 
   }

   closedir($hdir);
?>

</TABLE>

