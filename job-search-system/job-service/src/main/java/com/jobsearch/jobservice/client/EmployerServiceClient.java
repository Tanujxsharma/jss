package com.jobsearch.jobservice.client;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class EmployerServiceClient {

    private final WebClient webClient;

    public EmployerServiceClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://employer-service").build();
    }

    public Optional<Long> getEmployerOwner(Long employerId, String authorization) {
        return webClient.get()
                .uri("/api/employers/{id}", employerId)
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(EmployerResponse.class).map(EmployerResponse::getUserId);
                    }
                    if (response.statusCode().value() == 404) {
                        return response.releaseBody().then(Mono.empty());
                    }
                    return response.createException().flatMap(error -> Mono.<Long>error(error));
                })
                .blockOptional();
    }

    private static class EmployerResponse {
        private Long userId;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }
    }
}
