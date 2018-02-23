package info.xiancloud.plugin.netty.http.bean;


/**
 * 封装这一个request和一个响应结果resPayload
 */
public class ResponseWrapper {

    private Request request;

    private String resPayload;

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

    @Override
    public String toString() {
        return resPayload;
    }
}
