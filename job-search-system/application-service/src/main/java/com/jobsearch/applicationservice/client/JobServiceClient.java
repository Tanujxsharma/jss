package com.jobsearch.applicationservice.client;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class JobServiceClient {

    private final WebClient webClient;

    public JobServiceClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://job-service").build();
    }

    public Optional<JobServiceResponse> getJob(Long jobId, String authorization) {
        return webClient.get()
                .uri("/api/jobs/{jobId}", jobId)
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(JobServiceResponse.class);
                    }
                    if (response.statusCode().value() == 404) {
                        return response.releaseBody().then(Mono.empty());
                    }
                    return response.createException().flatMap(error -> Mono.<JobServiceResponse>error(error));
                })
                .blockOptional();
    }
}
