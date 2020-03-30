package com.example.connector.repository;

import com.example.connector.domain.User;
import com.google.cloud.firestore.QuerySnapshot;

import java.util.List;
import java.util.Map;
import reactor.core.publisher.Mono;

public interface UserRepository {

  Mono<List<User>> findAll(Map<String, String> queryParams);

  void save(User user);

  void update(User user);

  void delete(String id);
}
