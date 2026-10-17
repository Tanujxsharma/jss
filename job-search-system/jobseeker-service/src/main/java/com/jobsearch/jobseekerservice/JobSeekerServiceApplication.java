package com.jobsearch.jobseekerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class JobSeekerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobSeekerServiceApplication.class, args);
    }
}
