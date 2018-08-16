package info.xiancloud.dao.core.transaction.local;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalCause;
import com.google.common.cache.RemovalListener;
import info.xiancloud.core.util.LOG;
import info.xiancloud.dao.core.connection.XianConnection;
import info.xiancloud.dao.core.pool.DatasourceConfigReader;
import info.xiancloud.dao.core.transaction.ReentrantTransaction;
import io.reactivex.Completable;

import java.util.concurrent.TimeUnit;

/**
 * Local, non-distributed, reentrant transaction
 *
 * @author happyyangyuan
 */
public abstract class BaseLocalTransaction extends ReentrantTransaction implements LocalTransaction {

    /**
     * transaction timeout in milliseconds, read from configuration.
     */
    private final static long TIMEOUT = DatasourceConfigReader.getTransactionTimeout();

    /**
     * private local transaction map
     */
    private static final Cache<String, BaseLocalTransaction> LOCAL_TRANS_MAP = CacheBuilder.newBuilder()
            .expireAfterWrite(TIMEOUT, TimeUnit.MILLISECONDS)
            .removalListener((RemovalListener<String, BaseLocalTransaction>) notification -> {
                if (notification.getCause() == RemovalCause.EXPIRED) {
                    //in case of transaction leak
                    BaseLocalTransaction transaction = notification.getValue();
                    LOG.warn("Transaction timeout detected: " + transaction.getTransactionId());
                    transaction.rollback().andThen(transaction.getConnection().close()).subscribe(() ->
                            LOG.info("Transaction Timeout has been rollbacked: " + transaction.getTransactionId())
                    );
                }
            })
            .build();

    public BaseLocalTransaction(String transactionId, XianConnection xianConnection) {
        super(transactionId, xianConnection);
    }

    /**
     * Add the specified local transaction into the local transaction map.
     */
    public static void addLocalTransaction(BaseLocalTransaction localTransaction) {
        LOCAL_TRANS_MAP.put(localTransaction.getTransactionId(), localTransaction);
    }

    /**
     * Get the existed transaction or null if not existed.
     *
     * @return local cached transaction reference or null if not exists.
     */
    public static BaseLocalTransaction getExistedLocalTrans(String transId) {
        return LOCAL_TRANS_MAP.getIfPresent(transId);
    }

    @Override
    protected Completable clear() {
        return Completable
                .fromAction(() -> LOCAL_TRANS_MAP.invalidate(transactionId))
                .doOnComplete(() -> count.set(0))
                .andThen(getConnection().close())
                ;
    }

}

