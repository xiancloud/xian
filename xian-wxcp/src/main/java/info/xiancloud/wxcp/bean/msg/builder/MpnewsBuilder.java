package info.xiancloud.wxcp.bean.msg.builder;

import java.util.ArrayList;
import java.util.List;

import info.xiancloud.wxcp.bean.WxConsts;
import info.xiancloud.wxcp.bean.msg.MpnewsArticle;
import info.xiancloud.wxcp.bean.msg.WxCpMessage;

/**
 * mpnews类型的图文消息builder
 * 
 * <pre>
 * 用法:
 * WxCustomMessage m = WxCustomMessage.MPNEWS().addArticle(article).toUser(...).build();
 * </pre>
 *
 */
public final class MpnewsBuilder extends BaseBuilder<MpnewsBuilder> {
	private List<MpnewsArticle> articles = new ArrayList<>();

	private String mediaId;

	public MpnewsBuilder() {
		this.msgType = WxConsts.CUSTOM_MSG_MPNEWS;
	}

	public MpnewsBuilder mediaId(String mediaId) {
		this.mediaId = mediaId;
		return this;
	}

	public MpnewsBuilder addArticle(MpnewsArticle article) {
		this.articles.add(article);
		return this;
	}

	@Override
	public WxCpMessage build() {
		WxCpMessage m = super.build();
		m.setMpnewsArticles(this.articles);
		if (this.mediaId != null) {
			m.setMediaId(this.mediaId);
		}
		return m;
	}
}
