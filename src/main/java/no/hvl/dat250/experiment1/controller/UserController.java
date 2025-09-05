package no.hvl.dat250.experiment1.controller;

import no.hvl.dat250.experiment1.domain.User;
import no.hvl.dat250.experiment1.manager.PollManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {
        "http://localhost:8080"
})
public class UserController {

    @Autowired
    private PollManager pollManager;

    @PostMapping
    public User createUser(@RequestBody User user) {
        return pollManager.createUser(user.getUsername(), user.getEmail());
    }

    @GetMapping
    public Map<String, User> getAllUsers() {
        return pollManager.getUsers();
    }

    @GetMapping("/{username}") // get one user
    public User getUser(@PathVariable String username) {
        return pollManager.getUser(username);
    }
}
