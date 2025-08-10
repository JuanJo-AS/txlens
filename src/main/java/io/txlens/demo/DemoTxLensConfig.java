package io.txlens.demo;

import io.txlens.config.TxLensConfig;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;

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

public class DemoTxLensConfig implements TxLensConfig {

    private static final String URL = "jdbc:postgresql://localhost:5432/platzi-market";
    private final DataSource readDataSource;
    private final DataSource writeDataSource;

    public DemoTxLensConfig() {
        BasicDataSource readDs = new BasicDataSource();
        readDs.setUrl(URL);
        readDs.setUsername("postgres");
        readDs.setPassword("root");
        this.readDataSource = readDs;

        BasicDataSource writeDs = new BasicDataSource();
        writeDs.setUrl(URL);
        writeDs.setUsername("postgres");
        writeDs.setPassword("root");
        this.writeDataSource = writeDs;
    }

    @Override
    public DataSource getReadDataSource() {
        return readDataSource;
    }

    @Override
    public DataSource getWriteDataSource() {
        return writeDataSource;
    }
}
