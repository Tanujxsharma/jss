package com.jobsearch.basketservice.service.impl;

import com.jobsearch.basketservice.dto.BasketRequest;
import com.jobsearch.basketservice.dto.BasketResponse;
import com.jobsearch.basketservice.entity.JobBasket;
import com.jobsearch.basketservice.exception.BasketItemNotFoundException;
import com.jobsearch.basketservice.exception.DuplicateBasketException;
import com.jobsearch.basketservice.repository.JobBasketRepository;
import com.jobsearch.basketservice.service.BasketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasketServiceImpl implements BasketService {

    private final JobBasketRepository basketRepository;

    @Override
    @Transactional
    public BasketResponse addToBasket(BasketRequest request, Long authenticatedUserId) {
        log.info("Adding job id {} to basket for job seeker id {}", request.getJobId(), request.getJobSeekerId());

        boolean exists = basketRepository.existsByJobSeekerIdAndJobId(
                request.getJobSeekerId(), request.getJobId());
        if (exists) {
            throw new DuplicateBasketException(
                    "Job with ID " + request.getJobId() + " is already in the basket for job seeker ID " + request.getJobSeekerId());
        }

        JobBasket jobBasket = JobBasket.builder()
                .jobSeekerId(request.getJobSeekerId())
                .ownerUserId(authenticatedUserId)
                .jobId(request.getJobId())
                .build();

        JobBasket saved = basketRepository.save(jobBasket);
        log.info("Successfully added job basket item with ID {}", saved.getBasketId());
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BasketResponse> getBasketBySeeker(Long jobSeekerId, Long authenticatedUserId) {
        log.info("Fetching basket items for job seeker id {}", jobSeekerId);
        return basketRepository.findByOwnerUserId(authenticatedUserId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public void removeFromBasket(Long basketId, Long authenticatedUserId) {
        log.info("Removing basket item with ID {}", basketId);
        JobBasket jobBasket = basketRepository.findByBasketIdAndOwnerUserId(basketId, authenticatedUserId)
                .orElseThrow(() -> new BasketItemNotFoundException(
                        "Basket item not found with id: " + basketId));
        basketRepository.delete(jobBasket);
        log.info("Successfully removed basket item with ID {}", basketId);
    }

    @Override
    @Transactional
    public void removeByJobAndSeeker(Long jobSeekerId, Long jobId, Long authenticatedUserId) {
        log.info("Removing basket item for seeker id {} and job id {}", jobSeekerId, jobId);
        JobBasket jobBasket = basketRepository.findByJobSeekerIdAndJobIdAndOwnerUserId(
                        jobSeekerId, jobId, authenticatedUserId)
                .orElseThrow(() -> new BasketItemNotFoundException(
                        "Basket item not found for seeker ID " + jobSeekerId + " and job ID " + jobId));
        basketRepository.delete(jobBasket);
        log.info("Successfully removed basket item for seeker id {} and job id {}", jobSeekerId, jobId);
    }

    private BasketResponse mapToResponse(JobBasket entity) {
        return BasketResponse.builder()
                .basketId(entity.getBasketId())
                .jobSeekerId(entity.getJobSeekerId())
                .jobId(entity.getJobId())
                .addedDate(entity.getAddedDate())
                .build();
    }
}
