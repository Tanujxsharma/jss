package com.jobsearch.jobseekerservice.repository;

import com.jobsearch.jobseekerservice.entity.JobSeeker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobSeekerRepository extends JpaRepository<JobSeeker, Long> {

    Optional<JobSeeker> findByUserId(Long userId);

    boolean existsByEmail(String email);

    boolean existsByUserId(Long userId);

    @Query("SELECT js FROM JobSeeker js WHERE " +
           "(:skills IS NULL OR LOWER(js.skills) LIKE LOWER(CONCAT('%', :skills, '%'))) AND " +
           "(:location IS NULL OR LOWER(js.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:minExperience IS NULL OR js.experienceYears >= :minExperience)")
    List<JobSeeker> searchCandidates(
            @Param("skills") String skills,
            @Param("location") String location,
            @Param("minExperience") BigDecimal minExperience
    );
}
