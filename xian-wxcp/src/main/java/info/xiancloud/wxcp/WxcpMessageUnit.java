package info.xiancloud.wxcp;

import info.xiancloud.plugin.*;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.util.LOG;
import info.xiancloud.plugin.util.StringUtil;
import info.xiancloud.wxcp.api.WxCpApi;
import info.xiancloud.wxcp.api.WxCpApiFactory;
import info.xiancloud.wxcp.api.WxCpConfigStorage;
import info.xiancloud.wxcp.api.WxCpConfigStorageFactory;
import info.xiancloud.wxcp.bean.msg.WxCpMessage;
import info.xiancloud.wxcp.exception.WxErrorException;

import java.util.List;

/**
 * 企业微信文本消息发送unit
 *
 * @author yyq
 */
public class WxcpMessageUnit implements Unit {
    @Override
    public String getName() {
        return "wxcpMessage";
    }

    @Override
    public Group getGroup() {
        return new WxcpGroup();
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setDescription("企业微信消息发送");
    }

    //FIXME
    //企业微信配置项
    private static WxCpConfigStorage configStorage;

    private static WxCpApi wxCpApi;

    static {
        configStorage = WxCpConfigStorageFactory.build();
        wxCpApi = WxCpApiFactory.buildWxCpApi();
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        //文本消息
        WxCpMessage message = WxCpMessage
                .TEXT()
                .agentId(configStorage.getAgentId())
                .toUser("@all")
                .content(msg.get("content", String.class))
                .build();

        List<Input.Obj> argList = this.getInput() != null ? this.getInput().getList() : null;
        if (argList != null && !argList.isEmpty()) {
            argList.forEach(arg -> {
                if (!StringUtil.isEmpty(msg.getArgMap().get(arg.getName()))) {
                    msg.getArgMap().get(arg.getName());
                }
            });
        }

        try {
            wxCpApi.messageSend(message);
        } catch (WxErrorException e) {
            LOG.error("企业微信发送消息出错", e);
            return UnitResponse.failure(null, e.getError().toString());

        }
        return UnitResponse.success(Group.CODE_SUCCESS);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("toUser", String.class, "成员ID列表（消息接收者，多个接收者用‘|’分隔，最多支持1000个）,特殊情况：指定为@all，则向关注该企业应用的全部成员发送,默认@all")
                .add("toparty", String.class, "部门ID列表，多个接收者用‘|’分隔，最多支持100个。当touser为@all时忽略本参数")
                .add("toparty", String.class, "标签ID列表，多个接收者用‘|’分隔，最多支持100个。当touser为@all时忽略本参数")
                .add("content", String.class, "消息内容，最长不超过2048个字节", REQUIRED);
    }

}
