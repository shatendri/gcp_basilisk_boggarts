package com.example.connector.repository;

import com.example.connector.domain.User;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface UserRepository {

    List<User> findAll(Map<String, String> queryParams) throws ExecutionException, InterruptedException;

    void save(User user);

    void update(User user) throws ExecutionException, InterruptedException;

    void delete(String id) throws ExecutionException, InterruptedException;
}
