# AlgoLens — Competitive Programming Analytics Platform

## System Overview

AlgoLens is a comprehensive analytics and learning platform designed for competitive programmers on Codeforces. The application integrates with the Codeforces API to fetch real-time user data, contest results, and submission statistics. It provides advanced features like user comparisons, friend management, personalized insights, and AI-powered code analysis using Groq AI.

### Main Modules/Features

1. **Authentication & User Management**
   - JWT-based authentication with email verification
   - Multi-device session management
   - Password reset functionality

2. **Codeforces Data Integration**
   - Real-time user profile fetching
   - Contest history and rating graphs
   - Submission statistics and verdicts analysis

3. **User Comparison System**
   - Side-by-side rating comparisons
   - Submission comparison for specific problems
   - Performance analysis between programmers

4. **Social Features (Friends)**
   - Add/remove friends for tracking
   - Leaderboards among friends
   - Contest overlap analysis
   - Streak comparisons

5. **Personalized Insights**
   - Weak topic identification based on submission patterns
   - Problem recommendations for improvement
   - Upsolving opportunities from recent contests

6. **AI-Powered Analysis**
   - Groq AI integration for code analysis
   - Personalized learning recommendations
   - Problem-solving pattern analysis

### Technology Stack

- **Backend**: Spring Boot 3.5.11, Java 21
- **Security**: JWT, Spring Security
- **Database**: MySQL with JPA/Hibernate
- **Caching**: Redis for performance
- **External APIs**: Codeforces API, Groq AI
- **Communication**: WebClient for reactive HTTP calls

---

## Core Business Flows

### Flow: User Registration and Authentication

**Purpose**: Allow new users to create accounts, verify emails, and authenticate securely.

#### Step-by-step execution:

1. **Frontend calls POST /api/auth/register**
   - User submits name, email, password
   - Request validated for format and uniqueness

2. **Service Layer (AuthService.register)**
   - Checks if email already exists in User table
   - Validates email rate limiting (prevents spam)
   - Creates/updates PendingRegistration record with hashed token
   - Sends verification email via EmailService

3. **Validations**:
   - Email format and uniqueness
   - Password strength requirements
   - Rate limiting (email sending limits)

4. **Database Operations**:
   - INSERT/UPDATE into pending_registrations table
   - No User record created yet

5. **Response**: "Registration successful. Please check your email to verify your account"

6. **Frontend Next Steps**:
   - Show success message
   - Redirect to email verification page
   - Store email locally for verification flow

#### Flow Diagram (textual)
```
User → POST /register → Email Validation → Rate Limit Check → Save PendingRegistration → Send Email → Return Success
```

---

### Flow: Email Verification and Account Activation

**Purpose**: Complete user registration by verifying email ownership.

#### Step-by-step execution:

1. **Frontend calls GET /api/auth/verify-email?token=xyz**
   - User clicks verification link from email

2. **Service Layer (AuthService.verifyEmail)**
   - Hashes the token and looks up PendingRegistration
   - Checks token expiry (24 hours)
   - Verifies email not already registered
   - Creates User record from pending data
   - Deletes pending registration

3. **Validations**:
   - Token exists and not expired
   - Email not already verified

4. **Database Operations**:
   - SELECT from pending_registrations
   - INSERT into users table
   - DELETE from pending_registrations

5. **Response**: "Email verified successfully. You can now log in."

6. **Frontend Next Steps**:
   - Show success message
   - Redirect to login page
   - Clear any stored verification state

---

### Flow: User Login and Token Generation

**Purpose**: Authenticate verified users and provide access tokens.

#### Step-by-step execution:

1. **Frontend calls POST /api/auth/login**
   - User submits email, password, device info

2. **Service Layer (AuthService.login)**
   - Checks IP-based rate limiting (login attempts)
   - Authenticates via Spring Security
   - Verifies email is verified
   - Generates JWT access token
   - Creates refresh token for device

3. **Validations**:
   - Email/password correctness
   - Account verification status
   - Login attempt rate limiting

4. **Database Operations**:
   - SELECT from users
   - INSERT into refresh_tokens

5. **Response**: AuthResponse with accessToken and refreshToken

6. **Frontend Next Steps**:
   - Store accessToken in memory/session
   - Store refreshToken securely (HttpOnly cookie recommended)
   - Set up automatic token refresh logic
   - Redirect to main application

---

### Flow: User Profile Data Retrieval

**Purpose**: Fetch and display Codeforces user statistics and history.

#### Step-by-step execution:

1. **Frontend calls GET /api/users/{handle}/profile**
   - Authenticated request with JWT token

2. **Service Layer (UserServices.getUserProfile)**
   - Calls CodeforcesApiClient.getUserInfo()
   - Maps API response to UserProfileDTO
   - Applies caching (Redis, 5 minutes)

3. **Validations**:
   - JWT token validity
   - Codeforces handle exists

4. **Database Operations**:
   - None (external API call with caching)

5. **Response**: UserProfileDTO with rating, rank, contest stats

6. **Frontend Next Steps**:
   - Display user profile information
   - Cache locally for performance
   - Enable navigation to other user endpoints

---

### Flow: User Comparison Analysis

**Purpose**: Compare performance metrics between two Codeforces users.

#### Step-by-step execution:

1. **Frontend calls GET /api/compare/rating?handle1=A&handle2=B**
   - Authenticated request with two handles

2. **Service Layer (ComparisonServices.compareRatings)**
   - Fetches both users' profiles via CodeforcesApiClient
   - Calculates rating differences and rankings
   - Determines higher-rated user

3. **Validations**:
   - Both handles exist on Codeforces
   - Users are not the same
   - JWT token validity

4. **Database Operations**:
   - None (external API calls)

5. **Response**: RatingComparisonDTO with deltas and rankings

6. **Frontend Next Steps**:
   - Display comparison table/cards
   - Show rating graphs side-by-side
   - Offer to add users as friends

---

### Flow: Friend Management

**Purpose**: Allow users to track and compare with other programmers.

#### Step-by-step execution:

1. **Frontend calls POST /api/friends/add**
   - User submits their handle and friend's handle

2. **Service Layer (FriendServices.addFriend)**
   - Validates both handles exist on Codeforces
   - Checks friendship doesn't already exist
   - Creates UserFriend entity

3. **Validations**:
   - Both users exist on Codeforces
   - Friendship relationship is unique
   - User not adding themselves

4. **Database Operations**:
   - INSERT into user_friends table

5. **Response**: "Friend added successfully"

6. **Frontend Next Steps**:
   - Refresh friends list
   - Update leaderboard display
   - Show friend-specific features

---

### Flow: Personalized Insights Generation

**Purpose**: Identify weak areas and provide learning recommendations.

#### Step-by-step execution:

1. **Frontend calls GET /api/insights/{handle}/weak-topics**
   - Authenticated request for user's weak areas

2. **Service Layer (InsightServices.getWeakTopics)**
   - Fetches user's submissions via CodeforcesApiClient
   - Analyzes verdict patterns by problem tags
   - Calculates acceptance rates per topic
   - Identifies topics with low success rates

3. **Validations**:
   - User has sufficient submission history
   - JWT token validity

4. **Database Operations**:
   - None (external API with caching)

5. **Response**: List of WeakTopicDTO with acceptance rates

6. **Frontend Next Steps**:
   - Display weak topics prominently
   - Show progress charts
   - Link to recommendations endpoint

---

### Flow: AI-Powered Code Analysis

**Purpose**: Provide intelligent analysis of user's problem-solving patterns.

#### Step-by-step execution:

1. **Frontend calls GET /api/analysis/upsolve/{handle}**
   - Request for AI analysis of unsolved problems

2. **Service Layer (AnalysisService.analyzeUpsolve)**
   - Fetches user's recent contest performance
   - Identifies unsolved problems
   - Constructs AI prompt with problem details
   - Calls GroqClient.generate() for analysis
   - Parses AI response into structured recommendations

3. **Validations**:
   - User has recent contest participation
   - AI service is available

4. **Database Operations**:
   - None (external API calls)

5. **Response**: AiAnalysisResponseDTO with problem analyses and recommendations

6. **Frontend Next Steps**:
   - Display AI insights prominently
   - Show actionable tips
   - Link to specific problems for practice

---

## API Reference (Connected to Flows)

### Authentication Endpoints

#### POST /api/auth/register
**Flow**: User Registration  
**Description**: Initiates user registration process  
**Request**:
```json
{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "password": "SecurePassword@123"
}
```
**Response**:
```json
"Registration successful. Please check your email to verify your account"
```
**Error Cases**: 400 (validation), 409 (email exists), 429 (rate limited)

#### GET /api/auth/verify-email
**Flow**: Email Verification  
**Description**: Completes registration by verifying email  
**Query Params**: token (string)  
**Response**:
```json
"Email verified successfully. You can now log in."
```
**Error Cases**: 400 (invalid/expired token)

#### POST /api/auth/login
**Flow**: User Login  
**Description**: Authenticates user and returns tokens  
**Request**:
```json
{
  "email": "john.doe@example.com",
  "password": "SecurePassword@123"
}
```
**Response**:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
}
```
**Error Cases**: 401 (invalid credentials), 403 (email not verified)

#### POST /api/auth/refresh
**Flow**: Token Refresh  
**Description**: Generates new tokens using refresh token  
**Request**:
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
}
```
**Response**: Same as login  
**Error Cases**: 401 (invalid refresh token)

### User Data Endpoints

#### GET /api/users/{handle}/profile
**Flow**: User Profile Retrieval  
**Description**: Fetches Codeforces user profile  
**Path Params**: handle (string)  
**Response**:
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
**Error Cases**: 404 (user not found)

#### GET /api/users/{handle}/contest-history
**Flow**: User Profile Retrieval  
**Description**: Gets contest participation history  
**Path Params**: handle (string)  
**Response**:
```json
[
  {
    "contestId": 1865,
    "contestName": "Codeforces Round 892 (Div. 1)",
    "rank": 12,
    "oldRating": 3920,
    "newRating": 3950,
    "ratingChange": 30
  }
]
```

#### GET /api/compare/rating
**Flow**: User Comparison  
**Description**: Compares ratings between two users  
**Query Params**: handle1, handle2 (strings)  
**Response**:
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

### Friend Management Endpoints

#### POST /api/friends/add
**Flow**: Friend Management  
**Description**: Adds a friend relationship  
**Request**:
```json
{
  "userHandle": "myhandle",
  "friendHandle": "tourist"
}
```
**Response**:
```json
"Friend added successfully"
```
**Error Cases**: 409 (already friends)

#### GET /api/friends/{handle}
**Flow**: Friend Management  
**Description**: Lists all friends with current ratings  
**Path Params**: handle (string)  
**Response**:
```json
[
  {
    "handle": "tourist",
    "rating": 3950,
    "maxRating": 3957,
    "rank": "International Master",
    "avatar": "https://codeforces.com/avatar/tourist.jpg",
    "contestsParticipated": 450
  }
]
```

### Insights Endpoints

#### GET /api/insights/{handle}/weak-topics
**Flow**: Personalized Insights  
**Description**: Identifies topics with low acceptance rates  
**Path Params**: handle (string)  
**Response**:
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

#### GET /api/insights/{handle}/recommendations
**Flow**: Personalized Insights  
**Description**: Suggests problems for weak topics  
**Path Params**: handle (string)  
**Response**:
```json
[
  {
    "contestId": 1853,
    "index": "C",
    "name": "Prefix Sum Problem",
    "rating": 2100,
    "tags": ["binary search", "prefix sums"]
  }
]
```

### Analysis Endpoints

#### GET /api/analysis/upsolve/{handle}
**Flow**: AI Analysis  
**Description**: AI-powered analysis of unsolved problems  
**Path Params**: handle (string)  
**Response**:
```json
{
  "problemAnalyses": [
    {
      "contestId": 1865,
      "problemIndex": "D",
      "problemName": "Yet Another Problem",
      "likelyIssue": "Implementation complexity leading to off-by-one errors",
      "conceptToStudy": "Careful boundary handling in iterative approaches",
      "actionableTip": "Use 0-based indexing consistently and add assertions"
    }
  ],
  "overallRecommendation": "Focus on mastering Binary Search and Divide & Conquer techniques"
}
```

---

## Frontend Integration Guide

### Exact Order of API Calls

#### 1. Initial App Load (Unauthenticated)
```
1. No API calls needed
2. Show registration/login forms
```

#### 2. User Registration Flow
```
1. POST /auth/register → Show success message
2. User checks email → Clicks verification link
3. GET /auth/verify-email → Show success, redirect to login
4. POST /auth/login → Store tokens, redirect to dashboard
```

#### 3. Main Application Flow (Authenticated)
```
1. GET /api/users/{userHandle}/profile → Display user info
2. GET /api/friends/{userHandle} → Load friends list
3. GET /api/contests/upcoming → Show upcoming contests
4. GET /api/insights/{userHandle}/weak-topics → Show insights
```

#### 4. User Comparison Flow
```
1. GET /api/compare/rating?handle1=A&handle2=B → Show comparison
2. POST /api/friends/add → Add as friend (optional)
3. GET /api/friends/{userHandle}/leaderboard → Update rankings
```

#### 5. Problem Analysis Flow
```
1. GET /api/analysis/upsolve/{handle} → Get AI insights
2. GET /api/insights/{handle}/recommendations → Show practice problems
3. GET /api/insights/{handle}/upsolve → Show upsolving opportunities
```

### Authentication/Token Handling

#### Token Storage Strategy
```javascript
// Access token (short-lived, 1 hour)
sessionStorage.setItem('accessToken', response.accessToken);

// Refresh token (long-lived, 7 days) - Use HttpOnly cookie for security
document.cookie = `refreshToken=${response.refreshToken}; HttpOnly; Secure; SameSite=Strict`;
```

#### Automatic Token Refresh
```javascript
// Intercept all API calls
axios.interceptors.response.use(
  response => response,
  async error => {
    if (error.response?.status === 401) {
      try {
        const refreshResponse = await axios.post('/api/auth/refresh', {
          refreshToken: getRefreshTokenFromCookie()
        });
        
        // Update stored tokens
        sessionStorage.setItem('accessToken', refreshResponse.data.accessToken);
        document.cookie = `refreshToken=${refreshResponse.data.refreshToken}; ...`;
        
        // Retry original request
        return axios(error.config);
      } catch (refreshError) {
        // Redirect to login
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);
```

#### Request Headers Setup
```javascript
const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json'
  }
});

// Add auth header to all requests
apiClient.interceptors.request.use(config => {
  const token = sessionStorage.getItem('accessToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
```

### Common Mistakes to Avoid

#### 1. Token Storage Security
❌ **Don't do this:**
```javascript
localStorage.setItem('refreshToken', token); // Vulnerable to XSS
```

✅ **Do this:**
```javascript
// Use HttpOnly cookies for refresh tokens
document.cookie = `refreshToken=${token}; HttpOnly; Secure; SameSite=Strict`;
```

#### 2. Error Handling
❌ **Don't ignore errors:**
```javascript
try {
  await api.getUserProfile(handle);
} catch (error) {
  // Silent failure - user confused
}
```

✅ **Handle errors properly:**
```javascript
try {
  const profile = await api.getUserProfile(handle);
  setUserProfile(profile);
} catch (error) {
  if (error.response?.status === 404) {
    showError('User not found on Codeforces');
  } else if (error.response?.status === 401) {
    // Token refresh will handle this automatically
  } else {
    showError('Failed to load user profile');
  }
}
```

#### 3. Race Conditions
❌ **Multiple simultaneous requests:**
```javascript
// User clicks button multiple times quickly
const handleCompare = () => api.compareUsers(handle1, handle2);
```

✅ **Prevent with loading states:**
```javascript
const [loading, setLoading] = useState(false);

const handleCompare = async () => {
  if (loading) return;
  setLoading(true);
  try {
    const result = await api.compareUsers(handle1, handle2);
    setComparison(result);
  } finally {
    setLoading(false);
  }
};
```

#### 4. Data Synchronization
❌ **Stale data after actions:**
```javascript
// Add friend but don't refresh friends list
await api.addFriend(friendHandle);
// Friends list still shows old data
```

✅ **Refresh dependent data:**
```javascript
const addFriend = async (friendHandle) => {
  await api.addFriend({ userHandle: currentUser, friendHandle });
  // Refresh friends list
  const updatedFriends = await api.getFriends(currentUser);
  setFriends(updatedFriends);
};
```

### State Management Best Practices

#### User Authentication State
```javascript
const [auth, setAuth] = useState({
  isAuthenticated: false,
  user: null,
  tokens: null
});

// After successful login
setAuth({
  isAuthenticated: true,
  user: userData,
  tokens: { access: accessToken, refresh: refreshToken }
});
```

#### API Response Caching
```javascript
const [cache, setCache] = useState(new Map());

const getCachedOrFetch = async (key, fetchFn) => {
  if (cache.has(key)) {
    return cache.get(key);
  }
  const data = await fetchFn();
  setCache(prev => new Map(prev).set(key, data));
  return data;
};

// Usage
const profile = await getCachedOrFetch(
  `profile-${handle}`, 
  () => api.getUserProfile(handle)
);
```

### Performance Optimization Tips

1. **Debounce Search Inputs**
   - Don't call APIs on every keystroke
   - Use 300-500ms debounce for user searches

2. **Implement Pagination**
   - Don't load all contests at once
   - Use page/size parameters for large lists

3. **Lazy Load Heavy Components**
   - Load rating graphs only when visible
   - Use React.lazy for analysis components

4. **Optimistic Updates**
   - Update UI immediately, then sync with server
   - Rollback on errors

---

## Assumptions

Based on code analysis, the following assumptions were made about the system behavior:

### Database Schema Assumptions
- **users table**: Stores registered users with email, password, verification status
- **pending_registrations table**: Temporary storage for unverified accounts
- **refresh_tokens table**: Device-specific refresh tokens for session management
- **user_friends table**: Many-to-many relationship between users (inferred from FriendServices)

### External API Behavior
- **Codeforces API**: Returns consistent JSON structure for user info, contests, submissions
- **Groq AI API**: Accepts chat completion format and returns structured analysis
- **Caching**: Redis stores API responses with TTL (5 minutes for user data, 1 hour for analysis)

### Business Logic Assumptions
- **Email Verification**: Required before login (enforced in AuthService)
- **Rate Limiting**: Applied to prevent abuse (login attempts, email sending)
- **Friend Relationships**: Bidirectional but managed by one user (add/remove operations)
- **Weak Topic Analysis**: Based on submission verdicts grouped by problem tags
- **AI Analysis**: Processes recent unsolved problems to provide learning insights

### Security Assumptions
- **JWT Tokens**: Access tokens expire in 1 hour, refresh tokens in 7 days
- **Password Storage**: BCrypt hashed passwords in database
- **Session Management**: Multi-device support with device-specific refresh tokens

### Performance Assumptions
- **Caching Strategy**: External API calls cached to reduce load
- **Database Queries**: Efficient with proper indexing on email, tokens
- **Concurrent Access**: Services handle multiple users simultaneously

All assumptions are based on the actual implementation patterns observed in the codebase.</content>
<parameter name="filePath">/Users/anchalsingh/Desktop/Projects/algo_lens/README.md
