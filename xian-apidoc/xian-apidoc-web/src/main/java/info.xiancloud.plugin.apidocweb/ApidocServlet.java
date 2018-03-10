package info.xiancloud.plugin.apidocweb;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.apidoc.unit.md.CustomizedMdApidocUnit;
import info.xiancloud.apidoc.unit.md.FullMdApidocUnit;
import info.xiancloud.apidoc.unit.md.GroupMdApidocUnit;
import info.xiancloud.plugin.Unit;
import info.xiancloud.plugin.message.Xian;
import info.xiancloud.plugin.util.HttpUtil;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author happyyangyuan
 */
public class ApidocServlet extends HttpServlet {

    private static final String PATH_PREFIX = "/apidoc/";
    private static final String DOC_TYPE_FULL = "full";
    private static final String DOC_TYPE_CUSTOM = "custom";
    private static final String DOC_TYPE_GROUP = "group";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        invoke(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        invoke(req, resp);
    }

    private void invoke(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setHeader("Content-type", "text/plain; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        String docType = req.getRequestURI().substring(PATH_PREFIX.length());
        Class<? extends Unit> apidocUnitClass;
        switch (docType) {
            case DOC_TYPE_CUSTOM:
                apidocUnitClass = CustomizedMdApidocUnit.class;
                break;
            case DOC_TYPE_FULL:
                apidocUnitClass = FullMdApidocUnit.class;
                break;
            case DOC_TYPE_GROUP:
                apidocUnitClass = GroupMdApidocUnit.class;
                break;
            default:
                apidocUnitClass = FullMdApidocUnit.class;
        }
        JSONObject params = HttpUtil.parseQueryString(req.getQueryString(), false);
        PrintWriter out = resp.getWriter();
        try {
            Unit apidocUnit = apidocUnitClass.newInstance();
            out.print(Xian.call(apidocUnit.getGroup().getName(), apidocUnit.getName(), params, 2 * 60 * 1000)
                    .throwExceptionIfNotSuccess().dataToStr());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
