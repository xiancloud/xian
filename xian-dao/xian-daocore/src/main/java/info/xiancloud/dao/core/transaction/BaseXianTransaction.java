package info.xiancloud.dao.core.transaction;

import info.xiancloud.dao.core.connection.XianConnection;

import java.util.Date;

/**
 * base xian transaction
 *
 * @author happyyangyuan
 */
public abstract class BaseXianTransaction implements XianTransaction {

    final protected XianConnection connection;
    final protected String transactionId;
    final protected Date createDate = new Date();

    public BaseXianTransaction(String transactionId, XianConnection xianConnection) {
        connection = xianConnection;
        this.transactionId = transactionId;
    }

    @Override
    public XianConnection getConnection() {
        return connection;
    }

    @Override
    public String getTransactionId() {
        return transactionId;
    }

    @Override
    public Date getCreateDate() {
        return createDate;
    }
}
