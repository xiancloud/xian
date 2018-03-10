package info.xiancloud.plugin.netty.http.bean;


/**
 * 封装这一个request和一个响应结果resPayload
 */
final public class ResponseWrapper {

    private Request request;

    private String resPayload;

    private String httpContentType;

    public ResponseWrapper(Request request, String resPayload) {
        this.request = request;
        this.resPayload = resPayload;
    }

    public Request getRequest() {
        return request;
    }

    public Object getResPayload() {
        return resPayload;
    }

    public String getHttpContentType() {
        return httpContentType;
    }

    public ResponseWrapper setHttpContentType(String httpContentType) {
        this.httpContentType = httpContentType;
        return this;
    }

    @Override
    public String toString() {
        return resPayload;
    }
}
