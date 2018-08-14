package info.xiancloud.dao.core.action.insert;

import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.Reflection;
import info.xiancloud.dao.core.action.AbstractSqlAction;
import info.xiancloud.dao.core.action.ISingleTableAction;
import info.xiancloud.dao.core.model.sqlresult.BatchInsertionResult;
import io.reactivex.Single;

import java.util.List;
import java.util.Map;

/**
 * Batch insertion action.
 * Parameter map must obey the following structure:
 * <code>{ "values" : [{},{},{}]}</code>
 * <p>
 * 注意:批量插入不支持本db框架的pattern模式,原因是本action的入参并不是单纯的一个map,而是map列表,这些map内的key都是一样的
 *
 * @author happyyangyuan
 */
public abstract class BatchInsertAction extends AbstractSqlAction implements ISingleTableAction {

    /**
     * Records to be inserted are in the parameter map under this key.
     */
    public static final String BATCH_INSERTION_VALUES_KEY = "values";

    @Override
    protected final Single<BatchInsertionResult> executeSql() {
        LOG.debug("返回的是插入的条数");
        List<Map<String, Object>> values = getValues();
        if (values == null || values.isEmpty()) {
            LOG.warn("没什么可以插入的数据，什么也不做");
            return Single.just(new BatchInsertionResult().setCount(0));
        }
        return getSqlDriver().batchInsert(this);
    }

    @Override
    public UnitResponse check() {
        if (getValues().size() >= 500) {
            LOG.warn("批量插入的记录过多:" + getValues().size(), new Throwable("本异常的作用是警告开发者批量插入的长度过长，但是它不阻止过长的插入语句执行."));
        }
        return super.check();
    }

    @Override
    protected final String patternSql() {
        throw new RuntimeException("pattern for batch insertion is forbidden.");
    }

    public List<Map<String, Object>> getValues() {
        return Reflection.toType(getMap().get("values"), List.class);
    }

}
