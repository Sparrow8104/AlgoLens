# AlgoLens — Competitive Programming Analytics & AI Insights Platform

## Overview

AlgoLens is a backend system designed to provide deep insights into competitive programming performance using Codeforces data. It combines analytics, comparison tools, and AI-driven analysis to help users improve their problem-solving skills.

The platform enables users to track progress, compare with peers, and receive intelligent recommendations based on their coding behavior.

---

## Features

### Authentication & Security

- JWT-based authentication  
- Refresh token rotation  
- Email verification system  
- Password reset functionality  
- Login attempt limiting  
- Email rate limiting  
- Device-based session management  
- Logout and logout-all support  

---

### User Analytics

- Fetch Codeforces user profile  
- Contest history tracking  
- Rating progression graph  
- Submission statistics analysis  

---

### Contest Features

- Upcoming contests  
- Paginated contest history  

---

### Comparison System

- Compare ratings between two users  
- Compare submissions for the same problem  

---

### Social & Insights

- Friend system  
- Leaderboard comparison with friends  
- Weak topic detection  
- Personalized problem recommendations  
- Upsolve suggestions from recent contests  

---

### AI-Powered Analysis

- AI-based upsolve analysis  
- Insights into problem-solving approaches  

---

## Tech Stack

**Backend**
- Java  
- Spring Boot  
- Spring Security  

**Database**
- MySQL  

**External APIs**
- Codeforces API  

**Other**
- REST APIs  
- WebClient (for API calls)  

---

## Architecture Overview

Client
↓
REST Controllers
↓
Service Layer
↓
├── Codeforces API (data fetch)
├── Database (user data, caching)
└── AI Analysis Service

### Security Layer

- JWT Authentication  
- Refresh Token Rotation  
- Rate Limiting (login & email)  

---

## API Endpoints

### Authentication
POST /api/auth/register
POST /api/auth/login
POST /api/auth/refresh
POST /api/auth/logout
POST /api/auth/logout-all
GET /api/auth/verify-email
POST /api/auth/resend-verification
POST /api/auth/forgot-password
POST /api/auth/reset-password


---

### User
GET /api/users/{handle}/profile
GET /api/users/{handle}/contest-history
GET /api/users/{handle}/rating-graph
GET /api/users/{handle}/submission-stats


---

### Contests


GET /api/contests/upcoming
GET /api/contests?page=0&size=20


### Comparison


GET /api/compare/rating?handle1=A&handle2=B
POST /api/compare/find


---

### Friends

POST /api/friends/add
DELETE /api/friends/{userHandle}/remove/{friendHandle}
GET /api/friends/{handle}
GET /api/friends/{handle}/leaderboard
GET /api/friends/{handle}/unsolved-by-me
GET /api/friends/{handle}/streak-compare
GET /api/friends/{handle}/contest-overlap/{contestId}


---

### Insights


GET /api/insights/{handle}/weak-topics
GET /api/insights/{handle}/recommendations
GET /api/insights/{handle}/upsolve

### AI Analysis


GET /api/analysis/upsolve/{handle}


---

## Setup Instructions

### Prerequisites

- Java 17+  
- Maven  
- MySQL  

---### 1. Clone the repository

```bash
git clone https://github.com/your-username/algolens.git
cd algolens
```

2. Configure environment variables

Set the following in application.properties or environment:

spring.datasource.url=YOUR_DB_URL
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD

jwt.secret=YOUR_SECRET_KEY

3. Run the application
mvn spring-boot:run
4. Access API
http://localhost:8080
Security Notes
Access tokens are short-lived
Refresh tokens are rotated and stored securely
Sensitive operations require authentication
Rate limiting prevents abuse


Future Improvements
Contest performance prediction
Advanced AI recommendations
Code similarity detection
Frontend dashboard integration


Author

Developed as a competitive programming analytics and learning platform.
