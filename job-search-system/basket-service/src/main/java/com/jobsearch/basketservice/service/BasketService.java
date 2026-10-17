package com.jobsearch.basketservice.service;

import com.jobsearch.basketservice.dto.BasketRequest;
import com.jobsearch.basketservice.dto.BasketResponse;

import java.util.List;

public interface BasketService {

    BasketResponse addToBasket(BasketRequest request);

    List<BasketResponse> getBasketBySeeker(Long jobSeekerId);

    void removeFromBasket(Long basketId);

    void removeByJobAndSeeker(Long jobSeekerId, Long jobId);
}
