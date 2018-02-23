#!/usr/bin/env bash

cd `dirname $0`
echo '清空所有plugins插件...'
rm -rf */plugins/
rm -rf libs/

#echo '清空gradle缓存...'
#rm -rf ~/.gradle/caches/modules-2/files-2.1/info.xiancloud.cloud

echo 'clean完毕...'
