package info.xiancloud.apidoc.unit.md;

import info.xiancloud.apidoc.ApiBuilder;
import info.xiancloud.apidoc.ApidocGroup;
import info.xiancloud.apidoc.handler.UnitMdBuilderHandler;
import info.xiancloud.apidoc.handler.filter.IUnitFilter;
import info.xiancloud.core.*;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.LOG;

import java.io.ByteArrayOutputStream;

/**
 * super class for api doc builder unit.
 *
 * @author happyyangyuan
 */
public abstract class AbstractMdApidocUnit implements Unit {

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setDataOnly(true);
    }

    @Override
    public Group getGroup() {
        return ApidocGroup.singleton;
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        String doc = specifyBuild(msg.getString("docDescription"), msg.getString("docName"), getFilter(msg));
        handler.handle(UnitResponse.createSuccess(doc));
    }

    @Override
    public Input getInput() {
        return Input.create().add("docName", String.class, "api doc name", REQUIRED)
                .add("docDescription", String.class, "docDescription", NOT_REQUIRED)
                .addAll(otherInput());
    }

    protected abstract Input otherInput();

    /**
     * @return the filter to unit filter. the filter is a unit full name list.
     */
    protected abstract IUnitFilter getFilter(UnitRequest request);

    /**
     * 返回生成MD的文件字符串
     */
    private static String specifyBuild(String description, String docName, IUnitFilter filter) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ApiBuilder.build(new UnitMdBuilderHandler(description, docName, filter).callback(data -> {
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
