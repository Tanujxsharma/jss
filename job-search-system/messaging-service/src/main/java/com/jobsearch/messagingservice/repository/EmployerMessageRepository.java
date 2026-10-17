package com.jobsearch.messagingservice.repository;

import com.jobsearch.messagingservice.entity.EmployerMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployerMessageRepository extends JpaRepository<EmployerMessage, Long> {

    List<EmployerMessage> findByEmployerId(Long employerId);

    List<EmployerMessage> findByJobSeekerIdAndEmployerId(Long jobSeekerId, Long employerId);

    List<EmployerMessage> findByJobId(Long jobId);

    List<EmployerMessage> findByJobIdAndJobSeekerIdAndEmployerIdOrderBySentDateAsc(
            Long jobId, Long jobSeekerId, Long employerId);
}
