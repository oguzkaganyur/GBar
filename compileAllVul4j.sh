#!/bin/bash

proj=VUL4J
for bug in $(seq 1 79;)
do
	echo ${proj}_${bug}
	python /Users/turgutt/vul4j/vul4j/main.py checkout --id ${proj}-${bug} -d /Users/turgutt/GBar/D4J/projects/${proj}_${bug}
	python /Users/turgutt/vul4j/vul4j/main.py compile -d /Users/turgutt/GBar/D4J/projects/${proj}_${bug}
done
