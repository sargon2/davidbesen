<?
	header("Cache-Control: no-cache, must-revalidate"); // HTTP/1.1
	header("Expires: Mon, 26 Jul 1997 05:00:00 GMT"); // Date in the past
?>
<head>
	<script src="http://live.3dtracking.net/js/general.js" type="text/javascript"></script>
	<script src="http://maps.google.com/maps?file=api&v=2.84&key=ABQIAAAA_l1lZ8gKIqZYjj_O7lM_3RTn_4Ce_IcuICuKBLRRWl-bjNNdsBRwf_SJVftdmNpz2Vr1Po_Fj2KXNQ" type="text/javascript"></script>
	<style type="text/css">
		v\:* {
		behavior:url(#default#VML);
		}
	</style>
	<script type="text/javascript">
		var map;
		var kml;

		function randomizeUrl(sUrl) {
			var sRandom = Math.floor(Math.random() * 10000);
			return sUrl + "?a=" + sRandom;
		}


		function onLoad() {
			if (GBrowserIsCompatible()) {
				map = new GMap2(document.getElementById("map"));
				map.setCenter(new GLatLng(0,0), 0);
				kml = new GGeoXml("http://free.3dtracking.net/live.kmz?guid=6af102b0-a5fd-4815-9a9c-2ebfdee219d2", function() {
						kml.gotoDefaultViewport(map)
						map.setZoom(16);
						var givenmaptypes = map.getMapTypes();
						map.setMapType(givenmaptypes[2]);
					});
				map.addControl(new GLargeMapControl());
				map.addControl(new GMapTypeControl());
				map.enableScrollWheelZoom();
				map.addOverlay(kml);
			}

			InitializeTimer(Reload, 60);
		}

		function Reload() {
			if (GBrowserIsCompatible()) {
				var url = RandomizeUrl("http://free.3dtracking.net/live.kmz?guid=6af102b0-a5fd-4815-9a9c-2ebfdee219d2");
				kml = new GGeoXml(url, Fun(url));

				//add the new overlay
				map.addOverlay(kml);
			}

			// do another loop around
			ResetTimer();
		}

		function Fun(url) {
			//remove the previous kml.
			map.removeOverlay(kml);
		}
	</script>
</head>
<body onload="onLoad()">
	If my GPS tracker is turned on, here's where I am:<br /><br />
	<div id="map" style="width: 800px; height: 600px; float:left; border: 1px solid black;"></div>
	<br clear="all" />
	<br />
</body> 
