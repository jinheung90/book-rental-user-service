package com.example.project;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@OpenAPIDefinition(servers = {
        @Server(url = "https://book-service-prod.jin900920.com/api", description = "main"),
        @Server(url = "http://localhost:8080/api", description = "local"),
        @Server(url = "http://book-service-prod.jin900920.com:8080/api", description = "deploy test")
})
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT30S")
public class MainApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainApiApplication.class, args);
    }
}
