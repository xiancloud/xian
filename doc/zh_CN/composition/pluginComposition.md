# 数据库DAO层插件组合部署
我们利用xian框架插件与插件之间的解耦性和插件可独立部署特性，可以实现业务与数据分离的部署架构

## 数据库DAO层服务与业务微服务分离部署
### 业务微服务与DAO数据层微服务节点数m:n
![业务微服务与DAO数据层微服务节点数m:n部署结构](http://processon.com/chart_image/5cd1b6dce4b06bcc139a55e6.png)

## 数据DAO层插件直接部署至业务微服务内
这种插件组合方式直接对应的就是如下1：1结构
### 业务微服务与DAO数据层微服务1:1部署
![业务微服务与DAO数据层微服务1:1部署](http://processon.com/chart_image/5cd1b97ae4b059e20a16a790.png)
## 快速开发模式——DAO层直接面向httpAPI接口提供服务
![DAO层直接面向httpAPI接口提供服务](http://processon.com/chart_image/5a8f9e94e4b0615ac05c8452.png)
