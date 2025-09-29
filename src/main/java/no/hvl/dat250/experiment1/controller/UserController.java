package no.hvl.dat250.experiment1.controller;

import no.hvl.dat250.experiment1.domain.User;
import no.hvl.dat250.experiment1.manager.PollManager;

import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.util.List;


@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = { "http://localhost:5173", "http://127.0.0.1:5173" })
@RequiredArgsConstructor
public class UserController {
    private final PollManager pollManager;
    
    @PostMapping
    public User create(@RequestBody CreateUser req){
        return pollManager.createUser(req.username(), req.email());
    }
    
    @GetMapping
    public List<User> all(){
        return pollManager.listUsers();
    }

    @GetMapping("/{id}")
    public User one(@PathVariable Long id){
        return pollManager.getUser(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        pollManager.deleteUser(id);
    }

    // Request DTO
    public record CreateUser(String username, String email) {}
}
