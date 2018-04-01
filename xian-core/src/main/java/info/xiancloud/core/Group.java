package info.xiancloud.core;

/**
 * A group is where Units are in.
 * A group is usually served in one micro group.
 *
 * @author happyyangyuan
 */
public interface Group {

    /**
     * successful code
     */
    String CODE_SUCCESS = "SUCCESS";
    /**
     * unknown error code
     */
    String CODE_UNKNOWN_ERROR = "UNKNOWN_ERROR";
    /**
     * missing parameters
     */
    String CODE_LACK_OF_PARAMETER = "LACK_OF_PARAMETER";
    /**
     * For internal use only, when exception is thrown while the unit execution, no response object will be returned,
     * then the framework helps to generate an response object with this error code and the exception data object.
     */
    String CODE_EXCEPTION = "EXCEPTION";
    /**
     * error code if you call a remote unit while the unit is disabled for remote rpc.
     */
    String CODE_REMOTE_SENDER_DISABLED = "REMOTE_SENDER_DISABLED";
    /**
     * data does not exists
     */
    String CODE_DATA_DOES_NOT_EXITS = "DATA_DOES_NOT_EXITS";
    /**
     * unit is undefined.
     */
    String CODE_UNIT_UNDEFINED = "UNIT_UNDEFINED";
    /**
     * application is undefined
     */
    String CODE_APPLICATION_UNDEFINED = "APPLICATION_UNDEFINED";
    /**
     * group is undefined
     */
    String CODE_GROUP_UNDEFINED = "GROUP_UNDEFINED";
    /**
     * group is offline
     */
    String CODE_GROUP_OFFLINE = "GROUP_OFFLINE";
    /**
     * unit is offline
     */
    String CODE_UNIT_OFFLINE = "UNIT_OFFLINE";
    /**
     * application is offline
     */
    String CODE_APPLICATION_OFFLINE = "APPLICATION_OFFLINE";
    /**
     * time out
     */
    String CODE_TIME_OUT = "TIME_OUT";
    /**
     * request is illegal
     */
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
