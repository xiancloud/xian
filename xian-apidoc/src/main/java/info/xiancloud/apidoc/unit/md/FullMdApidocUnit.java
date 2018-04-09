package info.xiancloud.apidoc.unit.md;

import info.xiancloud.apidoc.ApiBuilder;
import info.xiancloud.apidoc.ApidocGroup;
import info.xiancloud.apidoc.handler.OAuth20MdBuilderHandler;
import info.xiancloud.apidoc.handler.UnitMdBuilderHandler;
import info.xiancloud.core.Group;
import info.xiancloud.core.Handler;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.LOG;


/**
 * generate full api doc
 *
 * @author happyyangyuan
 */
public class FullMdApidocUnit implements Unit {

    @Override
    public String getName() {
        return "fullMd";
    }

    @Override
    public Input getInput() {
        return null;
    }

    @Override
    public Group getGroup() {
        return ApidocGroup.singleton;
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        handler.handle(UnitResponse.createSuccess(buildAll()));
    }


    private String buildAll() {
        StringBuffer doc = new StringBuffer();
        try {
            ApiBuilder.build(new UnitMdBuilderHandler().callback(data -> {
                if (data != null && data.length > 0) {
                    LOG.info("api-doc接口文档unit文档大小 : " + data.length);
                    doc.append(new String(data));
                } else {
                    LOG.info("api-doc接口文档unit暂无扫描到相关数据");
                }
            }), new OAuth20MdBuilderHandler().callback(data -> {
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
