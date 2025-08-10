package io.txlens.interceptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionStatus;

class RoutingTransactionManagerTest {

    @Test
    void testReadOnlyDelegation() {
        DataSourceTransactionManager readTxManager = mockManager();
        DataSourceTransactionManager writeTxManager = mockManager();
        RoutingTransactionManager routingTxManager =
                new RoutingTransactionManager(readTxManager, writeTxManager);

        TransactionDefinition definition = mockDefinition(true);

        RoutingTransaction routingTx = (RoutingTransaction) routingTxManager.doGetTransaction();
        routingTxManager.doBegin(routingTx, definition);

        verifyManagerAndTransactionInvocation(readTxManager, routingTx, definition);
    }

    @Test
    void testWriteDelegation() {
        DataSourceTransactionManager readTxManager = mockManager();
        DataSourceTransactionManager writeTxManager = mockManager();
        RoutingTransactionManager routingTxManager =
                new RoutingTransactionManager(readTxManager, writeTxManager);

        TransactionDefinition definition = mockDefinition(false);

        RoutingTransaction routingTx = (RoutingTransaction) routingTxManager.doGetTransaction();
        routingTxManager.doBegin(routingTx, definition);

        verifyManagerAndTransactionInvocation(writeTxManager, routingTx, definition);
    }

    private TransactionDefinition mockDefinition(boolean readOnly) {
        TransactionDefinition definition = mock(TransactionDefinition.class);
        when(definition.isReadOnly()).thenReturn(readOnly);
        return definition;
    }

    private void verifyManagerAndTransactionInvocation(DataSourceTransactionManager txManager,
            RoutingTransaction routingTx, TransactionDefinition definition) {
        assertEquals(txManager, routingTx.getTxManager());
        verify(txManager).getTransaction(definition);
    }

    // TODO: does this method has to be tested with readTxManager?
    @Test
    void testCommitDelegation() {
        DataSourceTransactionManager readTxManager = mockManager();
        DataSourceTransactionManager writeTxManager = mockManager();
        RoutingTransactionManager routingTxManager =
                new RoutingTransactionManager(readTxManager, writeTxManager);

        TransactionStatus txStatus = mock(TransactionStatus.class);

        RoutingTransaction routingTx = new RoutingTransaction(txStatus, writeTxManager);

        DefaultTransactionStatus status = mockStatus(routingTx);

        routingTxManager.doCommit(status);

        verify(writeTxManager).commit(txStatus);
    }

    // TODO: does this method has to be tested with readTxManager?
    @Test
    void testRollbackDelegation() {
        DataSourceTransactionManager readTxManager = mockManager();
        DataSourceTransactionManager writeTxManager = mockManager();
        RoutingTransactionManager routingTxManager =
                new RoutingTransactionManager(readTxManager, writeTxManager);

        TransactionStatus txStatus = mock(TransactionStatus.class);

        RoutingTransaction routingTx = new RoutingTransaction(txStatus, readTxManager);

        DefaultTransactionStatus status = mockStatus(routingTx);

        routingTxManager.doRollback(status);

        verify(readTxManager).rollback(txStatus);
    }

    private DataSourceTransactionManager mockManager() {
        return mock(DataSourceTransactionManager.class);
    }

    private DefaultTransactionStatus mockStatus(RoutingTransaction routingTx) {
        DefaultTransactionStatus status = mock(DefaultTransactionStatus.class);
        when(status.getTransaction()).thenReturn(routingTx);
        return status;
    }
}
