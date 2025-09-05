package no.hvl.dat250.experiment1.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class RootController {

    @GetMapping("/")
    public Map<String, Object> index() {
        Map<String, Object> endpoints = new LinkedHashMap<>();
        // Users
        Map<String, String> users = new LinkedHashMap<>();
        users.put("Create user (POST)", "/api/users");
        users.put("Get all users (GET)", "/api/users");
        users.put("Get user by username (GET)", "/api/users/{username}");
        endpoints.put("Users", users);
        // Polls
        Map<String, String> polls = new LinkedHashMap<>();
        polls.put("Create poll (POST, requires ?username=creator)", "/api/polls?username={username}");
        polls.put("Get all polls (GET)", "/api/polls");
        polls.put("Get poll by id (GET)", "/api/polls/{pollId}");
        polls.put("Add option (POST)", "/api/polls/{pollId}/options");
        endpoints.put("Polls", polls);
        // Votes
        Map<String, String> votes = new LinkedHashMap<>();
        votes.put("Cast/change vote (POST, requires ?username&voteOptionId)", "/api/polls/{pollId}/votes?username={username}&voteOptionId={id}");
        votes.put("Get votes for poll (GET)", "/api/polls/{pollId}/votes");
        votes.put("Get vote count for poll (GET)", "/api/polls/{pollId}/votes/count");
        endpoints.put("Votes", votes);

        return endpoints;
    }
}
