package io.txlens.interceptor;

import io.txlens.annotations.TxAnotations;
import io.txlens.annotations.TxRead;
import io.txlens.annotations.TxWrite;
import io.txlens.config.TxLensConfig;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

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
    private final TxLensConfig config;
    private boolean isReadOnly;

    private static final ThreadLocal<Connection> currentConnection = new ThreadLocal<>();

    public TxLensInterceptor(Object target, TxLensConfig config) {
        this.target = target;
        this.config = config;
    }

    @SuppressWarnings("unchecked")
    public static <T> T createProxy(T target, TxLensConfig config, Class<T> iface) {
        return (T)
                Proxy.newProxyInstance(
                        iface.getClassLoader(),
                        new Class[] {iface},
                        new TxLensInterceptor(target, config));
    }

    public static Connection getCurrentConnection() {
        return currentConnection.get();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        TxRead txRead = method.getAnnotation(TxRead.class);
        TxWrite txWrite = method.getAnnotation(TxWrite.class);

        TxAnotations annotations = new TxAnotations(txRead, txWrite);

        if (annotations.hasNoTxAnnotation()) {
            return method.invoke(target, args);
        }

        DataSource dataSource = getDataSourceAndSetIsReadOnly(txRead);

        return executeWithConnection(dataSource, method, args);
    }

    private DataSource getDataSourceAndSetIsReadOnly(TxRead txRead) {
        if (txRead != null) {
            isReadOnly = true;
            return config.getReadDataSource();
        } else {
            return config.getWriteDataSource();
        }
    }

    private Object executeWithConnection(DataSource ds, Method method, Object[] args)
            throws Throwable {
        Connection connection = null;
        try {
            connection = ds.getConnection();

            if (isReadOnly) {
                connection.setReadOnly(true);
                connection.setAutoCommit(true);
            } else {
                connection.setAutoCommit(false);
            }

            currentConnection.set(connection);

            Object result = method.invoke(target, args);

            if (!isReadOnly) {
                connection.commit();
            }

            return result;
        } catch (Exception e) {
            if (!isReadOnly && connection != null) {
                connection.rollback();
            }
            throw e.getCause() != null ? e.getCause() : e;
        } finally {
            closeConnection(connection);
        }
    }

    private void closeConnection(Connection connection) throws SQLException {
        currentConnection.remove();
        if (connection != null) {
            connection.close();
        }
    }
}
