# JobConnect frontend

JobConnect is a responsive React/Vite frontend for the Spring Boot job-search services in this repository. It uses the API gateway at `http://localhost:8080` and only calls routes present in the backend controllers.

## Run locally

1. Start the API gateway and its registered services. The gateway is configured for port `8080`; auth/profile/job/application/basket/message services register through Eureka.
2. In a separate terminal:

   ```powershell
   cd frontend
   npm install
   npm run dev
   ```

3. Open `http://localhost:5173`.

The development console includes a connection check for the read-only `GET /api/jobs` route. A `401` or `403` confirms that the gateway responded but that this request requires authentication or a role. A network error with no readable response may be a CORS block or an unavailable gateway.

## Environment

Copy `.env.example` to `.env` if needed:

```env
VITE_API_BASE_URL=http://localhost:8080
VITE_JOBSEEKER_SERVICE_URL=http://localhost:8084
```

`VITE_API_BASE_URL` is the API origin; service methods append the controller paths such as `/api/jobs`. `VITE_JOBSEEKER_SERVICE_URL` is only used to open uploaded resume files; the gateway currently has no `/uploads` route, and the job-seeker service defaults to port `8084`. To use the Vite proxy instead of making a cross-origin API request, set:

```env
VITE_API_BASE_URL=http://localhost:5173
```

The Vite dev server forwards `/api` requests to `http://localhost:8080`. Restart Vite after changing `.env`.

## API connection and CORS

The gateway currently has a `CorsWebFilter` and global CORS properties, but both allow arbitrary origins/headers and set `allowCredentials=true`. The application uses stateless JWT Bearer tokens (not session cookies), so credentials are unnecessary and the wildcard configuration should be narrowed for local development.

In `api-gateway`'s `CorsConfig`, use:

```java
corsConfig.setAllowedOrigins(Collections.singletonList("http://localhost:5173"));
corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
corsConfig.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
corsConfig.setAllowCredentials(false);
```

Also replace the matching wildcard values in `api-gateway/src/main/resources/application.properties` with:

```properties
spring.cloud.gateway.globalcors.cors-configurations.[/**].allowed-origins=http://localhost:5173
spring.cloud.gateway.globalcors.cors-configurations.[/**].allowed-methods=GET,POST,PUT,PATCH,DELETE,OPTIONS
spring.cloud.gateway.globalcors.cors-configurations.[/**].allowed-headers=Authorization,Content-Type
spring.cloud.gateway.globalcors.cors-configurations.[/**].allow-credentials=false
```

The gateway's `CorsWebFilter` handles OPTIONS preflights. If a browser request is routed directly to a secured service instead of through the gateway, enable CORS in that service's Spring Security chain and permit preflight requests, for example:

```java
.cors(Customizer.withDefaults())
.authorizeHttpRequests(auth -> auth
    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
    // existing matchers
)
```

The services are stateless and expect `Authorization: Bearer <token>`; the frontend stores the JWT returned by `/api/auth/login` or `/api/auth/register` and sends it on subsequent API requests.

## Implemented pages → backend endpoints

| Page | Controller endpoint(s) |
|---|---|
| Landing | No API request |
| Sign in / create account | `POST /api/auth/login`, `POST /api/auth/register` |
| Dashboard | `GET /api/jobseekers/user/{userId}` or `GET /api/employers/user/{userId}`; `GET /api/jobs`, `GET /api/jobs/employer/{employerId}`, `GET /api/applications/seeker/{seekerId}`, `GET /api/applications/employer/{employerId}` |
| Job search | `GET /api/jobs` with `title`, `location`, `skills`, and/or `company` query parameters |
| Job detail / apply | `GET /api/jobs/{id}`, `POST /api/applications` |
| Saved jobs | `GET /api/basket/{jobSeekerId}`, `POST /api/basket`, `DELETE /api/basket/{basketId}` |
| Profile setup and edit | `GET /api/jobseekers/user/{userId}`, `POST /api/jobseekers`, `PUT /api/jobseekers/{id}`; `GET /api/employers/user/{userId}`, `POST /api/employers`, `PUT /api/employers/{id}` |
| Resume upload | `POST /api/jobseekers/{id}/resume` (`multipart/form-data`, field `file`) |
| Employer job listings | `GET /api/jobs/employer/{employerId}`, `PATCH /api/jobs/{id}/close`, `DELETE /api/jobs/{id}` |
| Create / edit job | `POST /api/jobs`, `GET /api/jobs/{id}`, `PUT /api/jobs/{id}` |
| Applications | `GET /api/applications/seeker/{seekerId}` or `GET /api/applications/employer/{employerId}`, `PATCH /api/applications/{id}/status` |
| Candidate search | `GET /api/jobseekers/search` with `skills`, `location`, and/or `minExperience` query parameters |
| Messages | `GET /api/messages/seeker/{seekerId}` or `GET /api/messages/employer/{employerId}`, `POST /api/messages/seeker`, `POST /api/messages/employer` |
| Not found | No API request |
