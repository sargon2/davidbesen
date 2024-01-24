<?
	require_once('../header.html');
	$loc = "hardware tv";
	require_once('../navbar.php');
	print '<DIV ALIGN=RIGHT>----------------[ tv server</DIV><hr size="0">';
?>
The purpose of the TV server is threefold. One, to act as a normal TV, two, to act as a TIVO, i.e. record (and even pause) tv shows, and three, to broadcast TV signal to my laptops over 802.11b.<br>
In order to accomplish this, I need a rather fast machine, as well as a rather nice TV card.  A big hard drive won't hurt either.<br><br>
Here's what I'm currently planning on buying (prices as of 1/04/01):<br>
<table width=500>
<tr><td class=bigger>Item<hr size=0></td><td class=bigger>Price<hr size=0></td><td class=bigger>Where<hr size=0></td></tr>
<tr><td>KDS Rad-5 (15" 1024x768@32bpp lcd)</td><td>$321.95</td><td>buy.com</td></tr>
<tr><td>256MB of Micron pc2100 DDR ram</td><td>$65</td><td>directron.com</td></tr>
<tr><td>Matrox Marvel G450 eTV</td><td>$189</td><td>directron.com</td></tr>
<tr><td>cpu fan</td><td>$18</td><td>directron.com</td></tr>
<tr><td>ATX case with 300W power</td><td>$29</td><td>directron.com</td></tr>
<tr><td>IBM 60GB 7200rpm hdd</td><td>$111</td><td>newegg.com</td></tr>
<tr><td>Athlon XP 1900</td><td>$236</td><td>newegg.com</td></tr>
<tr><td>Toshiba 16x DVD drive</td><td>$65</td><td>newegg.com</td></tr>
<tr><td>Asus A7V266-E</td><td>$161</td><td>newegg.com</td></tr>
<tr><td>3Com 3c905</td><td>$32</td><td>newegg.com</td></tr>
<tr><td>X10 Mouse remote</td><td>$49.99</td><td>x10.com</td></tr>
<tr><td class=bigger><hr size=0>Total</td><td class=bigger><hr size=0>$1277.94</td></tr>
</table>
<br>
I guess with that setup, the machine will be far more than a TV server.  But it should perform its intended purpose spectacularly.<br>
<br>
So everyone asks me, "sargon, while you're sitting out on the front porch on your 802.11b watching TV on your libretto, how will you change channels?"<br>
The answer is simple.  I will write a small application that listens on a socket, authenticates, then interfaces with the WinTV software.  Then I'll write a 'remote' application for the client side that connects to that, authenticates, and tells it what channel to go to, along with any other settings such as volume.<br><br>
Update: I have the TV server built, and it STILL won't serve tv.  Grr.<br><br>
<?
	require_once('../footer.html');
?>
