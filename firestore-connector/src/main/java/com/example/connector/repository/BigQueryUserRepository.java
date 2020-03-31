package com.example.connector.repository;

import com.example.connector.domain.User;
import reactor.core.publisher.Mono;

import java.util.List;

public class BigQueryUserRepository {
    public Mono<List<User>> findAll() {
        return null;
    }
}
