# 🏗️ Construction HRMS - Java Backend

A robust backend system for managing blue-collar workforce operations in the construction industry.  
This system handles worker attendance, real-time active workers tracking, overtime management, and monthly settlement processing.

---

# 🚀 Features Implemented

## ✅ Worker & Site Management
- Create and manage workers
- Create and manage construction sites
- Worker active/inactive status management

## ✅ Attendance Management
- Clock-In / Clock-Out system
- Prevents duplicate active attendance
- Tracks working duration automatically

## ✅ Real-Time Active Workers (Redis)
- Fast retrieval of active workers
- Redis cache with TTL support
- Optimized for supervisor dashboards

## ✅ Automatic Overtime Calculation
- 1.5x wage for first 2 overtime hours
- 2x wage after 2 overtime hours
- Automatic overtime entry creation

## ✅ Monthly Overtime Cap
- Maximum 60 overtime hours per month
- Prevents exceeding overtime limits

## ✅ Settlement System
- Atomic overtime settlement using `@Transactional`
- SMS notification triggered only after successful DB commit
- Supports monthly settlement flow

## ✅ Performance Optimizations
- Pagination support
- N+1 Query Prevention using `@EntityGraph`
- HikariCP connection pooling

## ✅ Proper Error Handling
- Global exception handling
- Validation support
- Business rule enforcement

---

# 🛠️ Tech Stack

| Technology | Usage |
|---|---|
| Java 17 | Core Language |
| Spring Boot 2.4.3 | Backend Framework |
| Spring Data JPA | ORM |
| Hibernate | Persistence Layer |
| PostgreSQL (Supabase) | Database |
| Redis | Caching |
| Lombok | Boilerplate Reduction |
| Maven | Build Tool |

---

# 📂 Project Architecture

```text
Controller Layer
       ↓
Service Layer
       ↓
Repository Layer
       ↓
PostgreSQL (Supabase)

Redis Cache
   ↓
Active Workers API
```

---

# ⚙️ Full Setup Guide

# 📌 Step 1: Clone Repository

```bash
git clone https://github.com/YOUR_USERNAME/YOUR_REPOSITORY_NAME.git

cd YOUR_REPOSITORY_NAME
```

---

# 📌 Step 2: Install Redis

## MacOS

```bash
brew install redis

brew services start redis
```

## Verify Redis

```bash
redis-cli ping
```

Expected Output:

```text
PONG
```

---

# 📌 Step 3: Setup Supabase Database

## 1️⃣ Create Supabase Project

Go to:

```text
https://supabase.com
```

- Sign in
- Click **New Project**
- Enter:
    - Project Name
    - Database Password
    - Region
- Create project

---

## 2️⃣ Open Database Settings

Inside Supabase Dashboard:

```text
Project Settings
      ↓
Database
```

---

## 3️⃣ Copy Required Credentials

Copy these values:

| Field | Example |
|---|---|
| JDBC URL | jdbc:postgresql://db.xxxxx.supabase.co:6543/postgres |
| Username | postgres |
| Password | your-password |

⚠️ IMPORTANT:
Use **Pooler Port = 6543**

NOT:

```text
5432
```

Use:

```text
6543
```

---

# 📌 Step 4: Configure application.yml

Create file:

```text
src/main/resources/application.yml
```

Add this configuration:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://db.YOUR_PROJECT_REF.supabase.co:6543/postgres
    username: postgres
    password: YOUR_SUPABASE_PASSWORD
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
```

---

# 📌 Step 5: Run Application

```bash
mvn clean spring-boot:run
```

Application starts on:

```text
http://localhost:8080
```

---

# 📡 API Endpoints

# 👷 Worker APIs

## Create Worker

```http
POST /api/workers
```

## Get All Workers

```http
GET /api/workers
```

---

# 🏗️ Site APIs

## Create Site

```http
POST /api/sites
```

## Get All Sites

```http
GET /api/sites
```

---

# 🕒 Attendance APIs

## Clock-In Worker

```http
POST /api/attendance/clock-in
```

### Request Body

```json
{
  "workerId": 1,
  "siteId": 1
}
```

---

## Clock-Out Worker

```http
POST /api/attendance/clock-out
```

### Request Body

```json
{
  "workerId": 1
}
```

---

## Get Active Workers (Redis)

```http
GET /api/attendance/active
```

---

## Attendance Logs

```http
GET /api/attendance/log?workerId=1&from=2026-05-01&to=2026-05-25&page=0&size=10
```

---

# ⏱️ Overtime APIs

## Get Overtime Summary

```http
GET /api/overtime/summary/{workerId}?month=2026-05
```

---

## Settle Overtime

```http
POST /api/overtime/settle/{workerId}?month=2026-04
```

---

# 📌 Business Rules Implemented

- Standard shift = 8 hours
- First 2 overtime hours → 1.5x wage
- After 2 overtime hours → 2x wage
- Monthly overtime cap = 60 hours
- Workers exceeding 16 hours are flagged
- Only one active attendance allowed per worker

---

# ⚡ Redis Usage

Redis is used only for:

```text
GET /api/attendance/active
```

## Cached Data
- Active workers list
- TTL = 16 hours

## Why Redis?
- Real-time response
- Faster than DB query
- Optimized for supervisor dashboards

---

# 🔒 Transaction Handling

Settlement flow uses:

```java
@Transactional
```

## Why?
Ensures:
- All DB operations succeed together
- No partial settlement

## AFTER_COMMIT Event
SMS notification is triggered only after successful transaction commit.

---

# 📈 Performance Improvements

- Pagination support
- `@EntityGraph` to prevent N+1 queries
- Redis caching
- HikariCP connection pooling

---

# 🧪 Example Flows

# ✅ Clock-In Flow

```text
Clock-In Request
      ↓
Validation
      ↓
Save Attendance
      ↓
Add Worker to Redis
      ↓
Success Response
```

---

# ✅ Clock-Out Flow

```text
Clock-Out Request
      ↓
Calculate Hours
      ↓
Check Overtime
      ↓
Create Overtime Entry
      ↓
Remove Worker from Redis
```

---

# ✅ Settlement Flow

```text
POST /settle
      ↓
@Transactional
      ↓
Update Status = SETTLED
      ↓
AFTER_COMMIT Event
      ↓
Send SMS
```

---

# 👨‍💻 Author

## Nitesh Kumar

Software Engineer | Java Backend Developer

---