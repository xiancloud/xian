package info.xiancloud.plugin.netty.http.bean;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import info.xiancloud.plugin.Constant;
import info.xiancloud.plugin.netty.http.Config;
import info.xiancloud.plugin.util.LOG;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {

    @JSONField(serialize = false)
    private final FullHttpRequest httpRequest;

    private final String msgId;

    private final String url;
    /**
     * xian内部传递的header，注意，本map只会记录内部header属性，所谓内部header是指，以xian_开头的header属性，其他header不会放入本map
     */
    private final Map<String, String> header;

    private final String body;

    private final String contentType;

    /**
     * 服务器接收到req的时间
     */
    private final long reqReceivedTimeInMillis;

    //异步回调时使用
    @JSONField(serialize = false)
    private ChannelHandlerContext channelHandlerContext;

    public Request(FullHttpRequest httpRequest, String msgId) {
        this.httpRequest = httpRequest;
        this.msgId = msgId;
        /*QueryStringDecoder queryStringDecoder = new QueryStringDecoder(httpRequest.uri());*/
        url = httpRequest.uri()/*queryStringDecoder.path()*/;
        header = new HashMap<>();
        for (Map.Entry<String, String> headerKeyValue : httpRequest.headers()) {
            if (headerKeyValue.getKey().startsWith(Constant.XIAN_HEADER_PREFIX)) {
                header.put(headerKeyValue.getKey(), headerKeyValue.getValue());
            }
        }
        contentType = httpRequest.headers().get(HttpHeaderNames.CONTENT_TYPE, Config.getContentType());//如果请求方没有定义,则默认为Config.getContentType()
        String rawBody = httpRequest.content().toString(Config.defaultUtf8()).trim();
        if ((rawBody.startsWith("[") && rawBody.endsWith("]"))
                || (rawBody.startsWith("{") && rawBody.endsWith("}"))
                || (rawBody.startsWith("<") && rawBody.endsWith(">"))
                || (!rawBody.contains("&") && !rawBody.contains("="))) {
            LOG.debug("目前就用这种粗鲁的方式来判断请求为application/json或 text/xml，不依赖客户端给定contentType，客户端往往不靠谱");
            body = rawBody;
        } else {
            LOG.debug("如果不是json/xml串格式，那么就是application/x-www-form-urlencoded咯");
            Map<String, List<String>> parameters = new QueryStringDecoder(rawBody, false).parameters();
            JSONObject body = new JSONObject();
            for (String key : parameters.keySet()) {
                body.put(key, parameters.get(key).get(0));
            }
            this.body = body.toJSONString();
        }
        reqReceivedTimeInMillis = System.currentTimeMillis();
    }

    /**
     * 请参考 {@link Request#header}
     */
    public Map<String, String> getHeader() {
        return header;
    }

    public String getMsgId() {
        return msgId;
    }

    public String getUrl() {
        return url;
    }

    public String getBody() {
        return body;
    }

    public FullHttpRequest getHttpRequest() {
        return httpRequest;
    }

    public String getContentType() {
        return contentType;
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }

    public void setChannelHandlerContext(ChannelHandlerContext ctx) {
        channelHandlerContext = ctx;
    }

    public long getReqReceivedTimeInMillis() {
        return reqReceivedTimeInMillis;
    }
}
