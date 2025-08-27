package com.volleyball.chatbotgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ChatbotGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChatbotGatewayApplication.class, args);
    }
}
