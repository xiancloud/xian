package info.xiancloud.plugin;

/**
 * A group is where Units are in.
 * A group is usually served in one micro group.
 *
 * @author happyyangyuan
 */
public interface Group {

    String CODE_SUCCESS = "SUCCESS";
    String CODE_FAILURE = "FAILURE";
    String CODE_LACK_OF_PARAMETER = "LACK_OF_PARAMETER";
    /**
     * For internal use only, when exception is thrown while the unit execution, no response object will be returned,
     * then the framework helps to generate an response object with this error code and the exception data object.
     */
    String CODE_EXCEPTION = "EXCEPTION";

    String CODE_REMOTE_SENDER_DISABLED = "REMOTE_SENDER_DISABLED";
    String CODE_OPERATE_ERROR = "OPERATE_ERROR";
    String CODE_DATA_DOES_NOT_EXITS = "DATA_DOES_NOT_EXITS";

    String CODE_UNIT_UNDEFINED = "UNIT_UNDEFINED";
    String CODE_APPLICATION_UNDEFINED = "APPLICATION_UNDEFINED";
    String CODE_GROUP_UNDEFINED = "GROUP_UNDEFINED";

    String CODE_GROUP_OFFLINE = "GROUP_OFFLINE";
    String CODE_UNIT_OFFLINE = "UNIT_OFFLINE";
    String CODE_APPLICATION_OFFLINE = "APPLICATION_OFFLINE";

    String CODE_TIME_OUT = "TIME_OUT";
    String CODE_BAD_REQUEST = "BAD_REQUEST";

    /**
     * Group name must be unique globally.
     *
     * @return Group name
     */
    String getName();

    /**
     * group description. Defaults to the group name.
     */
    default String getDescription() {
        return getName();
    }

    /**
     * @return true if this group is dao group otherwise false. Defaults to false.
     */
    default boolean isDao() {
        return false;
    }

}
