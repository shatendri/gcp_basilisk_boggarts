package com.example.connector.service;

import com.example.connector.domain.User;
import com.example.connector.repository.UserRepositoryImpl;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class UserService { // TODO add interface

    private final UserRepositoryImpl userRepository;

    public UserService(UserRepositoryImpl userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<List<User>> getUsers(Map<String, String> queryParams) {
        return userRepository
                .findAll(queryParams)
                .map(qs -> qs.toObjects(User.class));
    }

    public void save(User user) {
        userRepository.save(user);

        // TODO RESTful APIs should always return created entity
        // See: https://hackernoon.com/restful-api-designing-guidelines-the-best-practices-60e1d954e7c9
    }

    public void delete(User user) {
        userRepository.delete(user);
    }

}
