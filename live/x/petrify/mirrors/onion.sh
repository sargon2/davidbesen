#!/bin/bash
cd /home/sargon/public_html/mirrors/onion
rm -f www.theonion.com/index.html
wget -nc -r -l5 -k -H -t0 -T20 -Dmanuel.theonion.com,graphics.theonion.com,www.theonion.com http://www.theonion.com
# wget -nc -r -l0 -k -H -t0 -T20 -Dtheonion.com http://www.theonion.com
