package info.xiancloud.core.message.sender;

import com.alibaba.fastjson.JSONObject;
import info.xiancloud.core.NotifyHandler;
import info.xiancloud.core.distribution.exception.AbstractXianException;
import info.xiancloud.core.distribution.exception.AbstractXianException;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.NotifyHandler;
import info.xiancloud.core.util.LOG;

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
