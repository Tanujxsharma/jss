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
public class JobSeekerServiceClient {

    private final WebClient webClient;

    public JobSeekerServiceClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://jobseeker-service").build();
    }

    public boolean validateJobSeekerExists(Long jobSeekerId) {
        try {
            Boolean exists = webClient.get()
                    .uri("/api/jobseekers/{id}", jobSeekerId)
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
            log.warn("Job seeker not found with id: {}", jobSeekerId);
            return false;
        } catch (Exception e) {
            log.error("Error validating job seeker with id {}: {}", jobSeekerId, e.getMessage());
            return false;
        }
    }
}
