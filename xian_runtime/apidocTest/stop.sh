#!/bin/bash

cd `dirname $0`
pwd

kill -15 `cat pid`
