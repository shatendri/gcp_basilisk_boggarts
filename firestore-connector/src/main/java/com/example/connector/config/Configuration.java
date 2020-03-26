package com.example.connector.config;

import com.example.connector.domain.User;
import com.example.connector.service.UserService;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Component
public class Configuration {


    @Bean
    public RouterFunction<ServerResponse> router(UserService userService) {
        return route(
                GET("/users"), request -> ServerResponse
                        .ok()
                        .body(userService.getUsers(request.queryParams()), User.class));
    }


    @Bean
    public Firestore firestore() {
        return FirestoreOptions.getDefaultInstance().newBuilder()
                .build()
                .getService();
    }
}
