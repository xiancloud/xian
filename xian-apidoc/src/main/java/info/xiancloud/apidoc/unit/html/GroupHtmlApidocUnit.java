package info.xiancloud.apidoc.unit.html;

import info.xiancloud.apidoc.ApidocGroup;
import info.xiancloud.apidoc.unit.md.GroupMdApidocUnit;
import info.xiancloud.plugin.Group;
import info.xiancloud.plugin.Input;
import info.xiancloud.plugin.Unit;
import info.xiancloud.plugin.UnitMeta;
import info.xiancloud.plugin.message.HttpContentType;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.Xian;

/**
 * generate html api doc.
 *
 * @author happyyangyuan
 */
public class GroupHtmlApidocUnit implements Unit {
    @Override
    public String getName() {
        return "groupHtml";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setDataOnly(true);
    }

    @Override
    public Input getInput() {
        return Input.create().add("groupName", String.class, "group name", REQUIRED)
                .add("docName", String.class, "api doc name", REQUIRED)
                .add("docDescription", String.class, "doc description", NOT_REQUIRED);
    }

    @Override
    public Group getGroup() {
        return ApidocGroup.singleton;
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        String md = Xian.call(GroupMdApidocUnit.class, msg.getArgMap()).throwExceptionIfNotSuccess().dataToStr();
        String html = MdToHtml.mdToHtml(md);
        return UnitResponse.success(html).setContext(UnitResponse.Context.create().setHttpContentType(HttpContentType.TEXT_HTML));
    }
}
