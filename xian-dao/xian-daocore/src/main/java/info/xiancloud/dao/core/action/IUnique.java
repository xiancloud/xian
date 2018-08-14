package info.xiancloud.dao.core.action;

/**
 * @author happyyangyuan
 */
public interface IUnique extends ISingleTableAction {

    /**
     * 已兼容传入驼峰/下划线
     *
     * @return 唯一性参数String/String数组/String集合
     * @deprecated 请直接给数据库表字段定义唯一性约束来实现!
     */
    default Object unique() {
        return "";
    }
}
