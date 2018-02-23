#!/usr/bin/env bash

##environment variables use 'export key=value'


#tencent yun docker getGroup
export dockerServiceRegistryUrl=xxx.xxx.com/xxx/
export dockerServiceNonproductionClusterId=cls-xxxx
export dockerServiceProductionClusterId=cls-xxxx
export dockerServiceSecretId=xxx
export dockerServiceSecretKey=xxx
export dockerServiceSignatureMethod=xxx
export dockerServiceRegion=gz/bj/sh


#api gateway plugin
export api_gateway_port=12345
export api_gateway_white_ip_list=127.0.0.1,::1,0:0:0:0:0:0:0:1

#gelf client plugin
export production_gelfInputLanUrl=udp:xxx.abc.com
export gelfInputPort=12201
export production_gelfInputPort=12201
export gelfInputInternetUrl=udp:gelf.abc.com
export gelfInputLanUrl=udp:gelf.abc.com

#xian-unitmonitor plugin
export monitorUnitList=xxxGroup.yyyUnit,zzzGroup.cccUnit

#xian-grafana plugin
export grafana_api_token=xxx
export grafana_http_api_dashboards_db_url=http://xxx.xxx.com/api/dashboards/db

#xian-monitor plugin
export lan_falcon_transfer_url=http://xxx.xxx.com:1988/v1/push
export internet_falcon_transfer_url=http://xxx.xxx.com:1988/v1/push

#rabbitmq plugin
export rabbitLanHost=xxx.xxx.com
export rabbitInternetHost=xxx.xxx.com
export rabbitPort=5672
export rabbitUserName=xxxx
export rabbitPwd=xxxx

#configuration for zookeeper plugins
export production_zookeeperConnectionStringLan=xxx.xxx.com:18129
export zookeeperConnectionStringLan=xxx.xxx.com:18129
export zookeeperConnectionStringInternet=xxx.xxx.com:18129
export LAN_REFERENCE_HOST=xxx.xxx.com