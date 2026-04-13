# AlgoLens — AI-Powered Competitive Programming Analytics Platform

AlgoLens is a full-stack analytics and learning platform designed for competitive programmers on Codeforces. It provides deep insights into programming progress, enables solution comparison with other programmers, and leverages AI-powered analysis to accelerate learning through code review and weak topic identification.

---

## Key Features

### 1. User Profile

Fetch and display a user's basic information.

Displayed data:

* Handle
* Rating
* Max Rating
* Rank
* Avatar

---

### 2. Rating & Contest History

Track performance across contests.

Information displayed:

* Contest name
* Rank achieved
* Old rating
* New rating
* Rating change

This allows users to analyze how their rating evolves over time.

---

### 3. Rating Progression Graph

Visual representation of rating progression.

Helps users understand:

* long-term improvement
* rating fluctuations
* contest impact on performance

---

### 4. Upcoming Contests

Displays upcoming Codeforces contests.

Information shown:

* Contest name
* Start time
* Duration

This allows users to prepare for upcoming competitions.

---

### 5. Submission Statistics

Analyze a user's submission behavior.

Statistics include:

* Total submissions
* Accepted submissions
* Acceptance rate
* Languages used

This provides insights into coding habits.

---

### 6. Problem Difficulty Distribution

Shows problems solved grouped by difficulty level.

Example categories:

* 800–1000
* 1000–1200
* 1200–1400
* 1400–1600
* 1600+

This helps identify:

* strengths
* weak difficulty ranges.

---

### 7. Rating Comparison

Compare two Codeforces users.

Comparison includes:

* Current rating
* Maximum rating
* Rank

This feature helps evaluate performance between programmers.

---

### 8. Code Comparison

Allows users to compare solutions for the same problem.

Workflow:

1. Provide two handles and a problem ID
2. Fetch accepted submissions
3. Retrieve source code
4. Display code side-by-side

This makes it easy to analyze how different programmers approach the same problem.

---

### 9. AI Code Explanation

AI analyzes two solutions and explains their differences.

AI analysis includes:

* Algorithm used
* Time complexity
* Key optimizations
* Which solution is more efficient

This helps users **learn new techniques by studying other solutions**.

---

### 10. Code Similarity Detection

Measures how similar two solutions are.

Possible approaches:

* Token comparison
* String similarity
* Structural analysis

Returns a similarity percentage.

---

### 11. Submission Caching

To improve performance, previously fetched submissions are cached in the database.

Stored data may include:

* submission code
* comparison history
* AI analysis results

This avoids repeated scraping and speeds up future comparisons.

---

## Tech Stack

Backend

* Java
* Spring Boot
* MySQL

External Data Source

* Codeforces API

Core Technologies

* REST APIs
* Web scraping for submission source code
* AI-based code analysis

---

## Architecture Overview

User
↓
Frontend Interface
↓
Spring Boot Backend
↓
Codeforces API
↓
MySQL Database
↓
AI Analysis Service

---

## Core API Endpoints

User Profile

GET /api/user/{handle}

---

Contest History

GET /api/user/{handle}/contests

---

Rating Graph

GET /api/user/{handle}/rating-graph

---

Upcoming Contests

GET /api/contests/upcoming

---

Submission Statistics

GET /api/user/{handle}/stats

---

Difficulty Distribution

GET /api/user/{handle}/difficulty-distribution

---

Rating Comparison

GET /api/compare/rating?handle1=A&handle2=B

---

Find Accepted Submissions

POST /api/compare/find

Input:

* handle1
* handle2
* problemId

---

Retrieve Submission Code

POST /api/compare/code

Returns:

* code1
* code2
* language

---

AI Solution Analysis

POST /api/analyze

Input:

* codeA
* codeB

---

Code Similarity

POST /api/similarity

Returns:

* similarity percentage

---

## Database Tables (Example)

users
Stores user metadata.

submissions
Stores cached submission code.

comparisons
Stores comparison history and analysis results.


## Goals of the Project

* Help competitive programmers analyze their progress.
* Provide insights into contest performance.
* Allow direct comparison of algorithmic solutions.
* Use AI to explain solution differences.
* Encourage learning through code analysis.

---

## Future Improvements

Possible enhancements:

* Practice recommendations
* Weak topic detection
* Contest performance prediction
* Personalized training suggestions
* Code optimization suggestions

---

## Author

Developed as a competitive programming analytics and learning platform.
