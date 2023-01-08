#!/bin/bash

proj=VUL4J
for bug in $(seq 1 79)
do
	echo ${proj}_${bug}
	sh runGZoltar.sh ${proj} ${bug}
done
