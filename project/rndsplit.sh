#!/bin/bash

if (( $# != 4 )) ; then
    echo "Usage: ${0} [n] [infile:m] [out1:n] [out2:m-n]" 1>&2
    exit 1
fi

n=${1}
infile=${2}
out1=${3}
out2=${4}

tmp=$(mktemp)
trap "rm $tmp" EXIT

./tok.py ${infile} | shuf > ${tmp}

head -n ${n} ${tmp} > ${out1}
tail -n +$((n+1)) ${tmp} > ${out2}
