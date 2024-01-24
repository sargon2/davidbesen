#!/bin/bash

cd "$(dirname "${BASH_SOURCE[0]}")"

cd ../live

gen_index () {
    pushd $1
    tree -H '.' -I index.html -o index.html
    popd
}

gen_index_no_recurse () {
    pushd $1
    tree -L 1 -H '.' -I index.html -o index.html
    popd
}

gen_index x/t
gen_index x/j
gen_index_no_recurse x
