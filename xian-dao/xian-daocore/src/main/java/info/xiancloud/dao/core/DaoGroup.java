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
    List<DaoGroup> groupList = Collections.unmodifiableList(Reflection.getSubClassInstances(DaoGroup.class));
    /**
     * @deprecated Do not use common group name. Group name is demanded to be unique globally.
     */
    DaoGroup singleton = () -> Constant.SYSTEM_DAO_GROUP_NAME;

    default boolean isDao() {
        return true;
    }
}
