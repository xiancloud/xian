package info.xiancloud.wxcp.bean.msg.builder;

import info.xiancloud.wxcp.bean.WxConsts;
import info.xiancloud.wxcp.bean.msg.WxCpMessage;
import info.xiancloud.wxcp.bean.msg.WxcpMessageText;

/**
 * 文本消息builder
 * 
 * <pre>
 * 用法: WxCustomMessage m = WxCustomMessage.TEXT().content(...).toUser(...).build();
 * </pre>
 *
 */
public final class TextBuilder extends BaseBuilder<TextBuilder> {
	private String content;

	public TextBuilder() {
		this.msgType = WxConsts.CUSTOM_MSG_TEXT;
	}

	public TextBuilder content(String content) {
		this.content = content;
		return this;
	}

	@Override
	public WxCpMessage build() {
		WxCpMessage m = super.build();
		WxcpMessageText text = new WxcpMessageText();
		text.setContent(this.content);
		m.setText(text);
		return m;
	}
}
