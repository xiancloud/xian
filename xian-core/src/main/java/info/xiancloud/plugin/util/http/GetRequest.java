package info.xiancloud.plugin.util.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class GetRequest extends Request {

    /**
     *
     */
    private static final long serialVersionUID = 7953771854365195465L;

    public GetRequest(String url) {
        super(url);
        method = HttpMethod.GET.name();
    }

    @Override
    protected HttpURLConnection generateConn() throws MalformedURLException, IOException {

        HttpURLConnection conn = null;
        StringBuilder params = null;
        // FIXME
        // 构建get请求url
        if (body != null && body.getParams() != null && body.getContent() == null) {
            params = new StringBuilder();
            for (String key : this.body.getParams().keySet()) {
				String value = this.body.getParams().get(key).toString();
                params.append(key).append("=").append(URLEncoder.encode(value, charset));
                params.append("&");
            }
            params.deleteCharAt(params.length() - 1);
            url += "?" + params.toString();
        }
        conn = (HttpURLConnection) new URL(url).openConnection();
        return conn;
    }
}
