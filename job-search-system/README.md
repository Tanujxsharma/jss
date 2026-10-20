# 🚀 Job Search System — Microservices-Based Job Portal

A complete microservices-based job portal built with **Java 17**, **Spring Boot 3.2.5**, **Spring Cloud 2023.0.1**, **MySQL**, and **JWT authentication**. Employers can post jobs, job seekers can search and apply, both parties can message each other, and the full flow is demonstrable via Swagger UI.

---

## 📐 Architecture

```
┌─────────────────────────────────────────────────────────┐
│                   SWAGGER UI / CLIENT                   │
└────────────────────────┬────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────┐
│              API GATEWAY  (port 8080)                   │
│         Routes, CORS, Load Balancing (lb://)            │
└──┬──────┬──────┬──────┬──────┬──────┬──────┬───────────┘
   │      │      │      │      │      │      │
   ▼      ▼      ▼      ▼      ▼      ▼      ▼
 Auth  Employer  Job  Seeker  App  Basket  Msg
 8081   8082   8083   8084   8085   8086   8087
   │      │      │      │      │      │      │
   │      │      │      │   ┌──┴──────┘      │
   │      │      │      │   │ WebClient calls │
   │      │      └──────┼───┘ (job-service,  │
   │      │             │     seeker-service) │
   │      │             │                    │
   ▼      ▼             ▼                    ▼
auth_db emp_db   job_db  seeker_db  app_db  basket_db  msg_db
                         (MySQL 8.x — one database per service)

                ┌─────────────────────┐
                │  EUREKA SERVER      │
                │  (port 8761)        │
                │  All services       │
                │  register here      │
                └─────────────────────┘
```

---

## 🛠️ Technology Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2.5 |
| Service Discovery | Netflix Eureka (Spring Cloud 2023.0.1) |
| API Gateway | Spring Cloud Gateway (WebFlux) |
| Security | Spring Security + JWT (JJWT 0.12.6) |
| Persistence | Spring Data JPA + Hibernate |
| Database | MySQL 8.x (one schema per service) |
| Validation | Jakarta Bean Validation (`@Valid`) |
| API Docs | SpringDoc OpenAPI 3 + Swagger UI 2.5.0 |
| Inter-service | WebClient (Spring WebFlux) with Eureka `lb://` |
| Testing | JUnit 5 + Mockito |
| Build | Maven 3.8+ |

---

## 📋 Prerequisites

- ☕ **Java 17** — `java -version` must show 17.x (`JAVA_HOME` set correctly)
- 📦 **Maven 3.8+** — `mvn -version`
- 🐬 **MySQL 8.x** — running locally on default port 3306
- 🌐 Ports **8761, 8080, 8081, 8082, 8083, 8084, 8085, 8086, 8087** must be free

---

## 🗄️ Database Setup

All databases are auto-created on first startup via `createDatabaseIfNotExist=true` in the JDBC URL. You can also create them manually:

```bash
mysql -u root -p < init-databases.sql
```

Or run the SQL directly:

```sql
CREATE DATABASE IF NOT EXISTS auth_db;
CREATE DATABASE IF NOT EXISTS employer_db;
CREATE DATABASE IF NOT EXISTS job_db;
CREATE DATABASE IF NOT EXISTS jobseeker_db;
CREATE DATABASE IF NOT EXISTS application_db;
CREATE DATABASE IF NOT EXISTS basket_db;
CREATE DATABASE IF NOT EXISTS messaging_db;
```

**Default credentials:** `username=root`, `password=root`

For local defaults and the required shared JWT secret, see [Environment Variables](#-environment-variables).

---

## 🚦 Startup Order (CRITICAL)

> Recommended dependency order. Eureka-based REST calls require the relevant downstream services to be registered and available before the corresponding operation is invoked.

| # | Service | Port | Wait Before Next |
|---|---|---|---|
| 1 | MySQL | 3306 | Must be fully running |
| 2 | `eureka-server` | 8761 | ~20 seconds |
| 3 | `api-gateway` | 8080 | ~15 seconds |
| 4 | `auth-service` | 8081 | ~15 seconds |
| 5 | `employer-service` | 8082 | ~10 seconds |
| 6 | `job-service` | 8083 | ~10 seconds |
| 7 | `jobseeker-service` | 8084 | ~10 seconds |
| 8 | `application-service` | 8085 | ~10 seconds |
| 9 | `basket-service` | 8086 | ~10 seconds |
| 10 | `messaging-service` | 8087 | — |

---

## ▶️ How to Build & Run

### Option A: One-Click Startup (Windows)

```bat
REM 1. Build everything
.\build-all.ps1

REM 2. Start all services in order (opens separate terminal per service)
.\start-all-services.bat
```

### Option B: Build with Tests

```powershell
.\build-all.ps1 -RunTests
```

### Option C: Run a Single Service (Development)

```bash
cd auth-service
mvn spring-boot:run
```

### Option D: Run All Manually (PowerShell — separate windows)

```powershell
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd eureka-server; mvn spring-boot:run"
Start-Sleep 20
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd api-gateway; mvn spring-boot:run"
Start-Sleep 15
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd auth-service; mvn spring-boot:run"
# ... repeat for each service
```

---

## 📖 Swagger UI URLs

Each service has its own interactive Swagger UI:

| Service | Swagger UI | API Docs |
|---|---|---|
| Auth Service | http://localhost:8081/swagger-ui.html | http://localhost:8081/v3/api-docs |
| Employer Service | http://localhost:8082/swagger-ui.html | http://localhost:8082/v3/api-docs |
| Job Service | http://localhost:8083/swagger-ui.html | http://localhost:8083/v3/api-docs |
| JobSeeker Service | http://localhost:8084/swagger-ui.html | http://localhost:8084/v3/api-docs |
| Application Service | http://localhost:8085/swagger-ui.html | http://localhost:8085/v3/api-docs |
| Basket Service | http://localhost:8086/swagger-ui.html | http://localhost:8086/v3/api-docs |
| Messaging Service | http://localhost:8087/swagger-ui.html | http://localhost:8087/v3/api-docs |
| Eureka Dashboard | http://localhost:8761 | — |

---

## 🔑 JWT Authentication in Swagger

> All endpoints (except `/api/auth/register` and `/api/auth/login`) require a Bearer token.

1. Open any service's Swagger UI (e.g., http://localhost:8083/swagger-ui.html)
2. Call **`POST /api/auth/register`** on Auth Service (port 8081) to create a user
3. Call **`POST /api/auth/login`** to receive the JWT token
4. Copy the `token` value from the response
5. In Swagger UI, click the **🔓 Authorize** button (top right)
6. In the `bearerAuth` field, enter: `Bearer <paste-token-here>`
7. Click **Authorize** → **Close**
8. All secured requests will now include the token automatically

---

## 🎬 End-to-End Demo Walkthrough (5–8 minutes)

### Step 1 — Register an Employer
**Auth Service** → `POST /api/auth/register`
```json
{
  "username": "techcorp",
  "email": "hr@techcorp.com",
  "password": "password123",
  "role": "EMPLOYER"
}
```

### Step 2 — Login as Employer
**Auth Service** → `POST /api/auth/login`
```json
{
  "email": "hr@techcorp.com",
  "password": "password123"
}
```
→ Copy the `token` value. Authorize in Swagger.

### Step 3 — Create Employer Profile
**Employer Service** → `POST /api/employers`
```json
{
  "userId": 1,
  "organizationName": "TechCorp Ltd",
  "email": "hr@techcorp.com",
  "contactNumber": "9876543210",
  "location": "Bangalore",
  "about": "Leading software company specializing in cloud solutions",
  "website": "https://techcorp.com"
}
```

### Step 4 — Post a Job
**Job Service** → `POST /api/jobs`
```json
{
  "employerId": 1,
  "title": "Senior Java Developer",
  "companyName": "TechCorp Ltd",
  "location": "Bangalore",
  "description": "Looking for an experienced Java developer with Spring Boot and microservices skills.",
  "skillsRequired": "Java, Spring Boot, MySQL, Microservices, Docker",
  "experienceRequired": 3.0,
  "salaryRange": "15-20 LPA",
  "jobType": "FULL_TIME"
}
```
📝 Note the `id` from the response (e.g., `1`).

### Step 5 — Register a Job Seeker
**Auth Service** → `POST /api/auth/register`
```json
{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123",
  "role": "JOB_SEEKER"
}
```
Login → get new token → re-Authorize in Swagger.

### Step 6 — Create Job Seeker Profile
**JobSeeker Service** → `POST /api/jobseekers`
```json
{
  "userId": 2,
  "fullName": "John Doe",
  "email": "john@example.com",
  "phone": "9123456789",
  "location": "Bangalore",
  "skills": "Java, Spring Boot, MySQL, REST APIs",
  "experienceYears": 4.0,
  "summary": "Experienced backend developer passionate about microservices architecture."
}
```

### Step 7 — Search Jobs
**Job Service** → `GET /api/jobs?skills=Java&location=Bangalore`

### Step 8 — Apply for the Job
**Application Service** → `POST /api/applications`
```json
{
  "jobId": 1,
  "jobSeekerId": 1,
  "employerId": 1,
  "coverLetter": "I am very interested in this Senior Java Developer position. With 4 years of Spring Boot experience, I am confident I can contribute immediately."
}
```
> ✅ This triggers inter-service validation: Application Service calls Job Service and JobSeeker Service via WebClient to verify both exist before saving.

### Step 9 — Employer Reviews Applications
**Application Service** → `GET /api/applications/job/1`

### Step 10 — Shortlist the Candidate
**Application Service** → `PATCH /api/applications/1/status`
```json
{
  "status": "SHORTLISTED"
}
```

### Step 11 — Job Seeker Checks Status
**Application Service** → `GET /api/applications/seeker/1`
→ See `"status": "SHORTLISTED"` ✅

### Step 12 — Send a Message (Employer → Seeker)
**Messaging Service** → `POST /api/messages/employer`
```json
{
  "employerId": 1,
  "jobSeekerId": 1,
  "jobId": 1,
  "subject": "Interview Invitation",
  "messageDescription": "Congratulations! We would like to invite you for a technical interview on Monday at 10 AM IST. Please confirm your availability."
}
```

### Step 13 — Save Job to Basket
**Basket Service** → `POST /api/basket`
```json
{
  "jobSeekerId": 1,
  "jobId": 1
}
```

### Step 14 — View Conversation
**Messaging Service** → `GET /api/messages/conversation?jobId=1&jobSeekerId=1&employerId=1`

---

## ❌ Validation Failure Examples

### Blank Job Title → 400 Bad Request
```json
POST /api/jobs
{ "title": "", "employerId": 1, "skillsRequired": "Java" }
```
**Response:**
```json
{
  "timestamp": "2026-10-17T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation Failed",
  "path": "/api/jobs"
}
```

### Invalid Email → 400
```json
POST /api/auth/register
{ "email": "not-an-email", "password": "pass123", "role": "EMPLOYER" }
```

### Duplicate Application → 409 Conflict
Applying to the same job twice returns **409 Conflict**.

### Missing Resource → 404
```
GET /api/jobs/9999
```
Returns **404 Not Found** with standardized error body — not a 500 stack trace.

### Negative Experience → 400
```json
{ "experienceRequired": -2.0 }
```
Rejected by `@DecimalMin("0.0")` validation.

---

## 🧪 Running Tests

```bash
# Test a specific service
cd auth-service && mvn test

# Test all services sequentially
cd auth-service        && mvn test && cd ..
cd employer-service    && mvn test && cd ..
cd job-service         && mvn test && cd ..
cd jobseeker-service   && mvn test && cd ..
cd application-service && mvn test && cd ..
cd basket-service      && mvn test && cd ..
cd messaging-service   && mvn test && cd ..
```

**Test coverage includes:**
- ✅ Service layer unit tests (Mockito mocks)
- ✅ Controller layer tests (`@WebMvcTest` + MockMvc)
- ✅ Positive scenarios (happy path)
- ✅ Negative scenarios (not found, duplicate, validation)
- ✅ Exception handling verification

---

## 🌳 Project Structure

```
job-search-system/
├── eureka-server/                  # Service registry (port 8761)
├── api-gateway/                    # API Gateway (port 8080)
├── auth-service/                   # JWT auth, register/login (port 8081)
│   └── src/main/java/com/jobsearch/authservice/
│       ├── entity/User.java
│       ├── security/JwtUtil.java
│       ├── service/impl/AuthServiceImpl.java
│       └── controller/AuthController.java
├── employer-service/               # Employer profiles (port 8082)
├── job-service/                    # Job CRUD & search (port 8083)
├── jobseeker-service/              # Candidate profiles + resume upload (port 8084)
│   └── uploads/resumes/            # Resume files stored here (auto-created)
├── application-service/            # Apply, shortlist, reject (port 8085)
│   └── client/
│       ├── JobServiceClient.java   # WebClient → job-service
│       └── JobSeekerServiceClient.java
├── basket-service/                 # Favourite jobs (port 8086)
├── messaging-service/              # Bidirectional messaging (port 8087)
├── init-databases.sql              # Creates all 7 MySQL schemas
├── start-all-services.bat          # Windows one-click startup
└── build-all.ps1                   # PowerShell build script
```

---

## ⚙️ Environment Variables

For local PowerShell sessions, configure the database credentials and generate one shared JWT secret before launching the service script:

```powershell
$env:SPRING_DATASOURCE_USERNAME = "root"
$env:SPRING_DATASOURCE_PASSWORD = "your-local-mysql-password"
$bytes = New-Object byte[] 32
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
$rng.GetBytes($bytes)
$env:JWT_SECRET = [Convert]::ToBase64String($bytes)
```

Keep this secret in the same shell session when you run `.\start-all-services.bat`; do not commit it. The services use it as a UTF-8 HMAC key, so keep the generated string identical across all services.

| Variable | Default | Used By |
|---|---|---|
| `SPRING_DATASOURCE_USERNAME` | `root` | All services |
| `SPRING_DATASOURCE_PASSWORD` | `root` | All services |
| `SPRING_DATASOURCE_URL` | Service-specific local MySQL URL | All data services |
| `JWT_SECRET` | Required; shared by all services (at least 32 characters) | Auth and protected services |
| `JWT_EXPIRATION` | `86400000` | Auth and protected services |

All service configuration is in `application.properties`. The same `JWT_SECRET` must be set for Auth Service and every service that validates tokens. Do not commit or reuse a development secret in production.

---

## ⚠️ Known Limitations

- **No Config Server** — configuration is per-service in `application.properties`
- **Resume files stored locally** at `./uploads/resumes/` in the jobseeker-service directory — not shareable across multiple instances
- **No email/SMS notifications**
- **No Kafka/RabbitMQ** — inter-service communication uses REST (WebClient)
- **Synchronous service dependencies** — applying for a job and creating a job require the relevant downstream services to be available
- **Messaging participant ownership is not fully enforced** — role checks exist, but message/conversation profile IDs are not yet verified against the authenticated user's profile
- **Single-node Eureka** — no high-availability replication
- **No advanced candidate matching** — job/candidate search uses basic SQL `LIKE` queries
- **No rate limiting** on API Gateway
- **Ownership columns added for new records** — legacy jobs/applications/basket entries need an explicit owner backfill before their protected update/read flows work

---

## 📊 API Summary

| Service | Base Path | Key Endpoints |
|---|---|---|
| Auth | `/api/auth` | POST /register, POST /login |
| Employer | `/api/employers` | POST /, GET /{id}, GET /user/{userId}, PUT /{id} |
| Job | `/api/jobs` | POST /, GET /(search), GET /{id}, PUT /{id}, DELETE /{id}, PATCH /{id}/close |
| JobSeeker | `/api/jobseekers` | POST /, GET /{id}, PUT /{id}, POST /{id}/resume, GET /search |
| Application | `/api/applications` | POST /, GET /job/{id}, GET /seeker/{id}, GET /employer/{id}, PATCH /{id}/status |
| Basket | `/api/basket` | POST /, GET /{jobSeekerId}, DELETE /{basketId} |
| Messaging | `/api/messages` | POST /employer, POST /seeker, GET /employer/{id}, GET /seeker/{id}, GET /conversation |

---

## 🎯 Evaluation Demo Script (5–8 min)

| Time | Action |
|---|---|
| 0:00–0:45 | Show Eureka dashboard — all 9 services registered |
| 0:45–1:45 | Swagger: Register employer → login → create profile → post job |
| 1:45–2:30 | Register job seeker → create profile |
| 2:30–3:30 | Search jobs → apply (show WebClient inter-service call) |
| 3:30–4:15 | Employer: list applications → shortlist candidate |
| 4:15–5:00 | Seeker: check application status → see SHORTLISTED |
| 5:00–5:30 | Show validation failure (blank title → 400) |
| 5:30–6:15 | Show 404 on missing resource vs generic 500 |
| 6:15–7:00 | Send message → view conversation |
| 7:00–7:30 | Add to basket → view basket |
| 7:30–8:00 | Show a few JUnit test results passing |
