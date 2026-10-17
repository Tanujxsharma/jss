package com.jobsearch.employerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class EmployerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmployerServiceApplication.class, args);
    }
}
