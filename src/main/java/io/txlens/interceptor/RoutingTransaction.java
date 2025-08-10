package io.txlens.interceptor;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

public class RoutingTransaction {

    private TransactionStatus txStatus;
    private PlatformTransactionManager txManager;

    public RoutingTransaction() {}

    public RoutingTransaction(TransactionStatus txStatus, PlatformTransactionManager txManager) {
        this.txStatus = txStatus;
        this.txManager = txManager;
    }

    public TransactionStatus getTxStatus() {
        return txStatus;
    }

    public void setTxStatus(TransactionStatus txStatus) {
        this.txStatus = txStatus;
    }

    public PlatformTransactionManager getTxManager() {
        return txManager;
    }

    public void setTxManager(PlatformTransactionManager transactionManager) {
        this.txManager = transactionManager;
    }


}
