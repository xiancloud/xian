package info.xiancloud.apidoc.unit.html;

import info.xiancloud.apidoc.ApidocGroup;
import info.xiancloud.apidoc.unit.md.CustomizedMdApidocUnit;
import info.xiancloud.core.*;
import info.xiancloud.core.message.HttpContentType;
import info.xiancloud.core.message.SingleRxXian;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;

/**
 * custom html api doc generator
 *
 * @author happyyangyuan
 */
public class CustomizedHtmlApidocUnit implements Unit {

    @Override
    public String getName() {
        return "customizedHtml";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create()
                /*tell the api gateway to return data part of the response only*/.setDataOnly(true)
                /*tell the api gateway not to check the access token header.*/.setSecure(false);
    }

    @Override
    public Input getInput() {
        return Input.create().add("docName", String.class, "api doc name", REQUIRED)
                .add("unitFilter", String.class, "unit list", REQUIRED)
                .add("docDescription", String.class, "api doc description", NOT_REQUIRED)
                ;
    }

    @Override
    public Group getGroup() {
        return ApidocGroup.singleton;
    }

    @Override
    public void execute(UnitRequest msg, Handler<UnitResponse> handler) {
        SingleRxXian
                .call(CustomizedMdApidocUnit.class, msg.getArgMap())
                .subscribe(unitResponse -> {
                    unitResponse.throwExceptionIfNotSuccess();
                    String md = unitResponse.dataToStr();
                    UnitResponse myResponse = UnitResponse.createSuccess(MdToHtml.mdToHtml(md)).setContext(UnitResponse.Context.create().setHttpContentType(HttpContentType.TEXT_HTML));
                    handler.handle(myResponse);
                });
    }
}
