package com.bankapp.payment;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PaymentServiceApplication {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure()
                .directory("/home/mico/.gemini/antigravity/scratch/banking-app")
                .ignoreIfMissing()
                .load();
        dotenv.entries().forEach(e -> System.setProperty(e.getKey(), e.getValue()));

        SpringApplication.run(PaymentServiceApplication.class, args);
    }
}
