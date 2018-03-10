package info.xiancloud.apidoc.handler;

import info.xiancloud.plugin.Input;
import info.xiancloud.plugin.Input.Obj;
import info.xiancloud.plugin.LocalUnitsManager;
import info.xiancloud.plugin.Unit;
import info.xiancloud.plugin.distribution.GroupBean;
import info.xiancloud.plugin.distribution.GroupProxy;
import info.xiancloud.plugin.distribution.UnitBean;
import info.xiancloud.plugin.distribution.exception.GroupUndefinedException;
import info.xiancloud.plugin.distribution.loadbalance.GroupRouter;
import info.xiancloud.plugin.distribution.service_discovery.GroupDiscovery;
import info.xiancloud.plugin.distribution.service_discovery.UnitDiscovery;
import info.xiancloud.plugin.util.EnvUtil;
import info.xiancloud.plugin.util.LOG;
import info.xiancloud.plugin.util.StringUtil;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * unit接口文档创建Handler
 * 注意：本类的实现依赖服务注册，必须接入注册中心才可以
 *
 * @author yyq
 */
public class UnitMdBuilderHandler extends BaseMdBuilderHandler {

    /**
     * 筛选map集 只生成该集合中指定的接口
     * <p>
     * key-对应group名称
     * <p>
     * values-对应当前group的unit集合
     */
    private Map<String, List<String>> filters;

    // 文档名称
    private String docName;

    private String subDec;

    public UnitMdBuilderHandler() {

    }

    public UnitMdBuilderHandler(Map<String, List<String>> filters) {
        this.filters = filters;
    }

    public UnitMdBuilderHandler(String docName) {
        this.docName = docName;
    }

    public UnitMdBuilderHandler(String subDec, String docName, Map<String, List<String>> filters) {
        this(docName);
        this.subDec = subDec;
        this.filters = filters;
    }

    @Override
    public void build() {
        LOG.info("-----unit接口文档开始构建----");
        List<GroupBean> groupList = buildUnit(filters);
        if (groupList == null || groupList.isEmpty()) {
            LOG.info("-----unit接口没扫描到业务模块，退出构建");
            invokeCallback(null);
            return;
        }
        LOG.info(String.format("-----unit接口扫描到业务模块数量:%s", groupList.size()));
        try {
            LOG.info("----unit接口开始生成API文档-----");
            // FileWriter fw = new FileWriter(storePath);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Writer wout = new OutputStreamWriter(bos);
            BufferedWriter bw = new BufferedWriter(wout);
            bw.write(String.format("# %s", StringUtil.isEmpty(docName) ? EnvUtil.getShortEnvName() + "业务接口文档" : docName));
            // 自定义描述
            if (!StringUtil.isEmpty(subDec)) {
                bw.newLine();
                bw.write(subDec);
            }
            bw.newLine();
            for (GroupBean groupBean : groupList) {
                bw.write("<br/>\r\n");
                //todo it recommended to use a template to generate this MD fragment.
                bw.write("## " + String.format("%s\r\n", StringUtil.isEmpty(groupBean.getDescription()) ? groupBean.getName() : groupBean.getDescription()));
                bw.newLine();
                bw.write(" 接口列表\r\n");
                List<String> unitNames = groupBean.getUnitNames();
                for (int i = 1; i <= unitNames.size(); i++) {
                    String unitName = unitNames.get(i - 1);
                    UnitBean unitBean = UnitDiscovery.singleton.newestDefinition(Unit.fullName(groupBean.getName(), unitName));
                    if (!unitBean.getMeta().isPublic()) {
                        LOG.info(String.format(" ---api-doc-unit接口:%s/%s非公开访问的，跳过生成", groupBean.getName(),
                                unitBean.getName()));
                        continue;
                    }
                    LOG.info(String.format(" ---api-doc-unit接口开始生成:%s/%s", groupBean.getName(), unitBean.getName()));

                    Input io = unitBean.getInput();
                    bw.write(String.format("### /%s/%s", groupBean.getName(), unitBean.getName()));
                    bw.newLine();
                    bw.write(String.format(" * 接口描述: %s\r\n",
                            StringUtil.isEmpty(unitBean.getMeta().getDescription()) ? "暂无" : unitBean.getMeta().getDescription()));
                    bw.newLine();
                    bw.write(" * 调用方式: POST");
                    bw.newLine();
                    bw.write(" * 入参数据结构说明\r\n");
                    bw.newLine();
                    bw.write(" <table border='1' class='table table-bordered table-striped table-condensed'>");
                    bw.newLine();
                    bw.write("<tr><td>名称</td><td>数据类型</td><td>参数说明</td><td>必须</td></tr>");
                    bw.newLine();
                    // bw.write(" | 名称 | 数据类型 | 参数说明 | 是否是必须的 |\r\n");
                    // bw.write(" | ------ | -------- | -------: | :--------:
                    // |\r\n");
                    if (io != null) {
                        List<Obj> objList = io.getList();
                        for (Obj obj : objList) {
                            bw.write("<tr>");
                            bw.newLine();
                            bw.write(String.format("<td>%s</td><td>%s</td><td>%s</td><td>%s</td>", obj.getName(),
                                    obj.getClazz().getName(),
                                    StringUtil.isEmpty(obj.getDescription()) ? "暂无" : obj.getDescription(),
                                    obj.isRequired() ? "是" : "否"));
                            bw.newLine();
                            bw.write("</tr>");
                        }
                    }
                    bw.write("</table>\r\n");
                    bw.newLine();
                    bw.write(" * 返回数据格式\r\n");
                    bw.newLine();// `
                    bw.write(String.format("````json\r\n%s\r\n````",
                            unitBean.getMeta().getSuccessfulUnitResponse() == null ?
                                    "暂无" : unitBean.getMeta().getSuccessfulUnitResponse().toVoJSONString(true)));
                    bw.write("<br/><br/>\r\n");
                }
            }
            bw.flush();
            invokeCallback(bos.toByteArray());
            LOG.info("-----unit接口文档构建完成----");
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    /**
     * only return specified group baans
     */
    private List<GroupBean> buildUnit(final Map<String, List<String>> filters) {
        List<GroupBean> groups = buildUnit();
        if (groups != null && filters != null && !filters.isEmpty()) {
            Iterator<GroupBean> groupIterator = groups.iterator();
            while (groupIterator.hasNext()) {
                GroupBean groupBean = groupIterator.next();
                if (filters.containsKey(groupBean.getName())) {
                    List<String> unitNames = groupBean.getUnitNames();
                    // 不存在于生成列表中，移除
                    unitNames.removeIf(unitName -> !filters.get(groupBean.getName()).contains(unitName));
                } else {
                    groupIterator.remove();
                }
            }
        }
        return groups;
    }

    /**
     * build unit list
     */
    private List<GroupBean> buildUnit() {
        List<GroupBean> groupList = new ArrayList<>();
        if (EnvUtil.isIDE()) {
            LocalUnitsManager.unitMap(unitMap -> {
                unitMap.forEach((groupName, unitList) ->
                        groupList.add(GroupProxy.create(LocalUnitsManager.getGroupByName(groupName))));
            });
        } else {// server runtime environment
            for (String groupName : GroupDiscovery.singleton.queryForNames()) {
                try {
                    groupList.add(GroupRouter.singleton.newestDefinition(groupName));
                } catch (GroupUndefinedException e) {
                    LOG.info("group " + groupName + "'s definition does not exist, ignored for api doc.");
                }
            }
            LOG.info(String.format("api-doc接口文档构建过程中扫描到[%s]个group", groupList.size()));
        }
        return groupList;
    }

    public Map<String, List<String>> getFilters() {
        return filters;
    }

    public void setFilters(Map<String, List<String>> filters) {
        this.filters = filters;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getSubDec() {
        return subDec;
    }

    public void setSubDec(String subDec) {
        this.subDec = subDec;
    }

}
