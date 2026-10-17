package com.jobsearch.jobservice.repository;

import com.jobsearch.jobservice.entity.Job;
import com.jobsearch.jobservice.enums.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByEmployerId(Long employerId);

    List<Job> findByStatus(JobStatus status);

    @Query("SELECT j FROM Job j WHERE " +
           "(:title IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:skills IS NULL OR LOWER(j.skillsRequired) LIKE LOWER(CONCAT('%', :skills, '%'))) AND " +
           "(:company IS NULL OR LOWER(j.companyName) LIKE LOWER(CONCAT('%', :company, '%'))) AND " +
           "j.status = 'ACTIVE'")
    List<Job> searchJobs(@Param("title") String title,
                         @Param("location") String location,
                         @Param("skills") String skills,
                         @Param("company") String company);
}
