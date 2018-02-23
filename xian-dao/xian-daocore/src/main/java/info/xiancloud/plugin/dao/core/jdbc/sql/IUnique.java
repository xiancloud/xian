package info.xiancloud.plugin.dao.core.jdbc.sql;

/**
 * @author happyyangyuan
 */
public interface IUnique extends ISingleTableAction{

    /**
     * 已兼容传入驼峰/下划线
     *
     * @return 唯一性参数String/String数组/String集合
     */
    Object unique();
}
