#!/usr/bin/env bash

cd `dirname $0`
rm -rf plugins/*
gradle copyPlugins