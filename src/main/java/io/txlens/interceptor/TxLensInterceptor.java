package io.txlens.interceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import io.txlens.annotations.Propagation;
import io.txlens.annotations.TxAnotations;
import io.txlens.annotations.TxRead;
import io.txlens.annotations.TxWrite;

/*
 * Copyright 2025 Juan José Andrade Sánchez
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

public class TxLensInterceptor implements InvocationHandler {

    private final Object target;
    private final PlatformTransactionManager transactionManager;

    public TxLensInterceptor(Object target, PlatformTransactionManager transactionManager) {
        this.target = target;
        this.transactionManager = transactionManager;
    }

    @SuppressWarnings("unchecked")
    public static <T> T createProxy(T target, PlatformTransactionManager transactionManager,
            Class<T> iface) {
        return (T) Proxy.newProxyInstance(iface.getClassLoader(), new Class[] {iface},
                new TxLensInterceptor(target, transactionManager));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        TxRead txRead = method.getAnnotation(TxRead.class);
        TxWrite txWrite = method.getAnnotation(TxWrite.class);

        TxAnotations annotations = new TxAnotations(txRead, txWrite);

        if (annotations.hasNoTxAnnotation()) {
            return method.invoke(target, args);
        }

        Propagation propagation;
        boolean readOnly;

        if (txRead != null) {
            // propagation = txRead.propagation();
            readOnly = true;
        } else {
            // propagation = txWrite.propagation();
            readOnly = false;
        }

        TransactionTemplate txTemplate = new TransactionTemplate(transactionManager);
        // txTemplate.setPropagationBehavior(propagation.value);
        txTemplate.setReadOnly(readOnly);
        return txTemplate.execute(status -> {
            try {
                return method.invoke(target, args);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

}
