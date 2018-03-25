package info.xiancloud.apifestoauth20;

import com.apifest.oauth20.api.ExceptionEventHandler;
import info.xiancloud.core.util.LOG;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.CharsetUtil;

/**
 * @author happyyangyuan
 */
public class OAuthExceptionHandler implements ExceptionEventHandler {
    @Override
    public void handleException(Exception ex, HttpRequest request0) {
        FullHttpRequest request = (FullHttpRequest) request0;
        LOG.error(String.format("认证异常,uri={%s},method={%s},content={%s}", request.uri(), request.method(), request.content().toString(CharsetUtil.UTF_8)), ex);
    }
}
