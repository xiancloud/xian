package com.apifest.oauth20.bean.token_request;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.CharsetUtil;

/**
 * @author happyyangyuan
 */
public class TokenRequest_json extends TokenRequest {

    public static TokenRequest_json create(FullHttpRequest httpRequest) {
        String body = httpRequest.content().toString(CharsetUtil.UTF_8);
        try {
            return JSON.parseObject(body).toJavaObject(TokenRequest_json.class);
        } catch (JSONException e) {
            throw new IllegalArgumentException(String.format("tokenRequest请求的jsonBody格式不合法:%s", body), e);
        }
    }

}
