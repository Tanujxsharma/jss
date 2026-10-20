package com.jobsearch.applicationservice.repository;

import com.jobsearch.applicationservice.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByJobId(Long jobId);

    List<Application> findByJobIdAndEmployerUserId(Long jobId, Long employerUserId);

    List<Application> findByJobSeekerId(Long jobSeekerId);

    List<Application> findByJobSeekerUserId(Long jobSeekerUserId);

    Optional<Application> findByJobIdAndJobSeekerId(Long jobId, Long jobSeekerId);

    boolean existsByJobIdAndJobSeekerId(Long jobId, Long jobSeekerId);

    List<Application> findByEmployerId(Long employerId);

    List<Application> findByEmployerIdAndEmployerUserId(Long employerId, Long employerUserId);
}
