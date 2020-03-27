package com.example.connector.controller;

import com.example.connector.domain.User;
import com.example.connector.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public Mono<List<User>> getUsers(@RequestParam Map<String, String> queryParams) {
        return userService.getUsers(queryParams);
    }

    @PostMapping(value = "/users/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void saveUser(@RequestBody User user) {
        userService.save(user);
    }

    @PostMapping(value = "/users/remove", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void removeUser(@RequestBody User user) {
        userService.delete(user);
    }

}
