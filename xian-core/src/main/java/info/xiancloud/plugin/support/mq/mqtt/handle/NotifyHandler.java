package info.xiancloud.plugin.support.mq.mqtt.handle;

import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.util.LOG;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author 钟俊伟, happyyangyuan
 */
public abstract class NotifyHandler {

    private boolean timeout = false;
    private List<Action> beforeActions = new ArrayList<>();
    private List<Action> afterActions = new ArrayList<>();
    private boolean async = true;

    public NotifyHandler setTimeout(boolean timeout) {
        this.timeout = timeout;
        return this;
    }

    public boolean isAsync() {
        return async;
    }

    public NotifyHandler setAsync(boolean async) {
        this.async = async;
        return this;
    }

    public void callback(UnitResponse unitResponseObject) {
        for (Action beforeAction : beforeActions) {
            try {
                beforeAction.run(unitResponseObject);
            } catch (Throwable throwable) {
                LOG.error("beforeAction执行失败，但是不妨碍下一个action执行", throwable);
            }
        }
        if (timeout) {
            LOG.error("本次消息已经被判定为超时,但是超时后又收到了响应!  需要人工恢复。 unitResponseObject= " + unitResponseObject);
        } else {
            try {
                toContinue(unitResponseObject);
            } catch (Throwable e) {
                LOG.error("回调执行失败", e);
            }
        }
        for (Action afterAction : afterActions) {
            try {
                afterAction.run(unitResponseObject);
            } catch (Throwable e) {
                LOG.error("afterAction执行失败，但是不妨碍下一个action执行", e);
            }
        }

    }

    public void addBefore(Action... oneOrMoreActions) {
        Collections.addAll(beforeActions, oneOrMoreActions);
    }

    public void addAfter(Action... oneOrMoreActions) {
        Collections.addAll(afterActions, oneOrMoreActions);
    }

    protected abstract void toContinue(UnitResponse unitResponse);

    public abstract static class Action {
        protected abstract void run(UnitResponse out);
    }
}
