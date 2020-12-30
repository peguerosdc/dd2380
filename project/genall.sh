#!/bin/bash
for n in {2..4} ; do
    for prefix in mega{,2,3} ultramega{1,2,3} ; do
        time ./plain.py ${n} train-val/${prefix}.{train,val} | tee train-val/${prefix}.e${n}_res &
        time ./fancy.py ${n} train-val/${prefix}.{train,val} | tee train-val/${prefix}.c${n}_res
    done
done
