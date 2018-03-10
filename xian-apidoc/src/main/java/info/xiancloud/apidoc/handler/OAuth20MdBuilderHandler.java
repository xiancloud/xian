package info.xiancloud.apidoc.handler;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.List;

import info.xiancloud.plugin.apidoc.annotation.DocOAuth20;
import info.xiancloud.plugin.apidoc.annotation.DocOAuth20Sub;
import info.xiancloud.plugin.apidoc.annotation.DocOAuth20SubIn;
import info.xiancloud.plugin.util.LOG;
import info.xiancloud.plugin.util.Reflection;

/**
 * OAuth20 is inner interface, we do not need to generate doc for them.
 *
 * @author yyq
 */
public class OAuth20MdBuilderHandler extends BaseMdBuilderHandler {

    @Override
    public void build() {
        LOG.info("-----Oauth20接口文档构建开始-----");
        LOG.info("-----Auth20接口开始扫描-----");
        List<Class> list = Reflection.getWithAnnotatedClass(DocOAuth20.class, "com.apifest");
        if (list == null || list.isEmpty()) {
            LOG.info("----Auth20接口扫描完成,暂无相关信息,构建退出---");
            invokeCallback(null);
            return;
        }
        System.out.println(String.format("----Auth20接口扫描完成%s---", list.size()));
        try {
            LOG.info("----Auth20接口文档开始生成---");
            // FileWriter fw = new FileWriter(storePath);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Writer wout = new OutputStreamWriter(bos);
            BufferedWriter bw = new BufferedWriter(wout);
            bw.write("# OAuth20接口文档\r\n");
            for (Class oauthApi : list) {
                // 获取所有方法
                Method[] methods = oauthApi.getDeclaredMethods();
                for (Method method : methods) {
                    DocOAuth20Sub sub = method.getAnnotation(DocOAuth20Sub.class);
                    // 该方法属于接口
                    if (sub != null) {
                        String name = sub.name();
                        String dec = sub.dec();
                        String url = sub.url();
                        String httpMethod = sub.method();

                        bw.write(String.format("## 接口 %s", name));
                        bw.newLine();
                        bw.write(String.format(" * 接口描述%s\r\n", dec));
                        bw.write(String.format(" * 接口路径%s\r\n", url));
                        bw.write(String.format(" * 请求方式%s\r\n", httpMethod));
                        // System.out.println(String.format("name[%s],dec[%s],url[%s],httpMethod[%s]",
                        // sub.name(),
                        // sub.dec(), sub.url(), sub.method()));
                        // 接口入参
                        DocOAuth20SubIn[] args = sub.args();

                        bw.write(" * 入参数据结构说明\r\n");
                        bw.newLine();
                        bw.write(" <table class='table table-bordered table-striped table-condensed'>");
                        bw.newLine();
                        bw.write("<tr><td>名称</td><td>数据类型</td><td>参数说明</td><td>必须</td></tr>");
                        bw.newLine();
                        // bw.write(" | 名称 | 数据类型 | 参数说明 | 是否是必须的 |\r\n");
                        // bw.write(" | ------ | -------- | -------: |
                        // :--------:
                        // |\r\n");

                        if (args != null && args.length > 0) {
                            for (DocOAuth20SubIn arg : args) {
                                bw.write("<tr>");
                                bw.newLine();
                                bw.write(String.format("<td>%s</td><td>%s</td><td>%s</td><td>%s</td>", arg.name(),
                                        arg.type(), arg.dec(), arg.require() ? "是" : "否"));
                                bw.newLine();
                                bw.write("</tr>");
                            }
                        }
                        bw.write("</table>\r\n");
                        bw.newLine();
                    }
                }
            }
            bw.flush();
            bw.close();
            LOG.info("-----Oauth20接口文档构建结束-----");
            invokeCallback(bos.toByteArray());
        } catch (Exception e) {
            LOG.error(e);
        }
    }

}
