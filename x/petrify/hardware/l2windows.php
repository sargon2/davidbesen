<?
	require_once('../header.html');
	$loc = "hardware l2 windows";
	require_once('../navbar.php');
	print '<DIV ALIGN=RIGHT>---------------[ l2 windows</DIV><hr size="0">';
?>
Windows 2000 runs pretty well on the L2, assuming you're competent enough to download drivers from Toshiba.  <br>
The one odd thing is that, due to some form of bug or another in Windows's ACPI.SYS, the fan never comes on, and the processor underclocks itself to compensate.<br>
In order to work around this, I downloaded Microsoft's <a href="http://www.microsoft.com/HWDEV/acpihct.htm">ACPIView</a> utility, which allows me to make calls directly to the ACPI symbol table.  I call the _ON_ function, and the fan turns on.<br><br>
In order to install 2000 in the first place, I had to boot into the Windows that was installed when I got it, run Toshiba's bios utility, set the boot order to include floppy, and boot off of a USB floppy.<br>
No matter how hard I tried, I couldn't get it to boot off of a cdrom.  Incompatible hardware, I guess.<br>
<?
	require_once('../footer.html');
?>
