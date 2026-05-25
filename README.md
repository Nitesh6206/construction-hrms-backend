# Construction HRMS - Java Backend

A robust backend system for managing blue-collar workforce in the construction industry. Features daily attendance tracking, real-time active workers, automatic overtime calculation, and monthly settlement.

## Features Implemented

- Worker & Site Management
- Clock-In / Clock-Out with business rules
- Real-time Active Workers (Redis)
- Automatic Overtime Calculation (1.5x for first 2 hrs, 2x after)
- Monthly Overtime Cap (60 hours)
- Atomic Overtime Settlement with SMS notification (after commit)
- Proper error handling and validation
- Pagination + N+1 Query Prevention using `@EntityGraph`

## Tech Stack

- **Java 17**
- **Spring Boot 2.4.3**
- **JPA / Hibernate**
- **PostgreSQL (Supabase)**
- **Redis** (for caching)
- **Lombok**

---

## Full Setup Guide

### Step 1: Clone & Setup New Repository

```bash
# 1. Clone original repo (if not already done)
git clone https://github.com/YOUR_USERNAME/spring-boot-fullstack-professional.git
cd spring-boot-fullstack-professional

# 2. Remove original git history (to detach from amigoscode)
rm -rf .git

# 3. Initialize new git repository
git init

# 4. Add remote to your new repo
git remote add origin https://github.com/YOUR_USERNAME/your-new-repo-name.git

# 5. Add all files
git add .

# 6. First commit
git commit -m "Initial commit: Construction HRMS Backend Assignment"

# 7. Push to new repository
git push -u origin main

### Step 2: Install Dependencies

# Install Redis (Mac)
brew install redis
brew services start redis

# Verify Redis
redis-cli ping   # Should return "PONG"

###Step 3: Setup Supabase Database

# 1. Go to supabase.com and create a new project
# 2. Go to Project Settings → Database
# 3. Copy the JDBC URL (use port 6543 - Pooler)
# 4. Note down postgres username and password

###Step 4: Configure application.yml
Create / Update src/main/resources/application.yml:
spring:
  datasource:
    url: jdbc:postgresql://db.[YOUR-PROJECT-REF].supabase.co:6543/postgres
    username: postgres
    password: [YOUR_SUPABASE_PASSWORD]
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

  data:
    redis:
      host: localhost
      port: 6379

server:
  port: 8080

###Step 5: Run the Application

# Clean and run
mvn clean spring-boot:run
Application will start on http://localhost:8080


API Endpoints
Worker

POST /api/workers → Create worker
GET /api/workers → List workers

Site

POST /api/sites → Create site
GET /api/sites → List sites

Attendance

POST /api/attendance/clock-inJSON{ "workerId": 1, "siteId": 1 }
POST /api/attendance/clock-outJSON{ "workerId": 1 }
GET /api/attendance/active → Real-time active workers (Redis)
GET /api/attendance/log?workerId=1&from=2026-05-01&to=2026-05-25&page=0&size=10

Overtime

GET /api/overtime/summary/{workerId}?month=2026-05
POST /api/overtime/settle/{workerId}?month=2026-04