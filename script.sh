#!/bin/bash

if [ -n "$1" ] && [ $1 == "UML" ]
then
    python script.py
fi

for file in graphs/*.dot
do
    dot $file -Tjpeg -o ${file%.dot}.jpeg
done
