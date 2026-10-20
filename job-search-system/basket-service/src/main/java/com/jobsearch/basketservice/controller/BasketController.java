package com.jobsearch.basketservice.controller;

import com.jobsearch.basketservice.dto.BasketRequest;
import com.jobsearch.basketservice.dto.BasketResponse;
import com.jobsearch.basketservice.service.BasketService;
import com.jobsearch.basketservice.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/api/basket", "/api/baskets"})
@RequiredArgsConstructor
@Tag(name = "Basket", description = "Endpoints for managing saved jobs in the seeker basket")
@SecurityRequirement(name = "bearerAuth")
public class BasketController {

    private final BasketService basketService;
    private final JwtUtil jwtUtil;

    @PostMapping
    @Operation(summary = "Add a job to basket", description = "Saves a job into the job seeker's basket. Returns 201 on success or 409 if already present.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Job added to basket successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "409", description = "Job already exists in basket"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<BasketResponse> addToBasket(
            @Valid @RequestBody BasketRequest request,
            @RequestHeader("Authorization") String authorization) {
        BasketResponse response = basketService.addToBasket(request, currentUserId(authorization));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{jobSeekerId}")
    @Operation(summary = "Get basket items by seeker ID", description = "Retrieves all saved jobs for the specified job seeker.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Basket items retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<BasketResponse>> getBasketBySeeker(
            @Parameter(description = "ID of the job seeker", required = true)
            @PathVariable Long jobSeekerId,
            @RequestHeader("Authorization") String authorization) {
        List<BasketResponse> responses = basketService.getBasketBySeeker(jobSeekerId, currentUserId(authorization));
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{basketId}")
    @Operation(summary = "Remove an item from basket by basket ID", description = "Deletes a specific basket entry by its unique basket ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Item removed from basket successfully"),
            @ApiResponse(responseCode = "404", description = "Basket item not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Void> removeFromBasket(
            @Parameter(description = "ID of the basket entry to delete", required = true)
            @PathVariable Long basketId,
            @RequestHeader("Authorization") String authorization) {
        basketService.removeFromBasket(basketId, currentUserId(authorization));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/seeker/{jobSeekerId}/job/{jobId}")
    @Operation(summary = "Remove a job from basket by seeker ID and job ID", description = "Deletes a basket item matching the job seeker ID and job ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Item removed from basket successfully"),
            @ApiResponse(responseCode = "404", description = "Basket item not found for seeker and job"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Void> removeByJobAndSeeker(
            @Parameter(description = "ID of the job seeker", required = true)
            @PathVariable Long jobSeekerId,
            @Parameter(description = "ID of the job to remove", required = true)
            @PathVariable Long jobId,
            @RequestHeader("Authorization") String authorization) {
        basketService.removeByJobAndSeeker(jobSeekerId, jobId, currentUserId(authorization));
        return ResponseEntity.noContent().build();
    }

    private Long currentUserId(String authorization) {
        return jwtUtil.extractUserId(authorization.substring("Bearer ".length()));
    }
}
