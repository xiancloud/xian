### 懒加载的gateway规则引擎——API编排实现
RuleControllerRouter.java类，懒加载的规则引擎
#### loadRules
懒加载的mappingRuleController方法。
细心的你应该会发现偶尔在日志系统里面可以看到如下加载rule的日志，eg.
```
INFO RuleControllers found: [class info.xiancloud.rules.test.uriParamTestRule.v1_0]
```
但是纳闷的是这个日志跟咱们业务调用链日志八竿子打不着关系。这是由于，我们在解析URI时会先匹配规则引擎，而当网关应用是初次启动时，会先加载出所
有预定义的规则列表，而这个日志则是打印出所有加载出的规则列表的。下次再有业务请求过来时，则直接使用内存中已经加载的规则进行快速匹配，匹配不上
则再立刻匹配unit了。