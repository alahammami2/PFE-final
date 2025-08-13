package com.volleyball.medicalservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application principale du microservice medical-service
 * Gestion des données médicales et rendez-vous pour l'équipe de volleyball
 */
@SpringBootApplication
public class MedicalServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedicalServiceApplication.class, args);
    }
}
