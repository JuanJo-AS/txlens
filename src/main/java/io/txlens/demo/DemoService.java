package io.txlens.demo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import io.txlens.annotations.TxRead;
import io.txlens.annotations.TxWrite;

@Service
public class DemoService {
    private final JdbcTemplate jdbc;

    public DemoService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @TxRead
    public void listProducts() {
        Integer result = jdbc.queryForObject("SELECT count(id) FROM test_txlens", Integer.class);
        System.out.println("text_txlens size: " + result);
    }

    @TxWrite
    public void addProduct() {
        jdbc.update("INSERT INTO test_txlens (mensaje) VALUES (?)", "addProduct test");
    }
}
