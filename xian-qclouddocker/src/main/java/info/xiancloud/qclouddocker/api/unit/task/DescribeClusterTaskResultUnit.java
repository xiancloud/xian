package info.xiancloud.qclouddocker.api.unit.task;

import info.xiancloud.core.Input;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.qclouddocker.api.unit.QCloudBaseUnit;

/**
 * 查询异步任务结果例接口
 *
 * @author yyq
 */
public class DescribeClusterTaskResultUnit extends QCloudBaseUnit {
    @Override
    public String getName() {
        return "describeClusterTaskResult";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("查询异步任务结果");
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("requestId", int.class, "异步任务ID", REQUIRED);
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        return super.execute(msg);
    }


    @Override
    public String getAction() {
        return "DescribeClusterTaskResult";
    }

    @Override
    public String getAPIHost() {
        return "ccs.api.qcloud.com";
    }

}
