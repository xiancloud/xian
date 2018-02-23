package info.xiancloud.plugin.message.sender;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.plugin.distribution.exception.AbstractXianException;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.support.mq.mqtt.handle.NotifyHandler;
import info.xiancloud.plugin.util.LOG;

/**
 * 专门处理异常的sender
 *
 * @author happyyangyuan
 */
public class ExceptionHandlerSender extends AbstractAsyncSender {
    private Throwable exception;

    ExceptionHandlerSender(UnitRequest request, NotifyHandler handler, Throwable exception) {
        super(request, handler);
        this.exception = exception;
        callback.addAfter(new NotifyHandler.Action() {
            protected void run(UnitResponse out) {
                LOG.error(new JSONObject() {{
                    put("type", "unit");
                    put("request", request.getArgMap());
                    put("response", out);
                    put("group", request.getContext().getGroup());
                    put("unit", request.getContext().getUnit());
                }}, exception);
            }
        });
    }

    @Override
    protected void asyncSend() throws Throwable {
        if (exception == null) {
            throw new IllegalArgumentException("既然是exceptionHandler，至少你传入一个exception对象过来啊，别传null啊!");
        }
        if (exception instanceof AbstractXianException) {
            callback.callback(((AbstractXianException) exception).toUnitResponse());
        } else
            throw exception;
    }

}
