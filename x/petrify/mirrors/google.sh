#!/bin/bash
rm -rf google/
mkdir google
cd google
wget -r -l2 -k -H -t0 -T20 -Dgoogle.com http://www.google.com
