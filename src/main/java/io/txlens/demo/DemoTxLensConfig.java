package io.txlens.demo;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

import io.txlens.config.TxLensConfig;

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
