package info.xiancloud.apidoc;

import info.xiancloud.apidoc.handler.BuildCallback;
import info.xiancloud.apidoc.handler.OAuth20BuildHandler;
import info.xiancloud.apidoc.handler.UnitBuildHandler;
import info.xiancloud.plugin.Group;
import info.xiancloud.plugin.Input;
import info.xiancloud.plugin.Unit;
import info.xiancloud.plugin.UnitMeta;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.util.LOG;
import info.xiancloud.plugin.util.StringUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * api doc generator unit. the auto generation is deprecated for now.
 *
 * @author yyq
 */
public class ApidocBuilderUnit implements Unit/*, IStartService*/ {

    /**
     * not now
     */
    private boolean buildAll() {
        try {
            APIBuild.build(new UnitBuildHandler().callback(new BuildCallback() {
                @Override
                public void call(byte[] data) {
                    try {
                        if (data != null && data.length > 0) {
                            LOG.info("api-doc接口文档unit文档大小 : " + data.length);
                            LOG.info("api-doc接口文档unit发布成功");
                        } else {
                            LOG.info("api-doc接口文档unit暂无扫描到相关数据");
                        }
                    } catch (Exception e) {
                        LOG.error("api-doc接口文档生成unit接口文档出错", e);
                    }
                }
            }), new OAuth20BuildHandler().callback(new BuildCallback() {
                public void call(byte[] data) {
                    try {
                        if (data != null && data.length > 0) {
                            InputStream bis = new ByteArrayInputStream(data);
                            LOG.info("api-doc接口文档oauth20发布成功");
                        } else {
                            LOG.info("api-doc接口文档auth20暂无扫描到相关数据");
                        }
                    } catch (Exception e) {
                        LOG.error("api-doc接口文档生成OAuth20接口文档出错", e);
                    }
                }
            }));

        } catch (Exception e) {
            LOG.error("api-doc接口文档启动生成出错", e);
        }
        return true;
    }

    @Override
    public String getName() {
        return "apiDocUnit";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create("apidoc生成").setDataOnly(true);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("docName", String.class, "文档名称")
                .add("unitFilter", String.class, "要生成的unit列表,格式-\"group.unit,group.unit,.....\"")
                .add("subDec", String.class, "文档自定义描述,md格式")
                .add("path", String.class, "文件存放路径(相对路径)-暂时不支持");
    }

    /**
     * 返回生成MD的文件字符串
     */
    private String specifyBuild(String subDec, String docName, Map<String, List<String>> filters, String path) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        APIBuild.build(new UnitBuildHandler(subDec, docName, filters).callback(new BuildCallback() {
            public void call(byte[] data) {
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
            }
        }));
        return bos.toString();
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        String subDec = msg.getString("subDec");
        String docName = msg.getString("docName");
        String unitFilter = msg.getString("unitFilter");
        String path = msg.getString("path");
        if (!StringUtil.isEmpty(docName) && !StringUtil.isEmpty(unitFilter)) {
            Map<String, List<String>> filterMap = null;
            if (!StringUtil.isEmpty(unitFilter)) {
                filterMap = new HashMap<>();
                String[] fullNameArr = unitFilter.split(",");
                for (String fullName : fullNameArr) {
                    String[] sb = fullName.split("\\.");
                    String groupName = sb[0];
                    String unitName = sb[1];
                    List<String> unitList = filterMap.computeIfAbsent(groupName, k -> new ArrayList<>());
                    unitList.add(unitName);
                }
            }
            String md = specifyBuild(subDec, docName, filterMap, path);
            return UnitResponse.success(md);
        } else {
            buildAll();
        }
        return UnitResponse.success();
    }

    @Override
    public Group getGroup() {
        return APIBuildServiceGroup.singleton;
    }

}
