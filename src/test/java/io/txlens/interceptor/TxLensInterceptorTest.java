package io.txlens.interceptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import java.lang.reflect.Method;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.transaction.PlatformTransactionManager;

class TxLensInterceptorTest {

    @ParameterizedTest
    @CsvSource({"readMethod, read", "writeMethod, write", "noTxMethod, no tx"})
    void testTransaction(String methodName, String expectedResult) throws Throwable {
        Service service = new Service();

        PlatformTransactionManager txManager = mock(PlatformTransactionManager.class);

        TxLensInterceptor interceptor = new TxLensInterceptor(service, txManager);

        Method method = Service.class.getMethod(methodName);
        Object result = interceptor.invoke(service, method, null);

        assertEquals(expectedResult, result);
    }
}
