package com.volleyball.performanceservice;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Application principale du microservice Performance Service
 * Gère les statistiques des joueurs et les absences
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class PerformanceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PerformanceServiceApplication.class, args);
        System.out.println(" Performance Service démarré avec succès !");
        System.out.println(" Service de gestion des performances et absences");
        System.out.println(" Accessible sur: http://localhost:8083/api/performance");
    }
}
