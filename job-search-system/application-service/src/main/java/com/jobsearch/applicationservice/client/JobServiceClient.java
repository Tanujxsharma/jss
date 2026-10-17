package com.jobsearch.applicationservice.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class JobServiceClient {

    private final WebClient webClient;

    public JobServiceClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://job-service").build();
    }

    public boolean validateJobExists(Long jobId) {
        try {
            Boolean exists = webClient.get()
                    .uri("/api/jobs/{jobId}", jobId)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, response -> {
                        if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
                            return Mono.empty();
                        }
                        return response.createException();
                    })
                    .toBodilessEntity()
                    .map(response -> response.getStatusCode().is2xxSuccessful())
                    .defaultIfEmpty(false)
                    .onErrorReturn(false)
                    .block();
            return Boolean.TRUE.equals(exists);
        } catch (WebClientResponseException.NotFound e) {
            log.warn("Job not found with id: {}", jobId);
            return false;
        } catch (Exception e) {
            log.error("Error validating job with id {}: {}", jobId, e.getMessage());
            return false;
        }
    }
}
