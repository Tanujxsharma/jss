package com.jobsearch.basketservice.repository;

import com.jobsearch.basketservice.entity.JobBasket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobBasketRepository extends JpaRepository<JobBasket, Long> {

    List<JobBasket> findByJobSeekerId(Long jobSeekerId);

    List<JobBasket> findByOwnerUserId(Long ownerUserId);

    Optional<JobBasket> findByBasketIdAndOwnerUserId(Long basketId, Long ownerUserId);

    boolean existsByJobSeekerIdAndJobId(Long jobSeekerId, Long jobId);

    Optional<JobBasket> findByJobSeekerIdAndJobId(Long jobSeekerId, Long jobId);

    Optional<JobBasket> findByJobSeekerIdAndJobIdAndOwnerUserId(Long jobSeekerId, Long jobId, Long ownerUserId);

    void deleteByJobSeekerIdAndJobId(Long jobSeekerId, Long jobId);
}
