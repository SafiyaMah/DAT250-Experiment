# DAT250: Software Technology Experiment Assignment 2

## What I did
In this assignment I implemented a simple REST API for a Poll application using Spring Boot.  
The domain model consists of:
- **User** (username, email)
- **Poll** (question, publishedAt, validUntil, creator)
- **VoteOption** (caption, presentationOrder)
- **Vote** (publishedAt, linked to a user and an option)

I created controller classes:
- `UserController` -> create and fetch users
- `PollController` -> create polls and list them
- `VoteOptionController` -> add options to polls
- `VoteController` -> cast votes, list votes, and get vote counts
- `RootController` -> overview of available endpoints

The `PollManager` class keeps everything in memory with hash maps.

## Testing
I used **Bruno** as my HTTP client to test the REST endpoints:
- POST `/api/users` to create users (e.g., Farax, Xalimo).
- GET `/api/users` to list users.
- POST `/api/polls?username={creator}` to create polls.
- POST `/api/polls/{pollId}/options` to add vote options (e.g., "Yes", "No").
- POST `/api/polls/{pollId}/votes?username={voter}&voteOptionId={id}` to cast votes.
- GET `/api/polls/{pollId}/votes` to see all votes for a poll.
- GET `/api/polls/{pollId}/votes/count` to get the total number of votes.

## Technical problems
At first I ran into issues when testing with Bruno, because I didn’t know exactly how the JSON body should look or what data to send. This gave me 415 Unsupported Media Type and 400 Bad Request errors. Once I learned to always set the header Content-Type: application/json and to provide the correct request format, the problems were resolved.

## Conclusion
I managed to implement a working REST API for polls, users, vote options, and votes, with endpoints tested in Bruno. Despite some initial errors, I got all main functionality working: creating users, polls, options, casting votes, and retrieving counts.

# DAT250: Software Technology Experiment Assignment 3/4
For assignment 3 I built a Spring Boot REST API for a poll app with four JPA entities (User, Poll, VoteOption, Vote) and controllers for users, polls, options, and votes. I wired everything through a PollManager service plus Spring Data repositories, enabled CORS for the Svelte client, and added endpoints to create users and polls, add options, list polls, fetch a poll by id, cast a vote, and return per-option results. I also enforced “one vote per user per poll” and supported public/private polls; the list endpoint returns all polls, and the UI filters so everyone sees public polls while a logged-in creator also sees their private ones. On the frontend I fixed session persistence and I corrected JSON casing so the boolean aPublic is serialized consistently.

For assignment 4 I created a PollsTest and ran the test with Gradle and confirmed it’s green. Along the way I fixed a few issues. After those fixes both the API and the test behave as expected.


# DAT250: Software Technology Experiment Assignment 5
For assignment 5 I integrated Redis (via Docker) as a cache layer for my Spring Boot poll app. I added a small Jedis-based helper and used it to cache the “votes per option” for each poll under a key like poll:{id}:counts with a short TTL. When results are requested, the app first checks Redis and serves from cache if present, otherwise it aggregates from MySQL, returns the data, and writes it into Redis. On every new vote I increment the cached counter, and when options change I invalidate the cache entry. This demonstrates using a NoSQL store as a fast, denormalized cache alongside the relational database, improving read performance while keeping data consistent.