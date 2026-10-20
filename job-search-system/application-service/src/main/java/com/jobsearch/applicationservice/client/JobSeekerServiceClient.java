package com.jobsearch.applicationservice.client;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class JobSeekerServiceClient {

    private final WebClient webClient;

    public JobSeekerServiceClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://jobseeker-service").build();
    }

    public Optional<JobSeekerServiceResponse> getJobSeeker(Long jobSeekerId, String authorization) {
        return webClient.get()
                    .uri("/api/jobseekers/{id}", jobSeekerId)
                    .header(HttpHeaders.AUTHORIZATION, authorization)
                    .exchangeToMono(response -> {
                        if (response.statusCode().is2xxSuccessful()) {
                            return response.bodyToMono(JobSeekerServiceResponse.class);
                        }
                        if (response.statusCode().value() == 404) {
                            return response.releaseBody().then(Mono.empty());
                        }
                        return response.createException().flatMap(error -> Mono.<JobSeekerServiceResponse>error(error));
                    })
                    .blockOptional();
    }
}
