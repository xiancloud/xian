package info.xiancloud.wxcp.bean.msg;

/**
 * 图文消息
 *
 */
public class NewArticle {

  private String title;
  private String description;
  private String url;
  private String picUrl;

  public String getTitle() {
    return this.title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getUrl() {
    return this.url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getPicUrl() {
    return this.picUrl;
  }

  public void setPicUrl(String picUrl) {
    this.picUrl = picUrl;
  }

}
