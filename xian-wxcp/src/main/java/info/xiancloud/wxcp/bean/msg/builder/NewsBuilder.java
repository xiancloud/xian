package info.xiancloud.wxcp.bean.msg.builder;

import java.util.ArrayList;
import java.util.List;

import info.xiancloud.wxcp.bean.WxConsts;
import info.xiancloud.wxcp.bean.msg.NewArticle;
import info.xiancloud.wxcp.bean.msg.WxCpMessage;

/**
 * 图文消息builder
 * <pre>
 * 用法:
 * WxCustomMessage m = WxCustomMessage.NEWS().addArticle(article).toUser(...).build();
 * </pre>
 *
 */
public final class NewsBuilder extends BaseBuilder<NewsBuilder> {

  private List<NewArticle> articles = new ArrayList<>();

  public NewsBuilder() {
    this.msgType = WxConsts.CUSTOM_MSG_NEWS;
  }

  public NewsBuilder addArticle(NewArticle article) {
    this.articles.add(article);
    return this;
  }

  @Override
  public WxCpMessage build() {
    WxCpMessage m = super.build();
    m.setArticles(this.articles);
    return m;
  }
}
