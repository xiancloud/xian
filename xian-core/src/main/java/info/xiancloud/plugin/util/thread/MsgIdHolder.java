package info.xiancloud.plugin.util.thread;

import info.xiancloud.plugin.message.IdManager;
import info.xiancloud.plugin.util.Reflection;

import java.util.List;

/**
 * msgId holder, for log tracing.
 *
 * @author happyyangyuan
 */
public abstract class MsgIdHolder {

    private static MsgIdHolder singleton;

    static {
        List<MsgIdHolder> msgIdHolders = Reflection.getSubClassInstances(MsgIdHolder.class);
        for (MsgIdHolder msgIdHolder : msgIdHolders) {
            if (msgIdHolder instanceof MsgIdHoldNothing)
                continue;
            singleton = msgIdHolder;
        }
        if (singleton == null)
            singleton = msgIdHolders.get(0);
    }

    protected static final String MSG_ID = "msgId";

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

    public static void set(String value) {
        singleton.set0(value);
    }

    protected abstract String get0();

    protected abstract void clear0();

    protected abstract void set0(String value);

    /**
     * A msgIdHolder holds nothing for IDE runtime. This class must be public for the reflectUtil to initiate.
     */
    public static final class MsgIdHoldNothing extends MsgIdHolder {

        @Override
        protected String get0() {
            return null;
        }

        @Override
        protected void clear0() {

        }

        @Override
        protected void set0(String value) {

        }
    }

}
