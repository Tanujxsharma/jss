@echo off
echo ============================================================
echo   Job Search System - Service Startup Script (Windows)
echo ============================================================
echo.
echo Starting services in the correct order...
echo Make sure MySQL is running before proceeding!
echo.

set BASE_DIR=%~dp0

:: ---- 1. Eureka Server ----
echo [1/8] Starting Eureka Server on port 8761...
start "Eureka Server" cmd /k "cd /d %BASE_DIR%eureka-server && mvn spring-boot:run"
echo Waiting 20 seconds for Eureka to be ready...
timeout /t 20 /nobreak > NUL

:: ---- 2. API Gateway ----
echo [2/8] Starting API Gateway on port 8080...
start "API Gateway" cmd /k "cd /d %BASE_DIR%api-gateway && mvn spring-boot:run"
echo Waiting 15 seconds...
timeout /t 15 /nobreak > NUL

:: ---- 3. Auth Service ----
echo [3/8] Starting Auth Service on port 8081...
start "Auth Service" cmd /k "cd /d %BASE_DIR%auth-service && mvn spring-boot:run"
timeout /t 15 /nobreak > NUL

:: ---- 4. Employer Service ----
echo [4/8] Starting Employer Service on port 8082...
start "Employer Service" cmd /k "cd /d %BASE_DIR%employer-service && mvn spring-boot:run"
timeout /t 10 /nobreak > NUL

:: ---- 5. Job Service ----
echo [5/8] Starting Job Service on port 8083...
start "Job Service" cmd /k "cd /d %BASE_DIR%job-service && mvn spring-boot:run"
timeout /t 10 /nobreak > NUL

:: ---- 6. JobSeeker Service ----
echo [6/8] Starting JobSeeker Service on port 8084...
start "JobSeeker Service" cmd /k "cd /d %BASE_DIR%jobseeker-service && mvn spring-boot:run"
timeout /t 10 /nobreak > NUL

:: ---- 7. Application Service ----
echo [7/8] Starting Application Service on port 8085...
start "Application Service" cmd /k "cd /d %BASE_DIR%application-service && mvn spring-boot:run"
timeout /t 10 /nobreak > NUL

:: ---- 8. Basket Service ----
echo [8/8a] Starting Basket Service on port 8086...
start "Basket Service" cmd /k "cd /d %BASE_DIR%basket-service && mvn spring-boot:run"
timeout /t 10 /nobreak > NUL

:: ---- 9. Messaging Service ----
echo [8/8b] Starting Messaging Service on port 8087...
start "Messaging Service" cmd /k "cd /d %BASE_DIR%messaging-service && mvn spring-boot:run"

echo.
echo ============================================================
echo   All services starting! Check individual windows for logs.
echo.
echo   Eureka Dashboard:     http://localhost:8761
echo   API Gateway:          http://localhost:8080
echo   Auth Swagger:         http://localhost:8081/swagger-ui.html
echo   Employer Swagger:     http://localhost:8082/swagger-ui.html
echo   Job Swagger:          http://localhost:8083/swagger-ui.html
echo   JobSeeker Swagger:    http://localhost:8084/swagger-ui.html
echo   Application Swagger:  http://localhost:8085/swagger-ui.html
echo   Basket Swagger:       http://localhost:8086/swagger-ui.html
echo   Messaging Swagger:    http://localhost:8087/swagger-ui.html
echo ============================================================
echo.
pause
