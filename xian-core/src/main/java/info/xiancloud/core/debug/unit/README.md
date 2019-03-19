### packages to scan
xian框架classpath扫描机制：
我们在程序刚加载时，通过扫描classpath内的unit、group等方式来获取服务列表的。
#### /system/GetPackagesToScan接口
该接口用于debug，它能获取所有节点的当前配置的包扫描配置
eg.
```json
{
    "code": "SUCCESS",
    "data": [
        [
            "info.xiancloud",
            "com.cedarhd"
        ],
        [
            "info.xiancloud",
            "com.cedarhd"
        ],
        [
            "info.xiancloud",
            "com.cedarhd"
        ],
        [
            "info.xiancloud",
            "com.cedarhd"
        ]
    ],
    "msgId": "xian_runtime_dev---apigateway---9@apigateway-7cf495c87-rv4d8_36"
}
```
