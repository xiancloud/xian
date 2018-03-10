package info.xiancloud.apidoc.unit;

import info.xiancloud.apidoc.ApiBuilder;
import info.xiancloud.apidoc.ApidocGroup;
import info.xiancloud.apidoc.handler.UnitBuildHandler;
import info.xiancloud.plugin.Group;
import info.xiancloud.plugin.Input;
import info.xiancloud.plugin.Unit;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.util.LOG;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

/**
 * super class for api doc builder unit.
 *
 * @author happyyangyuan
 */
public abstract class AbstractApidocUnit implements Unit {

    @Override
    public Group getGroup() {
        return ApidocGroup.singleton;
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        String doc = specifyBuild(msg.getString("docDescription"), msg.getString("docName"), filter(msg));
        return UnitResponse.success(doc);
    }

    @Override
    public Input getInput() {
        return Input.create().add("docName", String.class, "api doc name", REQUIRED)
                .add("docDescription", String.class, "docDescription", NOT_REQUIRED)
                .addAll(otherInput());
    }

    protected abstract Input otherInput();

    /**
     * @return the filter to unit filter. the filter map's key is group name and value is unit name list.
     */
    protected abstract Map<String, List<String>> filter(UnitRequest request);

    /**
     * 返回生成MD的文件字符串
     */
    private static String specifyBuild(String description, String docName, Map<String, List<String>> filters) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ApiBuilder.build(new UnitBuildHandler(description, docName, filters).callback(data -> {
            try {
                bos.write(data);
                if (data.length > 0) {
                    LOG.info("api-doc接口文档unit文档大小 : " + data.length);
                    LOG.info("api-doc接口文档unit发布成功");
                } else {
                    LOG.info("api-doc接口文档unit暂无扫描到相关数据");
                }
            } catch (Exception e) {
                LOG.error("api-doc接口文档生成unit接口文档出错", e);
            }
        }));
        return bos.toString();
    }

}
