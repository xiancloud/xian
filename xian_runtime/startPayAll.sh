#!/usr/bin/env bash

cd `dirname $0`
for application in *; do
    if [[ -d ${application} ]]; then
        ./${application}/start.sh
    fi
done

echo "列出java进程: "
jps -m


