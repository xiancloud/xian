package info.xiancloud.apidoc.unit.html;

import info.xiancloud.apidoc.ApidocGroup;
import info.xiancloud.apidoc.unit.md.FullMdApidocUnit;
import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.HttpContentType;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.message.Xian;

/**
 * full html api doc generator
 *
 * @author happyyangyuan
 */
public class FullHtmlApidocUnit implements Unit {

    @Override
    public String getName() {
        return "fullHtml";
    }

    @Override
    public Input getInput() {
        return Input.create().add("docName", String.class, "api doc name", NOT_REQUIRED)
                .add("docDescription", String.class, "api doc description", NOT_REQUIRED);
    }

    @Override
    public Group getGroup() {
        return ApidocGroup.singleton;
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        String md = Xian.call(FullMdApidocUnit.class, msg.getArgMap()).throwExceptionIfNotSuccess().dataToStr();
        String html = MdToHtml.mdToHtml(md);
        return UnitResponse.success(html).setContext(UnitResponse.Context.create().setHttpContentType(HttpContentType.TEXT_HTML));
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setDataOnly(true);
    }
}
