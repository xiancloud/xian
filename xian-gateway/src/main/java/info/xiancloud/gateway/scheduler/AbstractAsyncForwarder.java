package info.xiancloud.gateway.scheduler;

import info.xiancloud.core.Group;
import info.xiancloud.core.distribution.exception.BadRequestException;
import info.xiancloud.core.message.HttpContentType;
import info.xiancloud.core.message.RequestContext;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.thread_pool.ThreadPoolManager;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.StringUtil;
import info.xiancloud.gateway.controller.BaseController;
import info.xiancloud.gateway.controller.URIBean;
import info.xiancloud.gateway.handle.TransactionalNotifyHandler;
import info.xiancloud.gateway.rule_engine.IControllerMapper;
import info.xiancloud.gateway.server.IServerResponder;
import info.xiancloud.gateway.server.ServerRequestBean;
import info.xiancloud.gateway.server.ServerResponseBean;

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
            responseBean.setResponseBody(UnitResponse.createError(Group.CODE_BAD_REQUEST, null, badRequestException.getLocalizedMessage()).toVoJSONString());
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
        //uri parameters overwrite body parameters.
        controllerRequest.getArgMap().putAll(uriBean.getUriParameters());
        BaseController controller = IControllerMapper.getController(controllerRequest, new TransactionalNotifyHandler() {
            @Override
            protected void handle(UnitResponse unitResponse) {
                if (unitResponse == null) {
                    Throwable emptyOutputException = new RuntimeException("UnitResponse is not allowed to be null.");
                    LOG.error(emptyOutputException);
                    unitResponse = UnitResponse.createException(emptyOutputException);
                } else if (StringUtil.isEmpty(unitResponse.getCode())) {
                    Throwable emptyOutputException = new RuntimeException("UnitResponse's code is not allowed to be empty: " + unitResponse);
                    LOG.error(emptyOutputException);
                    unitResponse = UnitResponse.createException(emptyOutputException);
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
