.PHONY: all

all: network.svg index.html

network.svg: network.dot
	dot -Tsvg network.dot -o network.svg

index.html: network.dot index.html.in
	URL=`cat network.dot | jq -R -r -s @uri | tr -d '\r' | tr -d '\n'` envsubst < index.html.in > index.html

.PHONY: clean
clean:
	rm -f network.svg index.html
