package io.txlens.interceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import io.txlens.annotations.TxRead;
import io.txlens.annotations.TxWrite;
import io.txlens.config.TxLensConfig;

/*
 * Copyright 2025 Juan José Andrade Sánchez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class TxLensInterceptor implements InvocationHandler {

    private final Object target;
    private final TxLensConfig config;

    private static final ThreadLocal<Connection> currentConnection = new ThreadLocal<>();

    public TxLensInterceptor(Object target, TxLensConfig config) {
        this.target = target;
        this.config = config;
    }

    @SuppressWarnings("unchecked")
    public static <T> T createProxy(T target, TxLensConfig config, Class<T> iface) {
        return (T) Proxy.newProxyInstance(iface.getClassLoader(), new Class[]{iface}, new TxLensInterceptor(target, config));
    }

    public static Connection getCurrentConnection() {
        return currentConnection.get();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        TxRead txRead = method.getAnnotation(TxRead.class);
        TxWrite txWrite = method.getAnnotation(TxWrite.class);

        if(isNotTxLensAnnotation(txRead, txWrite)) {
            return method.invoke(target, args);
        }

        DataSource dataSource;
        if(txRead != null) {
            dataSource = config.getReadDataSource();
        } else {
            dataSource = config.getWriteDataSource();
        }

        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            return setCurrentConnextionAndExecuteAndCommit(connection, method, args);
        } catch (Throwable t) {
            throw rollbackWithThrowable(connection, t);
        } finally {
            closeConnection(connection);
        }
    }

    public boolean isNotTxLensAnnotation(TxRead read, TxWrite write) {
        return read == null && write == null;
    }

    public Object setCurrentConnextionAndExecuteAndCommit(Connection connection, Method method, Object[] args) throws Throwable {
        connection.setAutoCommit(false);

        currentConnection.set(connection);

        Object result = method.invoke(target, args);
        connection.commit();

        return result;
    }

    public Throwable rollbackWithThrowable(Connection connection, Throwable t) {
        if (connection != null) {   
            try {
                connection.rollback();
            } catch (SQLException e) {
                // TODO improve this log or set a return value
                e.printStackTrace();
            }
        }
        return t.getCause() != null ? t.getCause() : t;
    }

    public void closeConnection(Connection connection) {
        if (connection != null) {   
            try {
                connection.close();
            } catch (SQLException e) {
                // TODO improve this log or set a return value
                e.printStackTrace();
            }
        }
    }

}
