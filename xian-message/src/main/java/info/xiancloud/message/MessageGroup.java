package info.xiancloud.message;


import info.xiancloud.core.Group;

/**
 * @author happyyangyuan
 */
public class MessageGroup implements Group {
    public static final String CODE_MESSAGE_TOO_LONG = "MESSAGE_TOO_LONG";
    final public static Group singleton = new MessageGroup();

    @Override
    public String getName() {
        return "message";
    }

    @Override
    public String getDescription() {
        return "消息服务";
    }

}