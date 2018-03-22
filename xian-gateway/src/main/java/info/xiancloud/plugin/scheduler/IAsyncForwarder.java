package info.xiancloud.plugin.scheduler;

import info.xiancloud.plugin.Unit;
import info.xiancloud.plugin.UnitMeta;
import info.xiancloud.plugin.distribution.exception.UnitUndefinedException;
import info.xiancloud.plugin.distribution.loadbalance.UnitRouter;
import info.xiancloud.plugin.executor.BaseController;
import info.xiancloud.plugin.executor.URIBean;
import info.xiancloud.plugin.scheduler.body_not_required.DefaultAsyncForwarder;
import info.xiancloud.plugin.scheduler.body_not_required.RequestBodyNotRequiredAndDataOnlyResponseAsyncForwarder;
import info.xiancloud.plugin.scheduler.body_required.BodyRequiredAndResponseDataOnlyAsyncForwarder;
import info.xiancloud.plugin.scheduler.body_required.BodyRequiredAsyncForwarder;
import info.xiancloud.plugin.server.ServerRequestBean;

import java.util.Map;

/**
 * This forwarder interface is abstraction for api server forwarding requests to business {@link BaseController controllers}.
 * All subclasses must be stateless in order to use singleton pattern.
 * Forwarder is asynchronous, requests from api server are submitted to business thread pool for execution.
 *
 * @author happyyangyuan
 */
public interface IAsyncForwarder {

    /**
     * @param requestBean the request bean from api gateway netty http server other some kind of servers.
     */
    void forward(ServerRequestBean requestBean);

    /**
     * deprecated because too many parameters
     *
     * @param uri    http请求的uri
     * @param ip     请求方的真实ip
     * @param msgId  一次http请求的会话id
     * @param header http header map
     * @param body   http post body map
     */
    void forward(String uri, String ip, String msgId, Map<String, String> header, String body);

    /**
     * factory method for producing the right forwarder
     *
     * @param uri request uri
     * @return the selected forwarder
     */
    static IAsyncForwarder getForwarder(String uri) {
        URIBean uriBean = URIBean.create(uri);
        try {
            UnitMeta unitMeta = UnitRouter.singleton.newestDefinition(Unit.fullName(uriBean.getGroup(), uriBean.getUnit())).getMeta();
            if (unitMeta.isBodyRequired() && unitMeta.isDataOnly())
                return BodyRequiredAndResponseDataOnlyAsyncForwarder.singleton;
            else if (unitMeta.isBodyRequired() && !unitMeta.isDataOnly())
                return BodyRequiredAsyncForwarder.singleton;
            else if (!unitMeta.isBodyRequired() && unitMeta.isDataOnly())
                return RequestBodyNotRequiredAndDataOnlyResponseAsyncForwarder.singleton;
            else
                return DefaultAsyncForwarder.singleton;
        } catch (UnitUndefinedException ignored) {
            // we return default processor for unmapped uri request.
            return DefaultAsyncForwarder.singleton;
        }
    }

}
