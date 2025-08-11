package io.txlens.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "io.txlens")
public class DemoApplication implements CommandLineRunner {

    private final DemoService demoService;

    public DemoApplication(DemoService demoService) {
        this.demoService = demoService;
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("== Prueba TxLens con PostgreSQL ==");
        demoService.listProducts();
        demoService.addProduct();
    }
}
