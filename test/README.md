# test
集成测试和单元测试模块

### test/src
该包内定义了对第三方工具的测试，该包内的测试代码旨在实现对外部第三方（非xian）组件的单元测试，以确保第三方插件的稳定性引入。
当对三方依赖组件进行版本升级时，需要进行一些必要的单元测试，降低升级风险。

### test/ide_runtime_test
该包内定义了综合测试代码。请留意该包内的build.gradle脚本：
```groovy
dependencies {
    for (p in rootProject.subprojects) {
        if (p.path != project.path) {
            compile project(p.path)
        }
    }
    compile group: 'junit', name: 'junit', version: '4.12'
}
```
该降本将所以module插件都引入到该项目内作为依赖，这样子，ide_runtime_test内便可以编写对任何module插件的单元测试了。
