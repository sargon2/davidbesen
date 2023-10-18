<?
	# This is the actual code of the page.
	# The template is "nums.phpt".

	# Notice that the name of the template is arbitrary;
	# it is simply nums.phpt because that's what's in the include.

	# You could also include this file at the top of the template
	# and name that .php and this .code, if you don't like this 
	# organization.

	for($i=0;$i<10;$i++) {
		$nums[] = $i;
		$squares[$i]['num'] = $i;
		$squares[$i]['square'] = $i * $i;
	}

require("nums.phpt"); ?>
