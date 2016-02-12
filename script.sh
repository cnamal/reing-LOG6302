#!/bin/bash

python script.py

for file in graphs/*.dot
do
    dot $file -Tjpeg -o ${file%.dot}.jpeg
done


