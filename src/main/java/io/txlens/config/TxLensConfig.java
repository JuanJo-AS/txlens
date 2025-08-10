package io.txlens.config;

import javax.sql.DataSource;

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

public interface TxLensConfig {

    DataSource getReadDataSource();
    
    DataSource getWriteDataSource();
    
    default DataSource getDataSourceByName(String name) {
        throw new UnsupportedOperationException("There is no dataSource with name: " + name);
    }
}
