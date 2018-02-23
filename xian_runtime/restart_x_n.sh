#!/usr/bin/env bash

cd `dirname $0`

for i in `seq 1 1`;
do
    #echo ${i}
    ./communication/start.sh
	sleep 5
    ./communication/stop.sh
#    ./startPayAll.sh
#    sleep 10
#    ./stopPayAll.sh
#    sleep 5
done


