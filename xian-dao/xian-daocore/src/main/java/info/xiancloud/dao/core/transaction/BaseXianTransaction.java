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
    final protected TransactionStatus transactionStatus = new TransactionStatus();

    protected static class TransactionStatus {
        private boolean begun = false;
        private boolean rollbacked = false;

        public boolean isBegun() {
            return begun;
        }

        public TransactionStatus setBegun(boolean begun) {
            this.begun = begun;
            return this;
        }

        public boolean isRollbacked() {
            return rollbacked;
        }

        public TransactionStatus setRollbacked(boolean rollbacked) {
            this.rollbacked = rollbacked;
            return this;
        }
    }

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

    @Override
    public boolean isBegun() {
        return transactionStatus.isBegun();
    }

    public void setBegun(boolean begun) {
        transactionStatus.setBegun(begun);
    }

    @Override
    public boolean isRollbacked() {
        return transactionStatus.isRollbacked();
    }
}
