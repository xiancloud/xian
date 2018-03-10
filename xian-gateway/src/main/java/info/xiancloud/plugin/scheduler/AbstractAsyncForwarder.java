package info.xiancloud.plugin.scheduler;

import info.xiancloud.plugin.Group;
import info.xiancloud.plugin.distribution.exception.BadRequestException;
import info.xiancloud.plugin.executor.BaseController;
import info.xiancloud.plugin.executor.URIBean;
import info.xiancloud.plugin.handle.TransactionalNotifyHandler;
import info.xiancloud.plugin.message.HttpContentType;
import info.xiancloud.plugin.message.RequestContext;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.rule_engine.IControllerMapping;
import info.xiancloud.plugin.server.IServerResponder;
import info.xiancloud.plugin.server.ServerRequestBean;
import info.xiancloud.plugin.server.ServerResponseBean;
import info.xiancloud.plugin.thread_pool.ThreadPoolManager;
import info.xiancloud.plugin.util.LOG;
import info.xiancloud.plugin.util.StringUtil;

import java.util.Map;

/**
 * Template super class for all controller forwarder
 *
 * @author happyyangyuan
 */
public abstract class AbstractAsyncForwarder implements IAsyncForwarder, IResponseExtractor {

    @Override
    public void forward(ServerRequestBean requestBean) {
        forward(requestBean.getUri(),
                requestBean.getIp(),
                requestBean.getMsgId(),
                requestBean.getHeader(),
                requestBean.getBody()
        );
    }

    @Override
    public void forward(String uri, String ip, String msgId, Map<String, String> header, String body) {
        LOG.debug("this method is running in the netty http server's io thread.");
        UnitRequest controllerRequest;
        try {
            controllerRequest = bodyParams(body, header);
        } catch (BadRequestException badRequestException) {
            LOG.warn(badRequestException);
            ServerResponseBean responseBean = new ServerResponseBean();
            responseBean.setMsgId(msgId);
            responseBean.setHttpContentType(HttpContentType.APPLICATION_JSON);
            responseBean.setResponseBody(UnitResponse.error(Group.CODE_BAD_REQUEST, null, badRequestException.getLocalizedMessage()).toVoJSONString());
            IServerResponder.singleton.response(responseBean);
            return;
        }
        RequestContext context = controllerRequest.getContext();
        URIBean uriBean = URIBean.create(uri);
        context.setUri(uri)
                .setIp(ip)
                .setHeader(header)
                .setUnit(uriBean.getUnit())
                .setGroup(uriBean.getGroup())
                .setUriExtension(uriBean.getUriExtension())
                .setMsgId(msgId)
                .setUriParameters(uriBean.getUriParameters());
        controllerRequest.getArgMap().putAll(uriBean.getUriParameters());//uri parameters overwrite body parameters.
        BaseController controller = IControllerMapping.getController(controllerRequest, new TransactionalNotifyHandler() {
            protected void handle(UnitResponse unitResponse) {
                if (unitResponse == null) {
                    Throwable emptyOutputException = new RuntimeException("UnitResponse is not allowed to be null.");
                    LOG.error(emptyOutputException);
                    unitResponse = UnitResponse.exception(emptyOutputException);
                } else if (StringUtil.isEmpty(unitResponse.getCode())) {
                    Throwable emptyOutputException = new RuntimeException("UnitResponse's code is not allowed to be empty: " + unitResponse);
                    LOG.error(emptyOutputException);
                    unitResponse = UnitResponse.exception(emptyOutputException);
                }
                ServerResponseBean responseBean = new ServerResponseBean();
                responseBean.setResponseBody(extractContext(unitResponse));
                responseBean.setMsgId(msgId);
                responseBean.setHttpContentType(unitResponse.getContext().getHttpContentType());
                IServerResponder.singleton.response(responseBean);
            }
        });
        ThreadPoolManager.execute(controller, msgId);
    }

    /**
     * prepare the body parameters with the given http body string
     *
     * @param body   the http post request body.
     * @param header the http header map
     * @return request bean with body and header data for the controller
     * @throws BadRequestException bad request
     */
    protected abstract UnitRequest bodyParams(String body, Map<String, String> header) throws BadRequestException;


}
