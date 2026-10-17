-- ============================================================
-- Job Search System - Database Initialization Script
-- Run this script once before starting any microservice.
-- MySQL 8.x required.
-- Usage: mysql -u root -p < init-databases.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS auth_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS employer_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS job_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS jobseeker_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS application_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS basket_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS messaging_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

SELECT 'All 7 databases created successfully!' AS status;
SHOW DATABASES LIKE '%_db';
