package info.xiancloud.wxcp;

import info.xiancloud.plugin.Group;

/**
 * 企业微信服务
 *
 * @author yyq
 */
public class WxcpGroup implements Group {

    @Override
    public String getName() {
        return "wxcp";
    }

    @Override
    public String getDescription() {
        return "企业微信服务单元组";
    }
}
