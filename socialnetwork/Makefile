.PHONY: all

all: network.svg index.html

network.svg: network.dot
	dot -Tsvg network.dot -o network.svg

index.html: network.dot
	echo "<img style=\"width: 2000px;\" src=\"network.svg\"/>" > index.html
	echo "<br />" >> index.html
	echo -n "<a href=\"https://dreampuf.github.io/GraphvizOnline/#" >> index.html
	cat network.dot | jq -R -r -s @uri | tr -d '\r' | tr -d '\n' >> index.html
	echo -n "\">edit this graph</a>" >> index.html
