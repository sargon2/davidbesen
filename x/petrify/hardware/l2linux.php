<?
	require_once('../header.html');
	$loc = "hardware l2 linux";
	require_once('../navbar.php');
	print '<DIV ALIGN=RIGHT>-----------------[ l2 linux</DIV><hr size="0">';
?>
My prime achievement in running Linux on the L2 is the 1280x600 vesa framebuffer console.  I don't even run XWindows because the console is so nice.<br>
In order to get this, I found a Windows utility to list the VESA modes your hardware supports.  1280x600@8bpp is VESA mode 0x204, which translates to kernel mode 1028(0x404).<br>  I just popped that number into VGA= in lilo.conf, and it works perfectly.<br><br>
I've been attempting to get one of my 802.11b wireless cards to work in linux.  So far, I've hacked the kernel to force IRQ assignment to my topic95 CardBus, which allowed the wvlan_cs drivers to see the card, but I still can't actually get it to work.<br><br>
The other thing I'd like to get working is power management.  The (practically nonexistant) L2 bios does not support APM, and unfortunately the Linux kernel does not currently support ACPI to the point that it's usable.
<?
	require_once('../footer.html');
?>
