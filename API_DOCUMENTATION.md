# AlgoLens API Documentation

**Base URL:** `http://localhost:8080/api`

**API Version:** 1.0

---

## Table of Contents

1. [Authentication](#authentication)
2. [Authentication Endpoints](#authentication-endpoints)
3. [User Profile Endpoints](#user-profile-endpoints)
4. [Contest Endpoints](#contest-endpoints)
5. [Comparison Endpoints](#comparison-endpoints)
6. [Friend Endpoints](#friend-endpoints)
7. [Insight Endpoints](#insight-endpoints)
8. [Analysis Endpoints](#analysis-endpoints)
9. [Verification Endpoints](#verification-endpoints)
10. [System Architecture & Core Business Flows](#system-architecture--core-business-flows)
11. [Notes for Frontend Developers](#notes-for-frontend-developers)

---

## Authentication

All protected endpoints require a **JWT Bearer token** in the Authorization header.

**Header Format:**
```
Authorization: Bearer <access_token>
```

**How to Get a Token:**
1. Register a new account at `/auth/register`
2. Verify your email at `/auth/verify-email`
3. Login at `/auth/login` to receive access token
4. Use the access token for all subsequent requests
5. When token expires, use `/auth/refresh` with your refresh token

---

## Authentication Endpoints

### Register User

**Method:** POST  
**URL:** `/auth/register`

**Description:**  
Create a new user account with email verification.

**Headers:**
```json
{
  "Content-Type": "application/json"
}
```

**Request Body:**
```json
{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "password": "SecurePassword@123"
}
```

**Success Response (200 OK):**
```
"User registered successfully. Verification email sent to john.doe@example.com"
```

**Error Response (400 Bad Request):**
```json
{
  "timestamp": "2026-04-13T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid email format or password too weak",
  "path": "/api/auth/register"
}
```

**Error Response (409 Conflict):**
```json
{
  "timestamp": "2026-04-13T10:30:00",
  "status": 409,
  "error": "Conflict",
  "message": "Email already exists",
  "path": "/api/auth/register"
}
```

**Validation Rules:**
- `name`: Required, 2-100 characters
- `email`: Required, valid email format, unique
- `password`: Required, minimum 8 characters

---

### Verify Email

**Method:** GET  
**URL:** `/auth/verify-email`

**Description:**  
Verify user email using the verification token from email.

**Query Parameters:**
- `token` (required): Email verification token

**Headers:**
```json
{
  "Content-Type": "application/json"
}
```

**Success Response (200 OK):**
```
"Email verified successfully"
```

**Error Response (400 Bad Request):**
```json
{
  "timestamp": "2026-04-13T10:35:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid or expired verification token",
  "path": "/api/auth/verify-email"
}
```

---

### Login

**Method:** POST  
**URL:** `/auth/login`

**Description:**  
Authenticate user and receive JWT tokens for API access.

**Headers:**
```json
{
  "Content-Type": "application/json",
  "X-Device-Id": "device-uuid-12345"
}
```

**Request Body:**
```json
{
  "email": "john.doe@example.com",
  "password": "SecurePassword@123"
}
```

**Success Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huLmRvZUBleGFtcGxlLmNvbSIsImlhdCI6MTcxMzAxODYwMCwiZXhwIjoxNzEzMDIyMjAwfQ.abcdef123456",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huLmRvZUBleGFtcGxlLmNvbSIsImlhdCI6MTcxMzAxODYwMCwiZXhwIjoxNzEzNjIzNDAwfQ.xyz789"
}
```

**Error Response (401 Unauthorized):**
```json
{
  "timestamp": "2026-04-13T10:40:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid email or password",
  "path": "/api/auth/login"
}
```

**Error Response (403 Forbidden):**
```json
{
  "timestamp": "2026-04-13T10:40:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Email not verified. Check your inbox for verification link",
  "path": "/api/auth/login"
}
```

**Notes:**
- Store both tokens securely
- Access token expires in 1 hour
- Refresh token expires in 7 days
- Device ID helps with multi-device tracking

---

### Refresh Token

**Method:** POST  
**URL:** `/auth/refresh`

**Description:**  
Generate new access token using a valid refresh token.

**Headers:**
```json
{
  "Content-Type": "application/json"
}
```

**Request Body:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Success Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.newtoken...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Error Response (401 Unauthorized):**
```json
{
  "timestamp": "2026-04-13T10:45:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Refresh token expired or invalid",
  "path": "/api/auth/refresh"
}
```

**Notes:**
- Call this when you get a 401 on protected endpoints
- Both new tokens are returned

---

### Logout

**Method:** POST  
**URL:** `/auth/logout`

**Description:**  
Invalidate the provided refresh token (logout from current device).

**Headers:**
```json
{
  "Content-Type": "application/json",
  "Authorization": "Bearer <access_token>"
}
```

**Request Body:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Success Response (200 OK):**
```
"Logged out successfully"
```

---

### Logout All Devices

**Method:** POST  
**URL:** `/auth/logout-all`

**Description:**  
Invalidate all refresh tokens across all devices for the authenticated user.

**Headers:**
```json
{
  "Content-Type": "application/json",
  "Authorization": "Bearer <access_token>"
}
```

**Request Body:** (empty)

**Success Response (200 OK):**
```
"Logged out from all devices successfully"
```

**Error Response (401 Unauthorized):**
```json
{
  "timestamp": "2026-04-13T10:50:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid or missing authentication token",
  "path": "/api/auth/logout-all"
}
```

---

### Resend Email Verification

**Method:** POST  
**URL:** `/auth/resend-verification`

**Description:**  
Resend verification email to the specified email address.

**Query Parameters:**
- `email` (required): User email address

**Headers:**
```json
{
  "Content-Type": "application/json"
}
```

**Success Response (200 OK):**
```
"Verification email resent to john.doe@example.com"
```

**Error Response (404 Not Found):**
```json
{
  "timestamp": "2026-04-13T10:50:00",
  "status": 404,
  "error": "Not Found",
  "message": "User with this email not found",
  "path": "/api/auth/resend-verification"
}
```

---

### Forgot Password

**Method:** POST  
**URL:** `/auth/forgot-password`

**Description:**  
Request password reset by sending OTP to registered email.

**Headers:**
```json
{
  "Content-Type": "application/json"
}
```

**Request Body:**
```json
{
  "email": "john.doe@example.com"
}
```

**Success Response (200 OK):**
```json
{
  "message": "Password reset OTP sent to john.doe@example.com"
}
```

**Error Response (404 Not Found):**
```json
{
  "timestamp": "2026-04-13T11:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "User not found",
  "path": "/api/auth/forgot-password"
}
```

---

### Verify Reset Token

**Method:** POST  
**URL:** `/auth/verify-reset-token`

**Description:**  
Verify the OTP received in password reset email.

**Headers:**
```json
{
  "Content-Type": "application/json"
}
```

**Request Body:**
```json
{
  "token": "123456"
}
```

**Success Response (200 OK):**
```json
{
  "message": "Token verified successfully"
}
```

**Error Response (400 Bad Request):**
```json
{
  "timestamp": "2026-04-13T11:05:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid or expired OTP",
  "path": "/api/auth/verify-reset-token"
}
```

---

### Reset Password

**Method:** POST  
**URL:** `/auth/reset-password`

**Description:**  
Reset password using OTP and new password.

**Headers:**
```json
{
  "Content-Type": "application/json"
}
```

**Request Body:**
```json
{
  "otp": "123456",
  "newPassword": "NewPassword@123"
}
```

**Success Response (200 OK):**
```json
{
  "message": "Password reset successfully"
}
```

**Error Response (400 Bad Request):**
```json
{
  "timestamp": "2026-04-13T11:10:00",
  "status": 400,
  "error": "Bad Request",
  "message": "OTP expired or invalid",
  "path": "/api/auth/reset-password"
}
```

**Validation Rules:**
- `otp`: Required, 6 digits
- `newPassword`: Required, minimum 8 characters

---

## User Profile Endpoints

### Get User Profile

**Method:** GET  
**URL:** `/users/{handle}/profile`

**Description:**  
Retrieve Codeforces user profile information. Data is cached for 5 minutes.

**Path Parameters:**
- `handle` (required): Codeforces username (e.g., "tourist")

**Headers:**
```json
{
  "Authorization": "Bearer <access_token>"
}
```

**Success Response (200 OK):**
```json
{
  "handle": "tourist",
  "rating": 3950,
  "maxRating": 3957,
  "rank": "International Master",
  "problemsSolved": 2847,
  "contestsParticipated": 450,
  "streakDays": 127,
  "lastActiveDate": "2026-04-13",
  "avatar": "https://codeforces.com/avatar/tourist.jpg"
}
```

**Error Response (404 Not Found):**
```json
{
  "timestamp": "2026-04-13T11:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "User handle 'invalid_user' not found on Codeforces",
  "path": "/api/users/invalid_user/profile"
}
```

---

### Get User Contest History (Paginated)

**Method:** GET  
**URL:** `/users/{handle}/contest-history/paginated`

**Description:**  
Retrieve contests the user participated in with rating changes, with pagination support.

**Path Parameters:**
- `handle` (required): Codeforces username

**Query Parameters:**
- `page` (optional, default=0): Page number (0-indexed)
- `size` (optional, default=20): Items per page

**Headers:**
```json
{
  "Authorization": "Bearer <access_token>"
}
```

**Success Response (200 OK):**
```json
{
  "content": [
    {
      "contestId": 1865,
      "contestName": "Codeforces Round 892 (Div. 1)",
      "rank": 12,
      "oldRating": 3920,
      "newRating": 3950,
      "ratingChange": 30
    },
    {
      "contestId": 1860,
      "contestName": "Codeforces Round 891 (Div. 1)",
      "rank": 45,
      "oldRating": 3910,
      "newRating": 3920,
      "ratingChange": 10
    }
  ],
  "page": {
    "size": 20,
    "number": 0,
    "totalElements": 450,
    "totalPages": 23
  }
}
```

**Error Response (404 Not Found):**
```json
{
  "timestamp": "2026-04-13T11:05:00",
  "status": 404,
  "error": "Not Found",
  "message": "No contest history found for user 'unknown_user'",
  "path": "/api/users/unknown_user/contest-history/paginated"
}
```

---

### Get Rating Graph Data

**Method:** GET  
**URL:** `/users/{handle}/rating-graph`

**Description:**  
Retrieve rating progression across all contests for building charts.

**Path Parameters:**
- `handle` (required): Codeforces username

**Headers:**
```json
{
  "Authorization": "Bearer <access_token>"
}
```

**Success Response (200 OK):**
```json
[
  {
    "contestId": 1865,
    "contestName": "Codeforces Round 892 (Div. 1)",
    "rating": 3950,
    "date": "2026-04-10"
  },
  {
    "contestId": 1860,
    "contestName": "Codeforces Round 891 (Div. 1)",
    "rating": 3920,
    "date": "2026-04-03"
  }
]
```

**Notes:**
- Use this data to plot rating progression chart
- Sorted by date in ascending order

---

### Get Submission Statistics

**Method:** GET  
**URL:** `/users/{handle}/submission-stats`

**Description:**  
Get detailed submission statistics with verdicts breakdown.

**Path Parameters:**
- `handle` (required): Codeforces username

**Headers:**
```json
{
  "Authorization": "Bearer <access_token>"
}
```

**Success Response (200 OK):**
```json
{
  "totalSubmissions": 3450,
  "solvedProblems": 847,
  "unSolvedProblems": 523,
  "verdictsCount": {
    "ACCEPTED": 847,
    "WRONG_ANSWER": 1200,
    "TIME_LIMIT_EXCEEDED": 450,
    "MEMORY_LIMIT_EXCEEDED": 180,
    "COMPILATION_ERROR": 95,
    "RUNTIME_ERROR": 87
  }
}
```

---

## Contest Endpoints

### Get Upcoming Contests

**Method:** GET  
**URL:** `/contests/upcoming`

**Description:**  
Retrieve list of upcoming Codeforces contests (next 7-14 days).

**Headers:**
```json
{
  "Authorization": "Bearer <access_token>"
}
```

**Success Response (200 OK):**
```json
[
  {
    "contestId": 1900,
    "name": "Codeforces Round 900 (Div. 1 + Div. 2)",
    "type": "CF",
    "durationSeconds": 7200,
    "startTimeSeconds": 1713177600,
    "relativeTimeSeconds": -86400
  },
  {
    "contestId": 1901,
    "name": "Educational Codeforces Round 160",
    "type": "ICPC",
    "durationSeconds": 7200,
    "startTimeSeconds": 1713264000,
    "relativeTimeSeconds": 0
  }
]
```

**Notes:**
- Convert `startTimeSeconds` (Unix timestamp) to display contest start time
- `relativeTimeSeconds`: negative means contest hasn't started, positive means it's ongoing/finished
- `durationSeconds` is 2 hours (7200 seconds) for most contests

---

### Get Upcoming Contests (Paginated)

**Method:** GET  
**URL:** `/contests/upcoming/paginated`

**Description:**  
Retrieve list of upcoming Codeforces contests (next 7-14 days) with pagination.

**Query Parameters:**
- `page` (optional, default=0): Page number (0-indexed)
- `size` (optional, default=20): Items per page

**Headers:**
```json
{
  "Authorization": "Bearer <access_token>"
}
```

**Success Response (200 OK):**
```json
{
  "content": [
    {
      "contestId": 1900,
      "name": "Codeforces Round 900 (Div. 1 + Div. 2)",
      "type": "CF",
      "durationSeconds": 7200,
      "startTimeSeconds": 1713177600,
      "relativeTimeSeconds": -86400
    },
    {
      "contestId": 1901,
      "name": "Educational Codeforces Round 160",
      "type": "ICPC",
      "durationSeconds": 7200,
      "startTimeSeconds": 1713264000,
      "relativeTimeSeconds": 0
    }
  ],
  "page": {
    "size": 20,
    "number": 0,
    "totalElements": 2,
    "totalPages": 1
  }
}
```

---

### Get All Contests (Paginated)

**Method:** GET  
**URL:** `/contests`

**Description:**  
Retrieve all historical and upcoming contests with pagination.

**Query Parameters:**
- `page` (optional, default=0): Page number (0-indexed)
- `size` (optional, default=20): Items per page (1-100)

**Headers:**
```json
{
  "Authorization": "Bearer <access_token>"
}
```

**Success Response (200 OK):**
```json
{
  "content": [
    {
      "contestId": 1865,
      "name": "Codeforces Round 892 (Div. 1)",
      "type": "CF",
      "durationSeconds": 7200,
      "startTimeSeconds": 1712869200,
      "relativeTimeSeconds": 1296000
    }
  ],
  "page": {
    "size": 20,
    "number": 0,
    "totalElements": 2847,
    "totalPages": 143
  }
}
```

---

## Comparison Endpoints

### Compare User Ratings

**Method:** GET  
**URL:** `/compare/rating`

**Description:**  
Compare current and maximum ratings between two Codeforces users.

**Query Parameters:**
- `handle1` (required): First username
- `handle2` (required): Second username

**Headers:**
```json
{
  "Authorization": "Bearer <access_token>"
}
```

**Success Response (200 OK):**
```json
{
  "handle1": "tourist",
  "handle2": "Petr",
  "rating1": 3950,
  "rating2": 3815,
  "ratingDelta": 135,
  "higherRatedHandle": "tourist",
  "maxRating1": 3957,
  "maxRating2": 3833,
  "rank1": "International Master",
  "rank2": "International Master",
  "contestsParticipated1": 450,
  "contestsParticipated2": 420
}
```

**Error Response (400 Bad Request):**
```json
{
  "timestamp": "2026-04-13T11:15:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Cannot compare user with themselves",
  "path": "/api/compare/rating?handle1=tourist&handle2=tourist"
}
```

**Error Response (404 Not Found):**
```json
{
  "timestamp": "2026-04-13T11:15:00",
  "status": 404,
  "error": "Not Found",
  "message": "User 'unknown_handle' not found",
  "path": "/api/compare/rating?handle1=tourist&handle2=unknown_handle"
}
```

---

### Find Submission Comparison

**Method:** POST  
**URL:** `/compare/find`

**Description:**  
Retrieve submissions from both users for a specific problem to compare solutions.

**Headers:**
```json
{
  "Content-Type": "application/json",
  "Authorization": "Bearer <access_token>"
}
```

**Request Body:**
```json
{
  "handle1": "tourist",
  "handle2": "Petr",
  "contestId": 1865,
  "index": "A"
}
```

**Success Response (200 OK):**
```json
{
  "contestId": 1865,
  "index": "A",
  "user1Result": {
    "handle": "tourist",
    "solved": true,
    "verdict": "ACCEPTED",
    "programmingLanguage": "C++",
    "timeConsumedMillis": 234,
    "memoryConsumedBytes": 2097152,
    "submittedAt": 1712869500,
    "submissionId": 187654321
  },
  "user2Result": {
    "handle": "Petr",
    "solved": true,
    "verdict": "ACCEPTED",
    "programmingLanguage": "C++",
    "timeConsumedMillis": 567,
    "memoryConsumedBytes": 3145728,
    "submittedAt": 1712869710,
    "submissionId": 187654340
  }
}
```

**Error Response (404 Not Found):**
```json
{
  "timestamp": "2026-04-13T11:20:00",
  "status": 404,
  "error": "Not Found",
  "message": "Problem A not found in contest 1865",
  "path": "/api/compare/find"
}
```

**Error Response (422 Unprocessable Entity):**
```json
{
  "timestamp": "2026-04-13T11:20:00",
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "User 'tourist' did not submit solution for problem A in contest 1865",
  "path": "/api/compare/find"
}
```

---

## Friend Endpoints

### Add Friend

**Method:** POST  
**URL:** `/friends/add`

**Description:**  
Add a Codeforces user as a friend for comparison and tracking.

**Headers:**
```json
{
  "Content-Type": "application/json",
  "Authorization": "Bearer <access_token>"
}
```

**Request Body:**
```json
{
  "userHandle": "myhandle",
  "friendHandle": "tourist"
}
```

**Success Response (200 OK):**
```
"Friend added successfully"
```

**Error Response (409 Conflict):**
```json
{
  "timestamp": "2026-04-13T11:25:00",
  "status": 409,
  "error": "Conflict",
  "message": "User 'tourist' is already your friend",
  "path": "/api/friends/add"
}
```

---

### Remove Friend

**Method:** DELETE  
**URL:** `/friends/{userHandle}/remove/{friendHandle}`

**Description:**  
Remove a user from your friend list.

**Path Parameters:**
- `userHandle` (required): Your Codeforces username
- `friendHandle` (required): Friend's Codeforces username

**Headers:**
```json
{
  "Authorization": "Bearer <access_token>"
}
```

**Success Response (200 OK):**
```
"Friend removed successfully"
```

**Error Response (404 Not Found):**
```json
{
  "timestamp": "2026-04-13T11:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Friend relationship not found",
  "path": "/api/friends/myhandle/remove/unknown"
}
```

---

### Get Friends List

**Method:** GET  
**URL:** `/friends/{handle}`

**Description:**  
Get all friends added by a user with their current ratings.

**Path Parameters:**
- `handle` (required): Your Codeforces username

**Headers:**
```json
{
  "Authorization": "Bearer <access_token>"
}
```

**Success Response (200 OK):**
```json
[
  {
    "handle": "tourist",
    "rating": 3950,
    "maxRating": 3957,
    "rank": "International Master",
    "avatar": "https://codeforces.com/avatar/tourist.jpg",
    "contestsParticipated": 450
  },
  {
    "handle": "Petr",
    "rating": 3815,
    "maxRating": 3833,
    "rank": "International Master",
    "avatar": "https://codeforces.com/avatar/Petr.jpg",
    "contestsParticipated": 420
  }
]
```

---

### Get Friends List (Paginated)

**Method:** GET  
**URL:** `/friends/{handle}/paginated`

**Description:**  
Get all friends added by a user with their current ratings, with pagination support.

**Path Parameters:**
- `handle` (required): Your Codeforces username

**Query Parameters:**
- `page` (optional, default=0): Page number (0-indexed)
- `size` (optional, default=20): Items per page

**Headers:**
```json
{
  "Authorization": "Bearer <access_token>"
}
```

**Success Response (200 OK):**
```json
{
  "content": [
    {
      "handle": "tourist",
      "rating": 3950,
      "maxRating": 3957,
      "rank": "International Master",
      "avatar": "https://codeforces.com/avatar/tourist.jpg",
      "contestsParticipated": 450
    },
    {
      "handle": "Petr",
      "rating": 3815,
      "maxRating": 3833,
      "rank": "International Master",
      "avatar": "https://codeforces.com/avatar/Petr.jpg",
      "contestsParticipated": 420
    }
  ],
  "page": {
    "size": 20,
    "number": 0,
    "totalElements": 2,
    "totalPages": 1
  }
}
```

---

### Get Friends Leaderboard

**Method:** GET  
**URL:** `/friends/{handle}/leaderboard`

**Description:**  
Get a ranked leaderboard of you and all your friends sorted by current rating.

**Path Parameters:**
- `handle` (required): Your Codeforces username

**Headers:**
```json
{
  "Authorization": "Bearer <access_token>"
}
```

**Success Response (200 OK):**
```json
[
  {
    "rank": 1,
    "handle": "tourist",
    "rating": 3950,
    "tier": "International Master",
    "maxRating": 3957
  },
  {
    "rank": 2,
    "handle": "myhandle",
    "rating": 3720,
    "tier": "Master",
    "maxRating": 3750
  },
  {
    "rank": 3,
    "handle": "Petr",
    "rating": 3815,
    "tier": "International Master",
    "maxRating": 3833
  }
]
```

---

### Get Friends Leaderboard (Paginated)

**Method:** GET  
**URL:** `/friends/{handle}/leaderboard/paginated`

**Description:**  
Get a ranked leaderboard of you and all your friends sorted by current rating, with pagination support.

**Path Parameters:**
- `handle` (required): Your Codeforces username

**Query Parameters:**
- `page` (optional, default=0): Page number (0-indexed)
- `size` (optional, default=20): Items per page

**Headers:**
```json
{
  "Authorization": "Bearer <access_token>"
}
```

**Success Response (200 OK):**
```json
{
  "content": [
    {
      "rank": 1,
      "handle": "tourist",
      "rating": 3950,
      "tier": "International Master",
      "maxRating": 3957
    },
    {
      "rank": 2,
      "handle": "myhandle",
      "rating": 3720,
      "tier": "Master",
      "maxRating": 3750
    },
    {
      "rank": 3,
      "handle": "Petr",
      "rating": 3815,
      "tier": "International Master",
      "maxRating": 3833
    }
  ],
  "page": {
    "size": 20,
    "number": 0,
    "totalElements": 3,
    "totalPages": 1
  }
}
```

---

### Get Unsolved Problems by Friends (Paginated)

**Method:** GET  
**URL:** `/friends/{handle}/unsolved-by-me/paginated`

**Description:**  
Get problems that friends have solved but you haven't, with pagination. Great for practice suggestions.

**Path Parameters:**
- `handle` (required): Your Codeforces username

**Query Parameters:**
- `page` (optional, default=0): Page number (0-indexed)
- `size` (optional, default=20): Items per page

**Headers:**
```json
{
  "Authorization": "Bearer <access_token>"
}
```

**Success Response (200 OK):**
```json
{
  "content": [
    {
      "contestId": 1860,
      "index": "D",
      "name": "Optimizing Orthogonal Tiling",
      "rating": 2800,
      "tags": ["binary search", "dp", "greedy"],
      "solvedByFriends": ["tourist", "Petr"]
    },
    {
      "contestId": 1850,
      "index": "E",
      "name": "Expected Value",
      "rating": 3000,
      "tags": ["math", "combinatorics", "probability"],
      "solvedByFriends": ["tourist"]
    }
  ],
  "page": {
    "size": 20,
    "number": 0,
    "totalElements": 2,
    "totalPages": 1
  }
}
```

---

### Compare Submission Streak

**Method:** GET  
**URL:** `/friends/{handle}/streak-compare`

**Description:**  
Compare coding streak (consecutive days with submissions) with all friends.

**Path Parameters:**
- `handle` (required): Your Codeforces username

**Headers:**
```json
{
  "Authorization": "Bearer <access_token>"
}
```

**Success Response (200 OK):**
```json
[
  {
    "handle": "myhandle",
    "currentStreak": 127,
    "lastSubmissionDate": "2026-04-13"
  },
  {
    "handle": "tourist",
    "currentStreak": 89,
    "lastSubmissionDate": "2026-04-12"
  },
  {
    "handle": "Petr",
    "currentStreak": 42,
    "lastSubmissionDate": "2026-04-11"
  }
]
```

---

### Compare Submission Streak (Paginated)

**Method:** GET  
**URL:** `/friends/{handle}/streak-compare/paginated`

**Description:**  
Compare coding streak (consecutive days with submissions) with all friends, with pagination support.

**Path Parameters:**
- `handle` (required): Your Codeforces username

**Query Parameters:**
- `page` (optional, default=0): Page number (0-indexed)
- `size` (optional, default=20): Items per page

**Headers:**
```json
{
  "Authorization": "Bearer <access_token>"
}
```

**Success Response (200 OK):**
```json
{
  "content": [
    {
      "handle": "myhandle",
      "currentStreak": 127,
      "lastSubmissionDate": "2026-04-13"
    },
    {
      "handle": "tourist",
      "currentStreak": 89,
      "lastSubmissionDate": "2026-04-12"
    },
    {
      "handle": "Petr",
      "currentStreak": 42,
      "lastSubmissionDate": "2026-04-11"
    }
  ],
  "page": {
    "size": 20,
    "number": 0,
    "totalElements": 3,
    "totalPages": 1
  }
}
```

---

### Get Contest Overlap Results

**Method:** GET  
**URL:** `/friends/{handle}/contest-overlap/{contestId}`

**Description:**  
Get performance results of you and your friends in a specific contest.

**Path Parameters:**
- `handle` (required): Your Codeforces username
- `contestId` (required): Contest ID

**Headers:**
```json
{
  "Authorization": "Bearer <access_token>"
}
```

**Success Response (200 OK):**
```json
[
  {
    "handle": "myhandle",
    "rank": 145,
    "oldRating": 3650,
    "newRating": 3720,
    "ratingChange": 70
  },
  {
    "handle": "tourist",
    "rank": 12,
    "oldRating": 3920,
    "newRating": 3950,
    "ratingChange": 30
  },
  {
    "handle": "Petr",
    "rank": 87,
    "oldRating": 3785,
    "newRating": 3815,
    "ratingChange": 30
  }
]
```

---

### Get Contest Overlap Results (Paginated)

**Method:** GET  
**URL:** `/friends/{handle}/contest-overlap/{contestId}/paginated`

**Description:**  
Get performance results of you and your friends in a specific contest, with pagination support.

**Path Parameters:**
- `handle` (required): Your Codeforces username
- `contestId` (required): Contest ID

**Query Parameters:**
- `page` (optional, default=0): Page number (0-indexed)
- `size` (optional, default=20): Items per page

**Headers:**
```json
{
  "Authorization": "Bearer <access_token>"
}
```

**Success Response (200 OK):**
```json
{
  "content": [
    {
      "handle": "myhandle",
      "rank": 145,
      "oldRating": 3650,
      "newRating": 3720,
      "ratingChange": 70
    },
    {
      "handle": "tourist",
      "rank": 12,
      "oldRating": 3920,
      "newRating": 3950,
      "ratingChange": 30
    },
    {
      "handle": "Petr",
      "rank": 87,
      "oldRating": 3785,
      "newRating": 3815,
      "ratingChange": 30
    }
  ],
  "page": {
    "size": 20,
    "number": 0,
    "totalElements": 3,
    "totalPages": 1
  }
}
```

---

## Insight Endpoints

### Get Weak Topics

**Method:** GET  
**URL:** `/insights/{handle}/weak-topics`

**Description:**  
Identify topics/tags where the user has low acceptance rate to focus practice on.

**Path Parameters:**
- `handle` (required): Codeforces username

**Headers:**
```json
{
  "Authorization": "Bearer <access_token>"
}
```

**Success Response (200 OK):**
```json
[
  {
    "tag": "divide and conquer",
    "totalAttempts": 23,
    "solvedCount": 4,
    "unsolvedCount": 19,
    "acRate": 17.4
  },
  {
    "tag": "suffix array",
    "totalAttempts": 15,
    "solvedCount": 3,
    "unsolvedCount": 12,
    "acRate": 20.0
  },
  {
    "tag": "square root decomposition",
    "totalAttempts": 28,
    "solvedCount": 8,
    "unsolvedCount": 20,
    "acRate": 28.6
  }
]
```

**Notes:**
- Sorted by acceptance rate (ascending)
- Focus on topics with acRate < 40% for improvement

---

### Get Problem Recommendations

**Method:** GET  
**URL:** `/insights/{handle}/recommendations`

**Description:**  
Get practice problems based on weak topics identified by the algorithm.

**Path Parameters:**
- `handle` (required): Codeforces username

**Headers:**
```json
{
  "Authorization": "Bearer <access_token>"
}
```

**Success Response (200 OK):**
```json
[
  {
    "contestId": 1853,
    "index": "C",
    "name": "Prefix Sum Problem",
    "rating": 2100,
    "tags": ["binary search", "prefix sums"]
  },
  {
    "contestId": 1800,
    "index": "D",
    "name": "Optimized Sorting",
    "rating": 2400,
    "tags": ["divide and conquer", "sorting"]
  },
  {
    "contestId": 1750,
    "index": "E",
    "name": "Advanced DP",
    "rating": 2600,
    "tags": ["divide and conquer", "dp"]
  }
]
```

**Notes:**
- Problems are selected from weak topics
- Sorted by rating difficulty (ascending)

---

### Get Upsolve Problems

**Method:** GET  
**URL:** `/insights/{handle}/upsolve`

**Description:**  
Get problems from recent contests that the user didn't solve (upsolving opportunities).

**Path Parameters:**
- `handle` (required): Codeforces username

**Headers:**
```json
{
  "Authorization": "Bearer <access_token>"
}
```

**Success Response (200 OK):**
```json
{
  "1865": [
    {
      "contestId": 1865,
      "index": "D",
      "name": "Yet Another Problem",
      "rating": 2800,
      "tags": ["binary search", "greedy", "sorting"],
      "bestVerdict": "WRONG_ANSWER",
      "url": "https://codeforces.com/contest/1865/problem/D"
    },
    {
      "contestId": 1865,
      "index": "F",
      "name": "Complex Algorithm",
      "rating": 3200,
      "tags": ["advanced", "divide and conquer", "segment tree"],
      "bestVerdict": "TIME_LIMIT_EXCEEDED",
      "url": "https://codeforces.com/contest/1865/problem/F"
    }
  ],
  "1860": [
    {
      "contestId": 1860,
      "index": "E",
      "name": "Math Problem",
      "rating": 2500,
      "tags": ["combinatorics", "math"],
      "bestVerdict": "WRONG_ANSWER",
      "url": "https://codeforces.com/contest/1860/problem/E"
    }
  ]
}
```

**Notes:**
- Returns problems grouped by contest ID
- `bestVerdict` indicates the best result when attempting
- Use `url` to navigate directly to problem

---

## Analysis Endpoints

### Analyze Upsolve Progress

**Method:** GET  
**URL:** `/analysis/upsolve/{handle}`

**Description:**  
AI-powered analysis of unsolved problems from recent contests with actionable tips using Groq AI.

**Path Parameters:**
- `handle` (required): Codeforces username

**Headers:**
```json
{
  "Authorization": "Bearer <access_token>"
}
```

**Success Response (200 OK):**
```json
{
  "problemAnalyses": [
    {
      "contestId": 1865,
      "problemIndex": "D",
      "problemName": "Yet Another Problem",
      "likelyIssue": "Implementation complexity leading to off-by-one errors in indexing logic",
      "conceptToStudy": "Careful boundary handling in iterative approaches and segment trees",
      "actionableTip": "Use 0-based indexing consistently and add assertions for boundary conditions"
    },
    {
      "contestId": 1865,
      "problemIndex": "F",
      "problemName": "Complex Algorithm",
      "likelyIssue": "TLE caused by inefficient graph traversal; current approach has O(n²) complexity",
      "conceptToStudy": "Binary Lifting, LCA (Lowest Common Ancestor), and Divide and Conquer optimization",
      "actionableTip": "Precompute using binary lifting to reduce query complexity to O(log n)"
    }
  ],
  "overallRecommendation": "Focus on mastering Binary Search and Divide & Conquer techniques. Practice problems in the 2600-2800 range specifically targeting these topics. Your implementations are correct but need optimization."
}
```

**Notes:**
- Uses AI (Groq) to analyze problem patterns
- Provides personalized learning recommendations
- Results are cached for 1 hour

---

## Verification Endpoints

### Send Phone Verification OTP

**Method:** POST  
**URL:** `/verification/send-otp`

**Description:**  
Request OTP for phone number verification. User will receive SMS with a 6-digit code. The request is rate-limited to prevent abuse (60-second cooldown per phone number/IP, max 5 requests per hour). OTP expires in 5 minutes.

**Headers:**
```json
{
  "Content-Type": "application/json",
  "Authorization": "Bearer <access_token>"
}
```

**Request Body:**
```json
{
  "phoneNumber": "+14155552671"
}
```

**Success Response (200 OK):**
```json
{
  "code": "OTP_SENT",
  "message": "OTP sent successfully to phone and email",
  "success": true
}
```

**Error Response (400 Bad Request):**
```json
{
  "code": "INVALID_INPUT",
  "message": "Invalid phone number format",
  "success": false
}
```

**Error Response (401 Unauthorized):**
```json
{
  "code": "UNAUTHORIZED",
  "message": "Authentication required",
  "success": false
}
```

**Error Response (429 Too Many Requests):**
```json
{
  "code": "RATE_LIMIT_EXCEEDED",
  "message": "Please wait 59s before requesting another verification OTP.",
  "success": false
}
```

**Notes:**
- Phone number must be in international format (e.g., +14155552671)
- OTP valid for 5 minutes only
- The phone number is not saved to the database immediately; instead, it is held temporarily in Redis under `pending_phone:<email>` with a 5-minute TTL.
- OTP is sent via Twilio SMS.
- Rate limits: 60-second cooldown per phone number/IP, max 5 sends per hour per phone number/IP.

---

### Verify Phone Number via OTP

**Method:** POST  
**URL:** `/verification/verify-otp`

**Description:**  
Verify phone number by submitting the OTP received via SMS.

**Headers:**
```json
{
  "Content-Type": "application/json",
  "Authorization": "Bearer <access_token>"
}
```

**Request Body:**
```json
{
  "otp": "123456"
}
```

**Success Response (200 OK):**
```json
{
  "code": "OTP_VERIFIED",
  "message": "Phone number successfully verified",
  "success": true
}
```

**Error Response (400 Bad Request - Invalid/Expired OTP):**
```json
{
  "code": "OTP_INVALID",
  "message": "Invalid or expired OTP",
  "success": false
}
```

**Error Response (400 Bad Request - Too Many Attempts):**
```json
{
  "code": "OTP_INVALID",
  "message": "Too many incorrect attempts. Please request a new verification code.",
  "success": false
}
```

**Validation Rules:**
- `otp`: Required, exactly 6 digits
- Must be submitted within 5 minutes of sending
- Maximum 5 attempts allowed before the session is locked and the pending number/OTP is removed.

**Notes:**
- After successful verification, the phone number is updated in the database and `phoneVerified` is set to `true`.
- After verification, user will receive contest SMS alerts and voice calls.
- `NotificationDispatcherService` checks `phoneVerified=true` before sending SMS/calls.
- OTP and pending phone are stored in Redis with a 5-minute TTL, automatically expiring if not verified.

---

## System Architecture & Core Business Flows

### System Overview

**AlgoLens** is a comprehensive Codeforces companion application designed to help competitive programmers track progress, analyze performance, compare with friends, and receive AI-powered insights. The system integrates multiple services:

- **Authentication & Security:** JWT-based auth with email verification, password reset, and phone verification via OTP
- **Codeforces Integration:** Real-time syncing of contests, problems, user profiles, and submission data
- **Notifications:** Email and SMS/voice alerts for upcoming contests via Twilio integration
- **AI Analysis:** Groq-powered analysis of unsolved problems with personalized recommendations
- **Friend Comparison:** Competitive analytics against friends with leaderboards and streak tracking

---

### Core Business Flows

#### Flow 1: User Registration & Email Verification

**Purpose:** Register a new user and verify their email address before granting API access

**Step-by-step Execution:**

1. **Frontend calls:** `POST /auth/register` with name, email, password
2. **Service Layer (AuthService):**
   - Email rate limiting check (60-second cooldown per email/IP, max 10 verification emails per hour per email/IP) via `RateLimiterService`
   - Hash email and check if user already exists
   - Create `PendingRegistration` record with hashed verification token (UUID)
   - Set token expiry to 24 hours
3. **Validation:**
   - Name: 2-100 characters, required
   - Email: Valid format, must be unique
   - Password: Minimum 8 characters, should be strong
4. **Database Operations:**
   - Insert into `pending_registration` table with encrypted password hash
5. **Response Returned:** `"Registration successful. Please check your email to verify your account"`
6. **EmailService Triggered:**
   - Sends verification link: `{baseUrl}/api/auth/verify-email?token={rawToken}`
7. **Frontend Action:**
   - Store email temporarily
   - Redirect to verification page or show verification pending message
   - User clicks email link (frontend reads token from URL query param)

**Token Verification Step:**

8. **Frontend calls:** `GET /auth/verify-email?token=xyz-token`
9. **Service Layer (AuthService):**
   - Hash token and find matching `PendingRegistration`
   - Check token expiry (24-hour TTL)
   - Create actual `User` record with `emailVerified=true`
   - Delete `PendingRegistration` record
10. **Response:** `"Email verified successfully. You can now log in."`
11. **Frontend Action:** Redirect to login page

**Flow Diagram (Textual):**
```
User → POST /auth/register 
  ↓ (Validate, check duplicate) 
  → EmailService.sendEmailVerification() 
  ↓
User clicks email link → GET /auth/verify-email?token=xyz 
  ↓ (Hash token, verify) 
  → Create User entity 
  ↓
Success → Redirect to login
```

---

#### Flow 2: User Login & Authentication

**Purpose:** Authenticate user and provide JWT tokens for API access

**Step-by-step Execution:**

1. **Frontend calls:** `POST /auth/login` with email, password (optional: X-Device-Id header)
2. **Service Layer (AuthService):**
   - Check login attempt counter (max 5 failures before 15-minute block, tracked persistingly in Redis) via `LoginAttemptService`
   - Authenticate using Spring Security with email + password
   - Load user from database
   - Check if email is verified (403 if not)
3. **Validation:**
   - Email format valid, password minimum 8 characters
   - Account must be email-verified
4. **Token Generation (JwtService & RefreshTokenService):**
   - Generate **Access Token** (1-hour expiry)
   - Generate **Refresh Token** (7-day expiry)
   - If a refresh token already exists for the user and device ID, it is rotated (the old token is marked as `used=true` and flushed, and a new one is created) to gracefully handle synchronous concurrent logins from the same device. Otherwise, a new record is created in the `refresh_tokens` table.
5. **Response Returned:**
   ```json
   {
     "accessToken": "eyJhbGci...",
     "refreshToken": "eyJhbGci..."
   }
   ```
6. **Frontend Action:**
   - Store accessToken in session storage (expires with browser session)
   - Store refreshToken in httpOnly secure cookie or local storage
   - Store user email/ID locally for context
   - Clear any login failure counters

**Flow Diagram (Textual):**
```
User → POST /auth/login
  ↓ (Check IP rate limit, authenticate)
  → Generate JWT tokens
  ↓ (Access: 1hr, Refresh: 7day)
  → Store in refresh_tokens table
  ↓
Return tokens → Frontend stores securely
```

---

#### Flow 3: Phone Verification & OTP

**Purpose:** Allow users to verify phone numbers for SMS/voice contest alerts

**Step-by-step Execution:**

1. **Frontend calls:** `POST /api/verification/send-otp` (authenticated)
   ```json
   {
     "phoneNumber": "+14155552671"
   }
   ```
2. **Service Layer (VerificationService):**
   - Check rate limits (60-second cooldown per phone number/IP, max 5 sends per hour per phone number/IP) via `RateLimiterService`
   - Generate secure 6-digit OTP using `SecureRandom`
   - Store OTP in Redis (`phone_otp:<email>`) with 5-minute TTL
   - Temporarily store the pending phone number in Redis (`pending_phone:<email>`) with a 5-minute TTL (does NOT write to database immediately)
3. **TwilioService Triggered:**
   - Sends SMS: `"Your AlgoLens verification code is: 123456"`
4. **EmailService Triggered:**
   - Sends backup email with OTP code
5. **Response:**
   ```json
   {
     "code": "OTP_SENT",
     "message": "OTP sent successfully to phone and email",
     "success": true
   }
   ```
6. **Frontend Action:**
   - Show OTP input form
   - Start 5-minute countdown timer
   - Allow "Resend OTP" after countdown (respecting rate limits)

**OTP Verification Step:**

7. **Frontend calls:** `POST /api/verification/verify-otp` (authenticated)
   ```json
   {
     "otp": "123456"
   }
   ```
8. **Service Layer (VerificationService):**
   - Retrieve OTP from Redis using user's email
   - Compare with submitted OTP, incrementing the attempt counter (max 5 attempts before locking)
   - If valid: Retrieve pending phone number from Redis, set user's `phoneNumber` and `phoneVerified=true` in database, delete OTP and pending phone from Redis
   - If invalid: Increment attempts counter. If attempts >= 5, delete OTP, pending phone, and attempt counter from Redis and lock out the verification session.
9. **Response:**
   ```json
   {
     "code": "OTP_VERIFIED",
     "message": "Phone number successfully verified",
     "success": true
   }
   ```
10. **Frontend Action:**
   - Show success message
   - Enable contest notification preferences

**Downstream Effect:**
- `NotificationDispatcherService` now sends SMS + voice alerts to this user for upcoming contests

**Flow Diagram (Textual):**
```
User → POST /api/verification/send-otp
  ↓ (Rate limit check)
  → Generate OTP & pending phone
  → Store in Redis (5-min TTL)
  → TwilioService.sendSms()
  → EmailService.sendOtpEmail()
  ↓
User → POST /api/verification/verify-otp
  ↓ (Hash check, increment attempts)
  → If valid: Read pending phone, update User (phoneNumber, phoneVerified=true)
  → Delete OTP & pending phone from Redis
  ↓
Success → Enable notifications
```

---

#### Flow 4: Password Reset (Forgot Password)

**Purpose:** Securely reset user password via email and OTP verification

**Step-by-step Execution:**

1. **Frontend calls:** `POST /auth/forgot-password`
   ```json
   {
     "email": "user@example.com"
   }
   ```
2. **Service Layer (PasswordResetService):**
   - Check email rate limit (5 requests/min)
   - Find user by email
   - If user exists AND email verified:
     - Generate secure JWT reset token (15-minute expiry)
     - Generate OTP (6 digits, 5-minute expiry)
     - Store in `password_reset_tokens` table with token hash, OTP hash, attempts counter
3. **Email Sent:** Contains reset link with JWT token
4. **Response:** `"If that email is registered, a password reset link has been sent."` (generic, for security)
5. **Frontend Action:** User clicks email link with token parameter

**Verify Reset Token Step:**

6. **Frontend calls:** `POST /auth/verify-reset-token`
   ```json
   {
     "token": "eyJhbGci..."
   }
   ```
7. **Service Layer (PasswordResetService):**
   - Parse JWT token
   - Verify token integrity using HMAC-SHA256
   - Check token hasn't been used before
   - Check token expiry
   - Generate new OTP, set 5-minute TTL
   - Send OTP via email
   - Set secure HTTP-only cookie `reset_session` with JTI (JWT ID)
8. **Response:** Generic success (HTTP 200)
9. **Frontend Action:** Show OTP input screen, start 5-min timer

**Reset Password Step:**

10. **Frontend calls:** `POST /auth/reset-password`
    ```json
    {
      "otp": "123456",
      "newPassword": "NewSecurePassword@123"
    }
    ```
11. **Service Layer (PasswordResetService):**
    - Read `reset_session` cookie (JTI)
    - Look up reset token by JTI
    - Increment OTP attempts counter
    - Check max 5 attempts not exceeded
    - Verify OTP hasn't expired
    - Compare OTP with stored hash (uses PasswordEncoder)
    - Check new password differs from current password
    - Encode and save new password
    - Mark token as used
    - Delete ALL refresh tokens for this user (forces re-login everywhere)
    - Expire session cookie
12. **Response:** `"Password reset successfully. Please log in with your new password."`
13. **Frontend Action:** Clear all stored tokens, redirect to login

**Flow Diagram (Textual):**
```
User → POST /auth/forgot-password
  ↓ (Rate limit check)
  → Generate JWT reset token (15 min)
  → Generate OTP (5 min)
  → Email sent with reset link
  ↓
User clicks link → POST /auth/verify-reset-token
  ↓ (Verify JWT, integrity check, check used)
  → Generate new OTP
  → Set reset_session cookie
  → Send OTP email
  ↓
User submits OTP → POST /auth/reset-password
  ↓ (OTP verify, increment attempts)
  → Encode new password
  → Invalidate all refresh tokens (logout everywhere)
  ↓
Success → User must re-login
```

---

#### Flow 5: Codeforces Data Synchronization (Background)

**Purpose:** Keep contest data fresh and notify users of upcoming contests

**Scheduled Execution (Every hour):**

1. **CodeforcesSyncService** (runs on fixed schedule: 3600000ms = 1 hour, `@Transactional`)
   - Fetch all existing contests from database in a single query and index them in a Map for $O(1)$ lookups
   - Call `CodeforcesApiClient.getContests()`
   - Filter for contests in "BEFORE" phase (not started yet)
2. **Database Operations:**
   - For each contest: Create or update `Contest` record with `isActive = true`, Codeforces contest ID, name, start time, type, and phase.
   - Compare with existing database records: if a contest was previously marked as `isActive = true` but is no longer returned in the incoming "BEFORE" phase (meaning it started or was removed), update `isActive = false`.
   - Batch save all modified/new contests to database.
   - Automatically delete finished/inactive contests older than 30 days.
3. **Logging:** Log count of synced and deactivated contests.

**Contest Notification Dispatch (Every minute):**

4. **NotificationDispatcherService** (runs cron: every minute)
   - Current time in UTC seconds
   - Find only active contests (`isActive = true`) starting between NOW+270s and NOW+330s (5-minute window)
   - Get all users where:
     - Email verified = true
     - Notify before contest = true (user preference)
5. **For each eligible user & contest:**
   - Hand off task execution to Java 21's Virtual Threads (`Executors.newVirtualThreadPerTaskExecutor()`) to process dispatches concurrently in parallel without blocking.
   - For each user in parallel:
     - **EmailService:** Send contest reminder email (with isolated try-catch error handling).
     - **TwilioService:** Send SMS if phone is verified (with isolated try-catch error handling).
     - **TwilioService:** Make voice call with contest message if phone is verified (with isolated try-catch error handling).
6. **Logging:** Log dispatch and error counts.

**No Frontend Action:** This is automatic background process

**Flow Diagram (Textual):**
```
[Hourly] CodeforcesSyncService.syncContestsWithStatusUpdate()
  ↓ (Call Codeforces API)
  → Index existing contests & fetch incoming ones
  → Update properties, set isActive=false for started contests
  → Delete contests older than 30 days
  → Batch insert/update
  ↓
[Every Minute] NotificationDispatcherService.dispatchNotifications()
  ↓ (Filter active contests in 5-min window)
  → Load eligible users (email verified, opted-in)
  → Hand off to Virtual Thread Executor
  → Concurrently per user (with isolated error handling):
    → EmailService.sendContestNotificationEmail()
    → TwilioService.sendSms() (if phone verified)
    → TwilioService.makeAgenticCall() (if phone verified)
```

---

#### Flow 6: Fetching User Profile & Building Dashboard

**Purpose:** Display Codeforces user stats on dashboard

**Step-by-step Execution:**

1. **Frontend calls:** `GET /users/{handle}/profile` (authenticated)
   - Path parameter: handle = Codeforces username (e.g., "tourist")
2. **Service Layer (UserController → UserService):**
   - Call `CodeforcesApiClient.getUserInfo(handle)`
   - Cache result for 5 minutes (Redis)
   - Return user profile data
3. **Response Returned:**
   ```json
   {
     "handle": "tourist",
     "rating": 3950,
     "maxRating": 3957,
     "rank": "International Master",
     "problemsSolved": 2847,
     "contestsParticipated": 450,
     "streakDays": 127,
     "lastActiveDate": "2026-04-13",
     "avatar": "https://codeforces.com/avatar/tourist.jpg"
   }
   ```
4. **Frontend Action:** Display profile card with all stats

**Get Contest History:**

5. **Frontend calls:** `GET /users/{handle}/contest-history/paginated` (authenticated)
6. **Service Layer:** Fetch contests user participated in with rating changes, paginated
7. **Response:** Page object wrapping contest records with rating deltas
8. **Frontend Action:** Display as list or chart

**Get Rating Graph Data:**

9. **Frontend calls:** `GET /users/{handle}/rating-graph` (authenticated)
10. **Response:** Array of data points (contestId, rating, date) for plotting
11. **Frontend Action:** Plot line chart showing rating progression

**Get Submission Stats:**

12. **Frontend calls:** `GET /users/{handle}/submission-stats` (authenticated)
13. **Response:** Total submissions, solved problems, verdict breakdown
14. **Frontend Action:** Display as progress bars or pie chart

**Flow Diagram (Textual):**
```
Frontend loads dashboard
  ↓
GET /users/{handle}/profile
  → [Cache 5 min] CodeforcesApiClient
  → Display profile card
  ↓
GET /users/{handle}/rating-graph
  → Plot rating chart
  ↓
GET /users/{handle}/submission-stats
  → Display verdict breakdown
  ↓
GET /users/{handle}/contest-history/paginated
  → Show recent contests table (with pagination controls)
```

---

#### Flow 7: Adding Friends & Building Leaderboard

**Purpose:** Track and compare with competitive friends

**Step-by-step Execution:**

1. **Frontend calls:** `POST /friends/add` (authenticated)
   ```json
   {
     "userHandle": "myhandle",
     "friendHandle": "tourist"
   }
   ```
2. **Service Layer (FriendController → FriendService):**
   - Validate both handles exist on Codeforces
   - Check if friendship already exists (409 if duplicate)
   - Create `Friend` relationship record
3. **Response:** `"Friend added successfully"`
4. **Frontend Action:** Refresh friends list, show success toast

**Get Friends List & Leaderboard:**

5. **Frontend calls:** `GET /friends/{handle}` (authenticated)
6. **Response:** Array of friends with current ratings
7. **Frontend calls:** `GET /friends/{handle}/leaderboard` (authenticated)
8. **Service Layer:**
   - Get user's Codeforces profile
   - Get all friends' profiles
   - Sort by rating descending
   - Assign ranks
9. **Response:** Ranked leaderboard with user in position
10. **Frontend Action:** Display leaderboard table with your position highlighted

**Get Unsolved Problems by Friends:**

11. **Frontend calls:** `GET /friends/{handle}/unsolved-by-me/paginated` (authenticated)
12. **Service Layer:**
    - Get problems solved by each friend
    - Get problems solved by you
    - Find problems friends solved but you haven't
    - Group by difficulty/tags and paginate
13. **Response:** Page object wrapping problems with friend names who solved each
14. **Frontend Action:** Display practice suggestions with "Solved by:" badges

**Compare Streaks:**

15. **Frontend calls:** `GET /friends/{handle}/streak-compare` (authenticated)
16. **Response:** All friends sorted by current streak (consecutive days with submissions)
17. **Frontend Action:** Display streak comparison, show who's leading

**Get Contest Overlap Results:**

18. **Frontend calls:** `GET /friends/{handle}/contest-overlap/{contestId}` (authenticated)
19. **Response:** All friends' results in that specific contest (rank, rating change)
20. **Frontend Action:** Show who performed best in that contest

**Remove Friend:**

21. **Frontend calls:** `DELETE /friends/{userHandle}/remove/{friendHandle}` (authenticated)
22. **Response:** `"Friend removed successfully"`
23. **Frontend Action:** Update friends list

**Flow Diagram (Textual):**
```
User → POST /friends/add (friendHandle=tourist)
  ↓ (Validate handles, check duplicate)
  → Create Friend relationship
  ↓
GET /friends/{handle}
  → Display all friends
  ↓
GET /friends/{handle}/leaderboard
  → Sort by rating
  → Display ranked leaderboard
  ↓
GET /friends/{handle}/unsolved-by-me/paginated
  → Show practice suggestions
  ↓
GET /friends/{handle}/streak-compare
  → Display streak competition
```

---

#### Flow 8: AI-Powered Analysis of Unsolved Problems

**Purpose:** Get AI-generated insights on why unsolved problems are difficult

**Step-by-step Execution:**

1. **Frontend calls:** `GET /analysis/upsolve/{handle}` (authenticated)
   - handle = Codeforces username
2. **Service Layer (AnalysisController → AnalysisService):**
   - Get recent contests user participated in
   - Identify problems with non-AC (non-accepted) verdicts
   - Collect problem metadata (name, tags, rating, submission verdict)
   - Call `GroqClient.analyzeUpsolveProblems(problemsList)`
3. **Groq AI Processing:**
   - Sends prompt to Groq AI API with problem details
   - Receives analysis for each problem with:
     - Likely issue (why the approach failed)
     - Concept to study (algorithm/data structure to learn)
     - Actionable tip (specific debugging advice)
4. **Cache:** Result cached for 1 hour in Redis
5. **Response Returned:**
   ```json
   {
     "problemAnalyses": [
       {
         "contestId": 1865,
         "problemIndex": "D",
         "problemName": "Yet Another Problem",
         "likelyIssue": "Implementation complexity leading to off-by-one errors in indexing logic",
         "conceptToStudy": "Careful boundary handling in iterative approaches and segment trees",
         "actionableTip": "Use 0-based indexing consistently and add assertions for boundary conditions"
       }
     ],
     "overallRecommendation": "Focus on mastering Binary Search and Divide & Conquer techniques. Practice problems in the 2600-2800 range specifically targeting these topics. Your implementations are correct but need optimization."
   }
   ```
6. **Frontend Action:**
   - Display each problem's analysis with styled cards
   - Highlight overall recommendation
   - Add links to problem editorials and relevant tutorials

**Flow Diagram (Textual):**
```
User → GET /analysis/upsolve/{handle}
  ↓ (Check Redis cache)
  → Fetch recent contests
  → Get unsolved problems (non-AC verdicts)
  → GroqClient.analyzeUpsolveProblems()
  ↓ (Call Groq AI API)
  → Receive AI analysis
  → Cache for 1 hour
  ↓
Display insights → Frontend renders analysis cards
```

---

#### Flow 9: Problem Recommendations & Weak Topics

**Purpose:** Suggest problems to practice based on weak areas

**Step-by-step Execution:**

1. **Frontend calls:** `GET /insights/{handle}/weak-topics` (authenticated)
2. **Service Layer (InsightService):**
   - Analyze user's submission history
   - Group by problem tags (e.g., "binary search", "DP", "graphs")
   - Calculate AC rate per tag (solved / attempts)
   - Sort by acceptance rate (ascending)
   - Filter topics with < 40% AC rate
3. **Response Returned:**
   ```json
   [
     {
       "tag": "divide and conquer",
       "totalAttempts": 23,
       "solvedCount": 4,
       "unsolvedCount": 19,
       "acRate": 17.4
     }
   ]
   ```
4. **Frontend Action:** Display weak topics sorted by difficulty level

**Get Problem Recommendations:**

5. **Frontend calls:** `GET /insights/{handle}/recommendations` (authenticated)
6. **Service Layer:**
   - Identify weak topics (from above)
   - Search Codeforces for problems with those tags
   - Sort by rating difficulty (ascending)
   - Return 10-20 practice problems
7. **Response:** Array of problems with tags matching weak topics
8. **Frontend Action:** Display as practice queue with difficulty badges

**Get Upsolve Opportunities:**

9. **Frontend calls:** `GET /insights/{handle}/upsolve` (authenticated)
10. **Service Layer:**
    - Get recent contests (last 7-14 days)
    - Find problems with non-AC verdicts
    - Group by contest ID
    - Include problem metadata and best verdict achieved
11. **Response:**
    ```json
    {
      "1865": [
        {
          "contestId": 1865,
          "index": "D",
          "name": "Yet Another Problem",
          "rating": 2800,
          "tags": ["binary search", "greedy"],
          "bestVerdict": "WRONG_ANSWER",
          "url": "https://codeforces.com/contest/1865/problem/D"
        }
      ]
    }
    ```
12. **Frontend Action:** Display problems grouped by contest, link directly to problem

**Flow Diagram (Textual):**
```
GET /insights/{handle}/weak-topics
  → Analyze submissions by tag
  → Calculate AC rate per tag
  → Filter AC rate < 40%
  ↓
GET /insights/{handle}/recommendations
  → Search problems matching weak topics
  → Sort by difficulty
  → Display practice queue
  ↓
GET /insights/{handle}/upsolve
  → Get recent contests (7-14 days)
  → Find non-AC verdicts
  → Group by contest ID
  → Display upsolve opportunities
```

---

## Notes for Frontend Developers

### Authentication Flow

```
1. User Registration
   POST /auth/register
   ↓
2. Email Verification
   GET /auth/verify-email?token=xyz
   ↓
3. Login
   POST /auth/login
   ↓ (Response: accessToken, refreshToken)
4. Store Tokens
   - accessToken: Session storage (expires 1 hour)
   - refreshToken: Secure cookie or local storage
5. API Requests
   All requests include: Authorization: Bearer <accessToken>
   ↓
6. Token Refresh
   When 401 response: POST /auth/refresh
   ↓
7. Logout
   POST /auth/logout-all (clears all sessions)
```

### Error Handling

All error responses follow this structure:

```json
{
  "timestamp": "ISO-8601 datetime",
  "status": "HTTP status code",
  "error": "Error category",
  "message": "Detailed message",
  "path": "Endpoint path"
}
```

**Common Status Codes:**
- `200` - Success
- `201` - Created
- `400` - Bad Request (validation error)
- `401` - Unauthorized (invalid/expired token)
- `403` - Forbidden (not allowed)
- `404` - Not Found
- `409` - Conflict (resource already exists)
- `422` - Unprocessable Entity
- `429` - Too Many Requests (rate limited)
- `500` - Server Error

### Rate Limiting

- **General endpoints:** 100 requests/minute per IP
- **Email Verification / Resend:** 60-second cooldown per email/IP, max 10 verification emails per hour.
- **Phone Verification OTP:** 60-second cooldown per phone number/IP, max 5 OTP sends per hour.
- **Login attempts:** Blocked for 15 minutes (900 seconds) after 5 failed login attempts per IP (tracked persistingly in Redis).
- **Codeforces API calls:** Respects Codeforces limits (2 req/sec)

### Caching

- **User Profile:** 5 minutes
- **Contest Data:** 1 hour
- **Analysis Results:** 1 hour

Clear cache by logging out and logging back in.

### Pagination

Endpoints returning lists support pagination:
```
?page=0&size=20
```

Response format:
```json
{
  "content": [...],
  "page": {
    "size": 20,
    "number": 0,
    "totalElements": 1000,
    "totalPages": 50
  }
}
```

### Best Practices

1. **Store tokens securely:**
   - Access token: Session storage
   - Refresh token: HttpOnly secure cookie

2. **Implement refresh logic:**
   - Catch 401 responses
   - Auto-refresh using refresh token
   - Retry original request

3. **Handle rate limiting:**
   - Implement exponential backoff
   - Show user-friendly message

4. **Cache user data:**
   - Profile data on login
   - Update on user action
   - Clear on logout

5. **Error user feedback:**
   - Show message from API response
   - Don't expose server details
   - Suggest recovery actions

### Example: Complete Login & API Call Flow

```javascript
// 1. Register
const register = async (name, email, password) => {
  const res = await fetch('http://localhost:8080/api/auth/register', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ name, email, password })
  });
  return res.text();
};

// 2. Verify Email
const verifyEmail = async (token) => {
  const res = await fetch(
    `http://localhost:8080/api/auth/verify-email?token=${token}`
  );
  return res.text();
};

// 3. Login
const login = async (email, password) => {
  const res = await fetch('http://localhost:8080/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password })
  });
  return res.json();
};

// 4. Get User Profile (with token)
const getUserProfile = async (handle, accessToken) => {
  const res = await fetch(
    `http://localhost:8080/api/users/${handle}/profile`,
    {
      headers: {
        'Authorization': `Bearer ${accessToken}`,
        'Content-Type': 'application/json'
      }
    }
  );
  
  if (res.status === 401) {
    // Token expired, refresh it
    const newToken = await refreshToken();
    return getUserProfile(handle, newToken);
  }
  
  return res.json();
};

// 5. Refresh Token
const refreshToken = async () => {
  const refreshToken = localStorage.getItem('refreshToken');
  const res = await fetch('http://localhost:8080/api/auth/refresh', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ refreshToken })
  });
  
  const data = await res.json();
  sessionStorage.setItem('accessToken', data.accessToken);
  localStorage.setItem('refreshToken', data.refreshToken);
  
  return data.accessToken;
};
```

### Using Swagger UI

Interactive API documentation is available at:
```
http://localhost:8080/swagger-ui/index.html
```
*(or `http://localhost:8080/swagger-ui.html` which redirects to the UI path)*

Try endpoints directly from the browser:
1. Click the "Authorize" button (configured to support OpenAPI 3 Bearer JWT Scheme).
2. Paste your access token (obtained from login/registration).
3. Try endpoints with pre-filled parameters.

---

## Support

For issues or questions:
1. Check error message in response
2. Review request format in this documentation
3. Verify authentication token is valid
4. Check rate limiting (wait and retry)
5. Ensure Codeforces handle exists

---

## Frontend Integration Guide (Critical Implementation Path)

### Recommended Implementation Order

Follow this exact sequence to implement a fully-functional frontend:

**Phase 1: Authentication (Must Complete First)**
1. Implement registration form with email, name, password validation
2. Implement email verification page (reads token from URL query params)
3. Implement login form with email/password
4. Store tokens securely (access token in sessionStorage, refresh token in httpOnly cookie)
5. Implement token refresh logic for 401 responses
6. Implement logout (single device + all devices)

**Phase 2: Core Dashboard**
1. Create dashboard page after successful login
2. Get user's Codeforces handle (either ask in registration or in onboarding)
3. Fetch and display user profile (`GET /users/{handle}/profile`)
4. Fetch and display rating graph (`GET /users/{handle}/rating-graph`)
5. Fetch and display submission stats (`GET /users/{handle}/submission-stats`)
6. Fetch and display contest history (`GET /users/{handle}/contest-history/paginated`)

**Phase 3: Friend Features**
1. Create "Add Friend" form (input friend's Codeforces handle)
2. Fetch and display friends list (`GET /friends/{handle}`) or paginated (`GET /friends/{handle}/paginated`)
3. Fetch and display leaderboard (`GET /friends/{handle}/leaderboard`) or paginated (`GET /friends/{handle}/leaderboard/paginated`)
4. Implement friend removal feature (`DELETE /friends/{handle}/remove/{friendHandle}`)
5. Fetch and display unsolved problems (`GET /friends/{handle}/unsolved-by-me/paginated`)
6. Fetch and display streak comparison (`GET /friends/{handle}/streak-compare`) or paginated (`GET /friends/{handle}/streak-compare/paginated`)

**Phase 4: Analytics & Insights**
1. Create "Insights" page
2. Fetch weak topics (`GET /insights/{handle}/weak-topics`)
3. Fetch recommendations (`GET /insights/{handle}/recommendations`)
4. Fetch upsolve opportunities (`GET /insights/{handle}/upsolve`)
5. Create "Analysis" page for AI insights (`GET /analysis/upsolve/{handle}`)

**Phase 5: Contest Tracking & Notifications**
1. Fetch upcoming contests (`GET /contests/upcoming`)
2. Implement contest notification toggle (requires backend endpoint to save preference)
3. Implement phone verification flow:
   - Add phone number input to account settings
   - Call `POST /verification/send-otp` (handle rate-limiting errors and parse the new `ApiResponse` JSON format)
   - Display OTP input modal with a 5-minute timer
   - Call `POST /verification/verify-otp` with the 6-digit OTP code (parse the success or attempt/locked-out error codes from `ApiResponse`)
4. Show confirmed phone verification status

**Phase 6: Advanced Features**
1. Implement contest comparison (`GET /compare/rating`)
2. Implement submission comparison (`POST /compare/find`)
3. Implement contest overlap view (`GET /friends/{handle}/contest-overlap/{contestId}`) or paginated (`GET /friends/{handle}/contest-overlap/{contestId}/paginated`)
4. Implement pagination for contests (`GET /contests?page=0&size=20`) and upcoming contests (`GET /contests/upcoming/paginated`)

**Phase 7: Account Management**
1. Implement password reset flow:
   - "Forgot Password" link on login page
   - Call `POST /auth/forgot-password`
   - User receives email, clicks link with token
   - Call `POST /auth/verify-reset-token`
   - Show OTP input screen
   - Call `POST /auth/reset-password`
2. Implement resend verification email (`POST /auth/resend-verification`)

---

### State Management & Token Handling

**What to Store Locally (After Login):**
```javascript
// Session Storage (auto-clears on browser close)
sessionStorage.setItem('accessToken', response.accessToken);

// Local Storage or HttpOnly Cookie
localStorage.setItem('refreshToken', response.refreshToken);

// Optional: User context for quick access
sessionStorage.setItem('userEmail', userEmail);
sessionStorage.setItem('userHandle', codeforcesHandle);
```

**Interceptor/Middleware Pattern (Pseudo-code):**
```javascript
// Before every API call:
if (endpoint.requiresAuth) {
  const token = sessionStorage.getItem('accessToken');
  headers['Authorization'] = `Bearer ${token}`;
}

// After response:
if (response.status === 401) {
  const newToken = await refreshAccessToken();
  retryRequest(originalRequest, newToken);
}

// On logout:
sessionStorage.removeItem('accessToken');
localStorage.removeItem('refreshToken');
sessionStorage.removeItem('userEmail');
sessionStorage.removeItem('userHandle');
// Redirect to login
```

---

### Error Handling Strategy

**Always check response.status before parsing JSON:**
```javascript
const handleApiError = async (response) => {
  if (response.ok) {
    return response.json();
  }

  if (response.status === 401) {
    // Token expired
    redirectToLogin();
    return;
  }

  if (response.status === 429) {
    // Rate limited
    showUserMessage("Too many requests. Please wait before retrying.");
    return;
  }

  const errorData = await response.json();
  showUserMessage(errorData.message || "An error occurred");
  log(errorData);
};
```

**User-Friendly Error Messages (Show from API):**
- DO: Display `error.message` from API response
- DON'T: Show stack traces or server details
- DO: Suggest recovery action (e.g., "Email not verified. Click resend.")

---

### Common Frontend Implementation Mistakes to Avoid

**❌ MISTAKE 1: Not Implementing Token Refresh**
```javascript
// WRONG - will fail after 1 hour
fetch('/api/users/profile', {
  headers: { 'Authorization': `Bearer ${accessToken}` }
});

// RIGHT - check for 401 and refresh
if (response.status === 401) {
  const newAccessToken = await refreshToken();
  retryRequest(newAccessToken);
}
```

**❌ MISTAKE 2: Storing Sensitive Data in localStorage**
```javascript
// WRONG - refresh token exposed to XSS
localStorage.setItem('refreshToken', refreshToken);

// RIGHT - use httpOnly cookie (set by server) or sessionStorage
// (or implement server-side session management)
```

**❌ MISTAKE 3: Not Validating Input Before API Call**
```javascript
// WRONG - API rejects invalid data
POST /auth/register
{ "email": "invalid", "password": "short" }

// RIGHT - validate on frontend first
if (!email.includes('@')) showError('Invalid email');
if (password.length < 8) showError('Password too short');
```

**❌ MISTAKE 4: Calling API on Every Page Load Without Caching**
```javascript
// WRONG - hammers backend
useEffect(() => {
  fetch('/users/profile').then(...);
}, []); // Called on every render

// RIGHT - cache and use state
const [profile, setProfile] = useState(null);
useEffect(() => {
  if (!profile) fetch('/users/profile').then(setProfile);
}, [profile]);
```

**❌ MISTAKE 5: Not Handling null/undefined Responses**
```javascript
// WRONG - crashes if profile is null
{profile.handle}

// RIGHT - check before rendering
{profile?.handle || 'Loading...'}
```

**❌ MISTAKE 6: Making Requests Without Authentication Header**
```javascript
// WRONG - 403 Forbidden on protected endpoints
fetch('/friends/add', { method: 'POST', body: {...} });

// RIGHT - always include auth header for protected routes
fetch('/friends/add', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${accessToken}`,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({...})
});
```

**❌ MISTAKE 7: Not Handling Contest Timestamp Conversion**
```javascript
// WRONG - displays raw Unix timestamp
<p>Contest starts at: {contest.startTimeSeconds}</p>

// RIGHT - convert to readable date
const date = new Date(contest.startTimeSeconds * 1000);
<p>Contest starts at: {date.toLocaleString()}</p>
```

**❌ MISTAKE 8: Ignoring Rate Limit Headers**
```javascript
// WRONG - spams requests until 429
for (let i = 0; i < 100; i++) {
  fetch('/api/endpoint');
}

// RIGHT - implement backoff
let delay = 1000;
async function makeRequestWithBackoff(url) {
  const res = await fetch(url);
  if (res.status === 429) {
    delay *= 2;
    await sleep(delay);
    return makeRequestWithBackoff(url);
  }
  return res;
}
```

---

### Flow: How Frontend Should Call APIs in Sequence

**User Registration to Dashboard:**
```
1. User lands on signup page
2. User submits: POST /auth/register
   → Receive: "Registration successful"
   → Frontend: Show "Check email" message

3. User clicks email link (includes ?token=xyz)
   → Frontend: POST /auth/verify-email?token=xyz
   → Receive: "Email verified"
   → Frontend: Redirect to login

4. User submits login form: POST /auth/login
   → Receive: { accessToken, refreshToken }
   → Frontend: Store tokens
   → Frontend: Redirect to dashboard

5. Dashboard loads, frontend needs to fetch data
   → GET /users/{handle}/profile
   → GET /users/{handle}/rating-graph
   → GET /users/{handle}/submission-stats
   → GET /users/{handle}/contest-history/paginated
   → All include: Authorization: Bearer <accessToken>

6. Frontend should parallelize these GET requests (not sequential)

7. User goes to Friends page
   → GET /friends/{handle}/paginated
   → GET /friends/{handle}/leaderboard/paginated
   → GET /friends/{handle}/unsolved-by-me/paginated
   → GET /friends/{handle}/streak-compare/paginated

8. User adds a friend
   → POST /friends/add with { userHandle, friendHandle }
   → Refresh friends list after success
```

---

#### Authentication Edge Cases

**Case 1: User Opens App in New Tab**
- Access token may be expired
- Frontend should make a test API call
- If 401, call `POST /auth/refresh`
- If refresh fails (refresh token expired), redirect to login

**Case 2: User's Refresh Token Expires (7 days)**
- Next API call returns 401
- `POST /auth/refresh` also fails
- Frontend must redirect to login page
- User must re-authenticate

**Case 3: User Changed Password on Another Device**
- After password reset, ALL refresh tokens are invalidated
- User on this device gets 401
- `POST /auth/refresh` fails
- Frontend redirects to login
- User must log in again (security feature)

**Case 4: User Logged Out All Devices**
- Similar to Case 3
- All refresh tokens invalidated
- User on any device must re-login

---

### Phone Verification & Notifications Flow

**After User Verifies Phone Number:**
```
1. User goes to Settings → Phone Verification
2. User enters phone: +14155552671
3. Frontend: POST /verification/send-otp
   → Receive: "OTP sent"
   → Frontend: Show OTP input modal

4. User receives SMS: "Your code is: 123456"
5. User enters OTP in modal
6. Frontend: POST /verification/verify-otp with { otp }
   → Receive: "Phone verified"
   → Frontend: Update UI to show "✓ Verified"

7. Backend automatically enables notifications
   → CodeforcesSyncService runs every hour
   → NotificationDispatcherService runs every minute
   → When contest starting in 5 minutes:
     → Backend sends Email ✓
     → Backend sends SMS (because phoneVerified=true) ✓
     → Backend makes Voice call (because phoneVerified=true) ✓

NO FURTHER FRONTEND ACTION NEEDED - it's automatic!
```

---

### Recommended Tech Stack Considerations

**Frontend Framework:**
- React / Vue / Angular - all work equally well
- Use TypeScript for better type safety with DTOs

**State Management:**
- Context API + useReducer for simple apps
- Redux or Zustand for complex apps
- TanStack Query (React Query) for server state + caching

**HTTP Client:**
- Axios with interceptors for auto-refresh logic
- Fetch API with custom wrapper
- TanStack Query for automatic caching

**Form Validation:**
- React Hook Form + Zod (strongly recommended)
- Formik + Yup (alternative)
- HTML5 validation as fallback

**Charting Library:**
- Chart.js / Recharts for rating graphs
- react-calendar-heatmap for streak visualization

**Authentication Library:**
- Auth0 (if integrating external auth)
- Custom JWT logic (already described above)
- NextAuth.js (if using Next.js)

---

## System Assumptions & Inferred Behavior

### Assumptions Made During Analysis

1. **User Registration Flow**
   - ASSUMED: `PendingRegistration` table stores incomplete registrations until email is verified
   - ASSUMED: Token is hashed before storage (SHA256 or similar)
   - ASSUMED: 24-hour TTL for verification tokens

2. **Authentication & JWT**
   - ASSUMED: Access tokens stored in memory/sessionStorage (short-lived)
   - ASSUMED: Refresh tokens stored in secure httpOnly cookies (long-lived)
   - ASSUMED: JwtService uses HS256 algorithm
   - ASSUMED: Device ID tracked for multi-device logout support

3. **Password Reset Security**
   - ASSUMED: Reset tokens are JWT with HMAC-SHA256 integrity check
   - ASSUMED: OTP stored with `PasswordEncoder` (bcrypt or similar)
   - ASSUMED: Maximum 5 OTP attempts before forcing new reset request
   - ASSUMED: Resetting password invalidates ALL refresh tokens (logout everywhere)

4. **Codeforces API Integration**
   - ASSUMED: `CodeforcesApiClient` wraps official Codeforces API (https://codeforces.com/api/)
   - ASSUMED: API calls respect Codeforces rate limits (2 req/sec max)
   - ASSUMED: Contest data is cached for 1 hour to avoid hammering Codeforces

5. **Background Services (Verified)**
   - IMPLEMENTED: `CodeforcesSyncService` runs on a fixed 1-hour schedule, `@Transactional` to update contest status (marking active/inactive contests) and cleaning up finished contests older than 30 days.
   - IMPLEMENTED: `NotificationDispatcherService` runs every minute via cron, using Java 21's Virtual Threads to asynchronously dispatch email, SMS, and voice call notifications in parallel.

6. **Notifications via Twilio & Email (Verified)**
   - IMPLEMENTED: SMS and Voice calls are sent only if `phoneVerified=true` and `phoneNumber` is not null.
   - IMPLEMENTED: Email, SMS, and Voice calls are dispatched in parallel with isolated try-catch blocks so a failure in one notification method or recipient does not block others.
   - IMPLEMENTED: OTP and the pending phone number are stored in Redis with a 5-minute TTL (auto-expiry).

7. **Groq AI Analysis**
   - ASSUMED: `GroqClient` calls Groq AI API (https://console.groq.com/)
   - ASSUMED: Results are cached for 1 hour in Redis
   - ASSUMED: Analysis takes 5-30 seconds (frontend should show loading spinner)
   - ASSUMED: Analysis is best-effort (graceful fallback if Groq is down)

8. **Email Service**
   - ASSUMED: `EmailService` uses Spring Mail (SMTP configuration)
   - ASSUMED: Verification emails include clickable link with token
   - ASSUMED: Password reset emails include OTP code
   - ASSUMED: OTP sent to BOTH email and SMS (redundancy)

9. **Rate Limiting (Verified)**
   - IMPLEMENTED: Email verification requests (register, resend) are rate-limited via Redis with a 60-second cooldown and maximum 10 sends per hour per email/IP.
   - IMPLEMENTED: Phone verification OTP requests are rate-limited via Redis with a 60-second cooldown and maximum 5 sends per hour per phone number/IP.
   - IMPLEMENTED: Login attempts are persistingly tracked in Redis with a maximum of 5 failed attempts per IP, after which the IP is blocked from login attempts for 15 minutes.
   - ASSUMED: General endpoints are rate-limited at 100 requests/minute per IP.

10. **Database Architecture**
    - ASSUMED: Tables: users, pending_registration, refresh_tokens, password_reset_tokens, friends, contests
    - ASSUMED: User table has fields: email, password, name, emailVerified, phoneNumber, phoneVerified, etc.
    - ASSUMED: Contests table synced hourly from Codeforces API
    - ASSUMED: Friends table stores many-to-many relationships between users

11. **Caching Strategy**
    - ASSUMED: User profiles cached for 5 minutes
    - ASSUMED: Contest data cached for 1 hour
    - ASSUMED: Analysis results cached for 1 hour
    - ASSUMED: Cache keys include user handle to prevent cross-user contamination

12. **Error Handling Patterns**
    - ASSUMED: All errors return standard JSON structure with timestamp, status, message
    - ASSUMED: 400 = validation error (request body malformed)
    - ASSUMED: 401 = authentication error (token missing/expired)
    - ASSUMED: 403 = authorization error (user lacks permission)
    - ASSUMED: 404 = resource not found
    - ASSUMED: 409 = conflict (duplicate resource)
    - ASSUMED: 422 = unprocessable entity (business logic error)

### Inferred Business Logic

**User Lifecycle:**
1. Register → Pending Registration
2. Verify Email → Active User
3. Login → Get Tokens
4. Token Expiry → Refresh Tokens
5. Password Reset → Invalidate ALL Tokens
6. Logout All → Clear All Tokens

**Friend Relationship Constraints:**
- Cannot add yourself as friend
- Cannot add duplicate friend (409 error)
- Removing friend requires both userHandle and friendHandle

**Contest Notification Rules:**
- Only sent to users with emailVerified=true
- Only sent to users with notifyBeforeContest=true (user preference)
- Only sent 5 minutes before contest starts (270-330 second window)
- SMS/voice only sent if phoneVerified=true

**Streak Calculation:**
- Consecutive days with at least one accepted submission
- Resets if no submission for 24+ hours
- Last submission date tracked to determine current streak

**Weak Topics Analysis:**
- AC rate = solved / total attempts (percentage)
- Topics with AC rate < 40% marked as weak
- Sorted by AC rate (lowest first)

**Upsolve Analysis:**
- Includes problems from contests in last 7-14 days
- Only problems with non-AC verdicts (WRONG_ANSWER, TLE, etc.)
- Grouped by contest ID for organization

### Potential Production Considerations (Not Implemented)

1. **Metrics & Monitoring (Partially Implemented)**
   - IMPLEMENTED: Spring Boot Actuator is added with the `/actuator/health` endpoint exposed to monitor application health.
   - IMPLEMENTED: SLF4J with Lombok `@Slf4j` is used extensively for logging across controllers and services.
   - No advanced metrics collection yet (should use Micrometer + Prometheus).

2. **API Security**
   - No mention of CORS configuration
   - No mention of CSRF protection
   - No mention of SQL injection prevention (should use parameterized queries)
   - No mention of input sanitization

3. **Database**
   - No mention of database backup strategy
   - No mention of connection pooling limits
   - No mention of query optimization / indexing strategy

4. **External Services**
   - Twilio integration assumes account already set up
   - Groq AI integration assumes API key configured
   - EmailService assumes SMTP credentials configured
   - No circuit breaker for Groq AI failures

5. **Deployment**
   - No mention of containerization (Docker)
   - No mention of Kubernetes deployment
   - No mention of environment-specific configuration

---
