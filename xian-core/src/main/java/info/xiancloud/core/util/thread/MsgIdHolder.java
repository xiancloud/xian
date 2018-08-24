package info.xiancloud.core.util.thread;

import info.xiancloud.core.message.IdManager;
import info.xiancloud.core.util.Reflection;
import info.xiancloud.core.util.StringUtil;

import java.util.List;
import java.util.Objects;

/**
 * msgId holder, for log tracing and transaction tracing
 *
 * @author happyyangyuan
 */
public abstract class MsgIdHolder {

    /**
     * For IDE runtime environment this singleton instance is {@link MsgIdHolderForIde}.
     * For none IDE runtime environment
     */
    private static MsgIdHolder singleton;

    static {
        List<MsgIdHolder> msgIdHolders = Reflection.getSubClassInstances(MsgIdHolder.class);
        for (MsgIdHolder msgIdHolder : msgIdHolders) {
            if (msgIdHolder instanceof MsgIdHolderForIde) {
                continue;
            }
            singleton = msgIdHolder;
        }
        if (singleton == null) {
            singleton = msgIdHolders.get(0);
        }
    }

    protected static final String MSG_ID_KEY = "msgId";

    public static String get() {
        return singleton.get0();
    }

    public static void clear() {
        singleton.clear0();
    }

    public static String init() {
        clear();
        set(IdManager.nextMsgId());
        return get();
    }

    /**
     * Set the msg id of current context
     *
     * @param value the msg id, can not be null
     * @return false if old value is the same as new value, true otherwise.
     */
    public static boolean set(String value) {
        if (StringUtil.isEmpty(value)) {
            throw new IllegalArgumentException("Can not set an empty msgId. If you want to clear msgId of current context, use clear() instead.");
        }
        if (Objects.equals(value, MsgIdHolder.get())) {
            //no need make sure, return false
            return false;
        } else {
            //overwrite the old value
            singleton.set0(value);
            return true;
        }
    }

    protected abstract String get0();

    protected abstract void clear0();

    protected abstract void set0(String value);

    /**
     * A msgIdHolder for IDE runtime, and will only take effect in IDE runtime environment.
     * This class must be public for the reflectUtil to initiate.
     */
    public static final class MsgIdHolderForIde extends MsgIdHolder {

        private static final ThreadLocal<String> MSG_ID_THREAD_LOCAL = new InheritableThreadLocal<>();

        @Override
        protected String get0() {
            return MSG_ID_THREAD_LOCAL.get();
        }

        @Override
        protected void clear0() {
            MSG_ID_THREAD_LOCAL.remove();
        }

        @Override
        protected void set0(String value) {
            MSG_ID_THREAD_LOCAL.set(value);
        }
    }

}
