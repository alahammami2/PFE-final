package com.volleyball.adminrequestservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application principale du microservice admin-request-service
 * Gestion des demandes administratives pour équipe de volleyball
 */
@SpringBootApplication
public class AdminRequestServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminRequestServiceApplication.class, args);
    }
}
