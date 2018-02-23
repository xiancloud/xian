#!/usr/bin/env bash

cd `dirname $0`

for application in *; do
    if [[ -d ${application} ]]; then
        ./${application}/stop.sh
    fi
done