package com.jobsearch.basketservice.service;

import com.jobsearch.basketservice.dto.BasketRequest;
import com.jobsearch.basketservice.dto.BasketResponse;

import java.util.List;

public interface BasketService {

    BasketResponse addToBasket(BasketRequest request, Long authenticatedUserId);

    List<BasketResponse> getBasketBySeeker(Long jobSeekerId, Long authenticatedUserId);

    void removeFromBasket(Long basketId, Long authenticatedUserId);

    void removeByJobAndSeeker(Long jobSeekerId, Long jobId, Long authenticatedUserId);
}
