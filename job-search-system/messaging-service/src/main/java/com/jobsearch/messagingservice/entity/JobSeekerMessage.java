package com.jobsearch.messagingservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "job_seeker_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobSeekerMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long messageId;

    @Column(name = "employer_id", nullable = false)
    private Long employerId;

    @Column(name = "job_seeker_id", nullable = false)
    private Long jobSeekerId;

    @Column(name = "job_id", nullable = false)
    private Long jobId;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "message_description", columnDefinition = "TEXT", nullable = false)
    private String messageDescription;

    @CreationTimestamp
    @Column(name = "sent_date", updatable = false)
    private LocalDateTime sentDate;
}
