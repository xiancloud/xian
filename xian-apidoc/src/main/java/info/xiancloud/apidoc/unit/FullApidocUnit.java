package info.xiancloud.apidoc.unit;

import info.xiancloud.apidoc.ApiBuilder;
import info.xiancloud.apidoc.ApidocGroup;
import info.xiancloud.apidoc.handler.OAuth20BuildHandler;
import info.xiancloud.apidoc.handler.UnitBuildHandler;
import info.xiancloud.plugin.Group;
import info.xiancloud.plugin.Input;
import info.xiancloud.plugin.Unit;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.util.LOG;


/**
 * generate full api doc
 *
 * @author happyyangyuan
 */
public class FullApidocUnit implements Unit {
    @Override
    public Input getInput() {
        return null;
    }

    @Override
    public Group getGroup() {
        return ApidocGroup.singleton;
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        return UnitResponse.success(buildAll());
    }


    private String buildAll() {
        StringBuffer doc = new StringBuffer();
        try {
            ApiBuilder.build(new UnitBuildHandler().callback(data -> {
                if (data != null && data.length > 0) {
                    LOG.info("api-doc接口文档unit文档大小 : " + data.length);
                    doc.append(new String(data));
                } else {
                    LOG.info("api-doc接口文档unit暂无扫描到相关数据");
                }
            }), new OAuth20BuildHandler().callback(data -> {
                if (data != null && data.length > 0) {
                    LOG.info("api-doc接口文档oauth20发布成功");
                    doc.append(new String(data));
                } else {
                    LOG.info("api-doc接口文档auth20暂无扫描到相关数据");
                }
            }));
        } catch (Exception e) {
            LOG.error("api-doc接口文档启动生成出错", e);
        }
        return doc.toString();
    }
}
