package io.txlens.demo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.springframework.stereotype.Repository;

@Repository
public class DemoDAO {

    private final DataSource dataSource;

    Logger logger = Logger.getLogger(getClass().getName());

    public DemoDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insert(String message) {
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps =
                        conn.prepareStatement("INSERT INTO test_txlens (mensaje) VALUES (?)")) {
            ps.setString(1, message);
            ps.executeUpdate();
        } catch (Exception e) {
            logger.severe("Error inserting data into the database: " + e.getMessage());
        }
    }

    public void list() {
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT id, mensaje FROM test_txlens");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String summary = String.format("ID: %d - Mensaje: %s%n", rs.getInt("id"),
                        rs.getString("mensaje"));
                logger.info(summary);
            }
        } catch (Exception e) {
            logger.severe("Error reading database: " + e.getMessage());
        }
    }
}
