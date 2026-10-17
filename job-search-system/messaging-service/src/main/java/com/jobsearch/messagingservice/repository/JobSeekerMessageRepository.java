package com.jobsearch.messagingservice.repository;

import com.jobsearch.messagingservice.entity.JobSeekerMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobSeekerMessageRepository extends JpaRepository<JobSeekerMessage, Long> {

    List<JobSeekerMessage> findByJobSeekerId(Long jobSeekerId);

    List<JobSeekerMessage> findByJobSeekerIdAndEmployerId(Long jobSeekerId, Long employerId);

    List<JobSeekerMessage> findByJobId(Long jobId);

    List<JobSeekerMessage> findByJobIdAndJobSeekerIdAndEmployerIdOrderBySentDateAsc(
            Long jobId, Long jobSeekerId, Long employerId);
}
