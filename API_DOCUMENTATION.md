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
9. [Notes for Frontend Developers](#notes-for-frontend-developers)

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

### Get User Contest History

**Method:** GET  
**URL:** `/users/{handle}/contest-history`

**Description:**  
Retrieve all contests the user participated in with rating changes.

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
]
```

**Error Response (404 Not Found):**
```json
{
  "timestamp": "2026-04-13T11:05:00",
  "status": 404,
  "error": "Not Found",
  "message": "No contest history found for user 'unknown_user'",
  "path": "/api/users/unknown_user/contest-history"
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

### Get Unsolved Problems by Friends

**Method:** GET  
**URL:** `/friends/{handle}/unsolved-by-me`

**Description:**  
Get problems that friends have solved but you haven't. Great for practice suggestions.

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
]
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
   ↓
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
- **Authentication:** 5 requests/minute per email
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
http://localhost:8080/swagger-ui.html
```

Try endpoints directly from the browser:
1. Click "Authorize"
2. Paste your access token
3. Try endpoints with pre-filled parameters

---

## Support

For issues or questions:
1. Check error message in response
2. Review request format in this documentation
3. Verify authentication token is valid
4. Check rate limiting (wait and retry)
5. Ensure Codeforces handle exists


