package info.xiancloud.apidoc.handler;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import info.xiancloud.apidoc.handler.filter.IUnitFilter;
import info.xiancloud.apidoc.handler.filter.NothingFilter;
import info.xiancloud.plugin.Input;
import info.xiancloud.plugin.Input.Obj;
import info.xiancloud.plugin.distribution.GroupProxy;
import info.xiancloud.plugin.distribution.UnitProxy;
import info.xiancloud.plugin.distribution.service_discovery.GroupDiscovery;
import info.xiancloud.plugin.distribution.service_discovery.UnitDiscovery;
import info.xiancloud.plugin.util.EnvUtil;
import info.xiancloud.plugin.util.LOG;
import info.xiancloud.plugin.util.StringUtil;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.List;

/**
 * unit接口文档创建Handler
 * 注意：本类的实现依赖服务注册，必须接入注册中心才可以
 *
 * @author yyq
 */
public class UnitMdBuilderHandler extends BaseMdBuilderHandler {

    // 文档名称
    private String docName;

    private String subDec;

    private IUnitFilter filter = new NothingFilter();

    public UnitMdBuilderHandler() {
    }

    public UnitMdBuilderHandler(String docName) {
        this.docName = docName;
    }

    public UnitMdBuilderHandler(String subDec, String docName, IUnitFilter filter) {
        this(docName);
        this.subDec = subDec;
        this.filter = filter;
    }

    @Override
    public void build() {
        LOG.info("-----unit接口文档开始构建----");
        Multimap<String, UnitProxy> unitMultimap = filter.filter(buildUnits());
        if (unitMultimap == null || unitMultimap.isEmpty()) {
            LOG.info("-----unit接口没扫描到业务模块，退出构建");
            invokeCallback(null);
            return;
        }
        LOG.info(String.format("-----unit接口扫描到业务模块数量:%s", unitMultimap.size()));
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
            for (String groupName : unitMultimap.keySet()) {
                GroupProxy groupProxy = GroupDiscovery.singleton.newestDefinition(groupName);
                //todo it recommended to use a template to generate this MD fragment.
                bw.write("## " + String.format("%s\r\n", StringUtil.isEmpty(groupProxy.getDescription()) ? groupProxy.getName() : groupProxy.getDescription()));
                bw.newLine();
                bw.write(" 接口列表\r\n");
                Collection<UnitProxy> unitProxies = unitMultimap.get(groupName);
                for (UnitProxy unitBean : unitProxies) {
                    if (!unitBean.getMeta().isPublic()) {
                        LOG.info(String.format(" ---api-doc-unit接口:%s/%s非公开访问的，跳过生成", groupProxy.getName(),
                                unitBean.getName()));
                        continue;
                    }
                    LOG.info(String.format(" ---api-doc-unit接口开始生成:%s/%s", groupProxy.getName(), unitBean.getName()));

                    Input io = unitBean.getInput();
                    bw.write(String.format("### /%s/%s", groupProxy.getName(), unitBean.getName()));
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
                    bw.newLine();
                    bw.write(String.format("````json\r\n%s\r\n````\r\n",
                            unitBean.getMeta().getSuccessfulUnitResponse() == null ?
                                    "暂无" : unitBean.getMeta().getSuccessfulUnitResponse().toVoJSONString(true)));
                    bw.write("&nbsp;&nbsp;\r\n");
                }
            }
            bw.write("&nbsp;&nbsp;\r\n");
            bw.flush();
            invokeCallback(bos.toByteArray());
            LOG.info("-----unit接口文档构建完成----");
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    private Multimap<String, UnitProxy> buildUnits() {
        Multimap<String, UnitProxy> groupedUnits = ArrayListMultimap.create();
        List<String> unitFullNames = UnitDiscovery.singleton.queryForNames();
        for (String unitFullName : unitFullNames) {
            UnitProxy unitProxy = UnitDiscovery.singleton.newestDefinition(unitFullName);
            if (unitProxy != null)
                groupedUnits.put(unitProxy.getGroup().getName(), unitProxy);
        }
        return groupedUnits;
    }

    public void setFilter(IUnitFilter filter) {
        this.filter = filter;
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
