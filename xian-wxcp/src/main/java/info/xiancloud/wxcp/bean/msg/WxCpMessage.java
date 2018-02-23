package info.xiancloud.wxcp.bean.msg;

import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import info.xiancloud.wxcp.bean.msg.builder.FileBuilder;
import info.xiancloud.wxcp.bean.msg.builder.ImageBuilder;
import info.xiancloud.wxcp.bean.msg.builder.MpnewsBuilder;
import info.xiancloud.wxcp.bean.msg.builder.NewsBuilder;
import info.xiancloud.wxcp.bean.msg.builder.TextBuilder;
import info.xiancloud.wxcp.bean.msg.builder.VideoBuilder;
import info.xiancloud.wxcp.bean.msg.builder.VoiceBuilder;

/**
 * Json格式消息
 *
 */
public class WxCpMessage implements Serializable {

	private static final long serialVersionUID = -2082278303476631708L;
	@JSONField(name = "touser")
	private String toUser;
	@JSONField(name = "toparty")
	private String toParty;
	@JSONField(name = "totag")
	private String toTag;
	@JSONField(name = "agentid")
	private Integer agentId;
	@JSONField(name = "msgtype")
	private String msgType;
	private WxcpMessageText text;
	@JSONField(name = "media_id")
	private String mediaId;
	private String thumbMediaId;
	private String title;
	private String description;
	private String musicUrl;
	private String hqMusicUrl;
	// private String safe;
	private List<NewArticle> articles;
	private List<MpnewsArticle> mpnewsArticles;

	public List<MpnewsArticle> getMpnewsArticles() {
		return mpnewsArticles;
	}

	public void setMpnewsArticles(List<MpnewsArticle> mpnewsArticles) {
		this.mpnewsArticles = mpnewsArticles;
	}

	/**
	 * 获得文本消息builder
	 */
	public static TextBuilder TEXT() {
		return new TextBuilder();
	}

	/**
	 * 获得图片消息builder
	 */
	public static ImageBuilder IMAGE() {
		return new ImageBuilder();
	}

	/**
	 * 获得语音消息builder
	 */
	public static VoiceBuilder VOICE() {
		return new VoiceBuilder();
	}

	/**
	 * 获得视频消息builder
	 */
	public static VideoBuilder VIDEO() {
		return new VideoBuilder();
	}

	/**
	 * 获得图文消息builder
	 */
	public static NewsBuilder NEWS() {
		return new NewsBuilder();
	}

	/**
	 * 获得mpnews图文消息builder
	 */
	public static MpnewsBuilder MPNEWS() {
		return new MpnewsBuilder();
	}

	/**
	 * 获得文件消息builder
	 */
	public static FileBuilder FILE() {
		return new FileBuilder();
	}

	public String getToUser() {
		return this.toUser;
	}

	public void setToUser(String toUser) {
		this.toUser = toUser;
	}

	public String getToParty() {
		return this.toParty;
	}

	public void setToParty(String toParty) {
		this.toParty = toParty;
	}

	public String getToTag() {
		return this.toTag;
	}

	public void setToTag(String toTag) {
		this.toTag = toTag;
	}

	public Integer getAgentId() {
		return this.agentId;
	}

	public void setAgentId(Integer agentId) {
		this.agentId = agentId;
	}

	public String getMsgType() {
		return this.msgType;
	}

	/**
	 * @param msgType
	 *            消息类型
	 */
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String getMediaId() {
		return this.mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}

	public String getThumbMediaId() {
		return this.thumbMediaId;
	}

	public void setThumbMediaId(String thumbMediaId) {
		this.thumbMediaId = thumbMediaId;
	}

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

	public String getMusicUrl() {
		return this.musicUrl;
	}

	public void setMusicUrl(String musicUrl) {
		this.musicUrl = musicUrl;
	}

	public String getHqMusicUrl() {
		return this.hqMusicUrl;
	}

	public void setHqMusicUrl(String hqMusicUrl) {
		this.hqMusicUrl = hqMusicUrl;
	}

	public List<NewArticle> getArticles() {
		return this.articles;
	}

	public void setArticles(List<NewArticle> articles) {
		this.articles = articles;
	}

	public String toJson() {
		return JSON.toJSONString(this);
	}

	public WxcpMessageText getText() {
		return text;
	}

	public void setText(WxcpMessageText text) {
		this.text = text;
	}

}

