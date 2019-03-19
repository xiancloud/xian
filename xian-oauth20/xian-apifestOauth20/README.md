# xian-apifestOauth20
基于开源项目apifest https://github.com/apifest/apifest-oauth20 自研的oauth2.0插件

### oauth2.0接口定义
我们是通过unit来暴露oauth2.0接口地址出去的。  
由于框架内置的扫描器只会去扫描classpath内`info.xiancloud`包下的类，所以unit和group接口的实现类必须都放在`info.xiancloud`包下，
否则扫描不到，会报错`UNIT_NOT_FOUND`。



### 认证信息持久化
目前我们暂时使用的redis做oauth认证数据的持久化，所以redis数据非常重要不能随意清理缓存。如果使用的k8s做redis部署，必须使用持久卷存储保证数据不丢失。
