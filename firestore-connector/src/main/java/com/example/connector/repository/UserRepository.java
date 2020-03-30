package com.example.connector.repository;

import com.example.connector.domain.User;
import com.google.cloud.firestore.QuerySnapshot;
import java.util.Map;
import reactor.core.publisher.Mono;

public interface UserRepository {

  Mono<QuerySnapshot> findAll(Map<String, String> queryParams);

  QuerySnapshot save(User user);

  void delete(User user);
}
