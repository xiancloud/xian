package info.xiancloud.apidoc.unit.html;

import info.xiancloud.apidoc.ApidocGroup;
import info.xiancloud.apidoc.unit.md.GroupMdApidocUnit;
import info.xiancloud.core.*;
import info.xiancloud.core.message.HttpContentType;
import info.xiancloud.core.message.SingleRxXian;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;

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
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        SingleRxXian
                .call(GroupMdApidocUnit.class, msg.getArgMap())
                .subscribe(unitResponse -> {
                    unitResponse.throwExceptionIfNotSuccess();
                    String md = unitResponse.dataToStr();
                    String html = MdToHtml.mdToHtml(md);
                    handler.handle(UnitResponse.createSuccess(html).setContext(UnitResponse.Context.create().setHttpContentType(HttpContentType.TEXT_HTML)));
                });
    }
}
