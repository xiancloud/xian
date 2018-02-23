#!/usr/bin/env bash
cd `dirname $0`

./cleanAll.sh

gradle_build="gradle --parallel "
for application in *; do
    if [[ -d ${application} ]]; then
        gradle_build="${gradle_build}${application}:copyPlugins "
    fi
done
echo "构建命令：${gradle_build}"
${gradle_build}

#如果编译失败，那么不执行task :xian_runtime:extractLib
if [ "$?" -ne 0 ] ; then echo "<<<<<<<构建失败，请求检查是否存在编译错误>>>>>>>"; exit 1; fi
gradle :xian_runtime:extractLib
