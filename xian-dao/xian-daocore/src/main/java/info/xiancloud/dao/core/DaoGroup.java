package info.xiancloud.dao.core;

import info.xiancloud.core.Constant;
import info.xiancloud.core.Group;
import info.xiancloud.core.util.Reflection;

import java.util.Collections;
import java.util.List;

/**
 * dao unit's group interface
 *
 * @author happyyangyuan
 */
public interface DaoGroup extends Group {
    String CODE_SQL_ERROR = "SQL_ERROR";
    String CODE_DB_ERROR = "DB_ERROR";
    String CODE_REPETITION_NOT_ALLOWED = "REPETITION_NOT_ALLOWED";
    String CODE_TRANSACTION_ALREADY_ENDS = "TRANSACTION_ALREADY_ENDS";
    List<DaoGroup> GROUP_LIST = Collections.unmodifiableList(Reflection.getSubClassInstances(DaoGroup.class));
    /**
     * @deprecated Do not use common group name. Group name is demanded to be unique globally.
     */
    DaoGroup SINGLETON = () -> Constant.SYSTEM_DAO_GROUP_NAME;

    /**
     * Indicates this group is a dao group
     *
     * @return always return true
     */
    @Override
    default boolean isDao() {
        return true;
    }
}
