package com.fluxfinanciers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
@SpringBootApplication
@EnableJpaAuditing


public class FluxFinanciers {
    public static void main(String[] args) {
        SpringApplication.run(FluxFinanciers.class, args);

        System.out.println("=================================================");
        System.out.println(" Application Flux Financiers démarrée !");
        System.out.println(" API disponible sur : http://localhost:8080/api");
        System.out.println(" Base de données : MySQL - flux_financiers");
        System.out.println("=================================================");
    }
    }

