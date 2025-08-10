package io.txlens.interceptor;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.ResourceTransactionManager;

public class RoutingTransactionManager extends AbstractPlatformTransactionManager
        implements ResourceTransactionManager {

    DataSourceTransactionManager readTxManager;
    DataSourceTransactionManager writeTxManager;

    public RoutingTransactionManager(DataSourceTransactionManager readTxManager,
            DataSourceTransactionManager writeTxManager) {
        this.readTxManager = readTxManager;
        this.writeTxManager = writeTxManager;
    }

    private PlatformTransactionManager getTransactionManager(TransactionDefinition definition) {
        if (definition.isReadOnly()) {
            return readTxManager;
        } else {
            return writeTxManager;
        }
    }

    @Override
    protected Object doGetTransaction() throws TransactionException {
        return new RoutingTransaction();
    }

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition)
            throws TransactionException {
        PlatformTransactionManager txManager = getTransactionManager(definition);

        RoutingTransaction routingTx = (RoutingTransaction) transaction;
        routingTx.setTransactionManager(txManager);
        routingTx.setTxStatus(txManager.getTransaction(definition));
    }

    @Override
    protected void doCommit(DefaultTransactionStatus status) throws TransactionException {
        RoutingTransaction routingTx = (RoutingTransaction) status.getTransaction();
        PlatformTransactionManager txManager = routingTx.getTransactionManager();
        txManager.commit(routingTx.getTxStatus());
    }

    @Override
    protected void doRollback(DefaultTransactionStatus status) throws TransactionException {
        RoutingTransaction routingTx = (RoutingTransaction) status.getTransaction();
        PlatformTransactionManager txManager = routingTx.getTransactionManager();
        txManager.rollback(routingTx.getTxStatus());
    }

    @Override
    public Object getResourceFactory() {
        return this;
    }

}
