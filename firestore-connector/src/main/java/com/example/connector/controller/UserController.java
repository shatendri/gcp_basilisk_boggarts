package com.example.connector.controller;

import com.example.connector.domain.User;
import com.example.connector.service.UserService;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  public Mono<List<User>> getUsers(@RequestParam Map<String, String> queryParams) {
    return userService.getUsers(queryParams);
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public User saveUser(@RequestBody User user) {
    userService.save(user);

    // TODO RESTful APIs should always return created entity
    // See: https://hackernoon.com/restful-api-designing-guidelines-the-best-practices-60e1d954e7c9
    return null;
  }

  @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public User editUser(@RequestBody User user) {
    // TODO
    return null;
  }

  @DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public void removeUser(@RequestBody User user) {
    userService.delete(user);
  }
}
