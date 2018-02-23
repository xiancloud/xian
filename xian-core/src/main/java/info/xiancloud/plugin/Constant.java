package info.xiancloud.plugin;

public class Constant {

    public static String DEFAULT_ENCODING = "UTF-8";

    /**
     * You should know that '_' is not allowed in http header key.
     */
    public static final String XIAN_HEADER_PREFIX = "xian-";
    public static final String XIAN_APPLICATION_HEADER = XIAN_HEADER_PREFIX + "application";
    public static final String XIAN_MSG_ID_HEADER = XIAN_HEADER_PREFIX + "msgId";
    public static final String XIAN_REQUEST_TOKEN_HEADER = XIAN_HEADER_PREFIX + "accessToken";

    public static final long UNIT_DEFAULT_TIME_OUT_IN_MILLI = 1000 * 20;
    public static final int SYSTEM_EXIT_CODE_FOR_RPC_ERROR = -100;
    public static final int SYSTEM_EXIT_CODE_FOR_SYS_INIT_ERROR = -1;

    /**
     * rpc delimiter for message between message.
     * This is the xianframe standard.
     */
    public final static String RPC_DELIMITER = "\r\n$end!";

    public static final String DATE_SERIALIZE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * _consumedTime in millisecond
     */
    public static final String COST = "_cost";

    /**
     * system dao group name.
     *
     * @deprecated Do not use common group name. Group name is demanded to be unique globally.  todo Will be deleted some time later.
     */
    public static final String SYSTEM_DAO_GROUP_NAME = "dao";
    /**
     * Dao group interface full class name.
     * This classname is used to judge whether current node is a dao node.
     */
    public static final String DAO_GROUP_FULL_CLASSNAME = "info.xiancloud.plugin.dao.core.group.DaoGroup";

}
