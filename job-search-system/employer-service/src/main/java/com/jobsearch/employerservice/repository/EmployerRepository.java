package com.jobsearch.employerservice.repository;

import com.jobsearch.employerservice.entity.Employer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployerRepository extends JpaRepository<Employer, Long> {

    Optional<Employer> findByUserId(Long userId);

    List<Employer> findByEmailContainingIgnoreCase(String email);

    boolean existsByEmail(String email);

    boolean existsByUserId(Long userId);
}
