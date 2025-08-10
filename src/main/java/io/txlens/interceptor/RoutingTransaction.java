package io.txlens.interceptor;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

public class RoutingTransaction {

    private TransactionStatus txStatus;
    private PlatformTransactionManager transactionManager;

    public RoutingTransaction() {}

    public RoutingTransaction(TransactionStatus txStatus,
            PlatformTransactionManager transactionManager) {
        this.txStatus = txStatus;
        this.transactionManager = transactionManager;
    }

    public TransactionStatus getTxStatus() {
        return txStatus;
    }

    public void setTxStatus(TransactionStatus txStatus) {
        this.txStatus = txStatus;
    }

    public PlatformTransactionManager getTransactionManager() {
        return transactionManager;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }


}
