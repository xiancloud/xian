package info.xiancloud.dao.core.connection;

import info.xiancloud.core.util.Reflection;
import info.xiancloud.dao.core.transaction.XianTransaction;
import info.xiancloud.dao.core.transaction.local.BaseLocalTransaction;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Base xian connection
 *
 * @author happyyangyuan
 */
public abstract class BaseXianConnection implements XianConnection {
    protected boolean closed = false;

    @Override
    public XianTransaction createTransaction(String transactionId) {
        //currently we only support local transaction.
        return createLocalTransaction(transactionId);
    }

    /**
     * create a new local transaction.
     * All transactions are produced by connections.
     *
     * @param transId transaction id
     */
    private BaseLocalTransaction createLocalTransaction(String transId) {
        BaseLocalTransaction baseLocalTransaction;
        try {
            baseLocalTransaction = localTransactionConstructor.newInstance(transId, this);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        BaseLocalTransaction.addLocalTransaction(baseLocalTransaction);
        return baseLocalTransaction;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }


    private static Constructor<? extends BaseLocalTransaction> localTransactionConstructor;

    static {
        try {
            Class<? extends BaseLocalTransaction> localTransactionClass = Reflection.getNonAbstractSubclasses(BaseLocalTransaction.class).iterator().next();
            localTransactionConstructor = localTransactionClass.getConstructor(String.class, XianConnection.class);
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

}
