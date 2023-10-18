<?

	function HashToXml($in) {
		return '<?xml version="1.0"?>' . "\n" . HashToXml_r($in, 0);
	}

	function HashToXml_r ($in, $i = 0) { 
		foreach($in as $k=>$v) {
			$ret .= str_repeat("    ", $i);
			$ret .= "<" . $k . ">";
			if(is_array($v)) {
				$ret .= "\n" . HashToXml_r($v, $i + 1);
				$ret .= str_repeat("    ", $i);
			} else {
				$ret .= $v;
			}
			$ret .= "</" . $k . ">\n";
		}
		return $ret;
	}
?>
