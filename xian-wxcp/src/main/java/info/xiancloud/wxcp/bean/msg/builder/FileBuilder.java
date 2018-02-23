package info.xiancloud.wxcp.bean.msg.builder;

import info.xiancloud.wxcp.bean.WxConsts;
import info.xiancloud.wxcp.bean.msg.WxCpMessage;

/**
 * 获得消息builder
 * <pre>
 * 用法: WxCustomMessage m = WxCustomMessage.FILE().mediaId(...).toUser(...).build();
 * </pre>
 *
 */
public final class FileBuilder extends BaseBuilder<FileBuilder> {
  private String mediaId;

  public FileBuilder() {
    this.msgType = WxConsts.CUSTOM_MSG_FILE;
  }

  public FileBuilder mediaId(String media_id) {
    this.mediaId = media_id;
    return this;
  }

  @Override
  public WxCpMessage build() {
    WxCpMessage m = super.build();
    m.setMediaId(this.mediaId);
    return m;
  }
}
