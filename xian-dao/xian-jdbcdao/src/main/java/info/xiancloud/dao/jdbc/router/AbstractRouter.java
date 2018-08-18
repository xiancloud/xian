package info.xiancloud.dao.jdbc.router;

/**
 * @author happyyangyuan
 */
public abstract class AbstractRouter implements IMonthTableRouter {
    @Override
    public String getRoutedSql(String sql) {
        return sql;
    }

    abstract String getTable(String tableHeader);
}
