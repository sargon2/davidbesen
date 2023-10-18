#!/bin/bash

cd "$(dirname "${BASH_SOURCE[0]}")"

cd ..

gen_index () {
    pushd $1
    tree -H '.' -I index.html -o index.html
    popd
}

gen_index x/t
gen_index x/j
