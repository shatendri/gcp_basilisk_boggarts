package com.example.connector.service;

import com.example.connector.domain.User;
import com.example.connector.repository.FirestoreUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private final FirestoreUserRepository userRepository;

    public UserService(FirestoreUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<List<User>> getUsers(MultiValueMap<String, String> queryParameters) {

        Map<String, String> queryParams = queryParameters.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get(0)));

        return userRepository.findUsers(queryParams).map(qs -> qs.toObjects(User.class));
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public void delete(User user) {
        userRepository.delete(user);
    }

}
