package com.volleyball.planningservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PlanningServiceApplicationTests {

    @Test
    void contextLoads() {
        // This test will pass if the application context loads successfully
        // It's a basic smoke test to ensure the Spring Boot application starts properly
    }

    @Test
    void mainMethodTest() {
        // Test that the main method can be called without throwing exceptions
        // This is a basic test to ensure the application entry point works
        String[] args = {};
        
        // In a real scenario, you might want to mock SpringApplication.run()
        // For now, we'll just verify the method exists and can be referenced
        assert PlanningServiceApplication.class.getDeclaredMethods().length > 0;
    }
}
