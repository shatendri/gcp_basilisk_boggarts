package com.example.connector.controller;

import com.example.connector.axon.command.AddUserCommand;
import com.example.connector.axon.command.DeleteUserCommand;
import com.example.connector.axon.command.UpdateUserCommand;
import com.example.connector.axon.coreapi.query.FindAllUsersFromBigQuery;
import com.example.connector.axon.query.FindUsersQuery;
import com.example.connector.domain.User;
import org.apache.commons.beanutils.BeanUtils;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/users")
public class UserController {
    private CommandGateway commandGateway;
    private QueryGateway queryGateway;

    public UserController(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<User> getUsers(@RequestParam Map<String, String> queryParams) throws ExecutionException, InterruptedException {
        FindUsersQuery findUsersQuery = new FindUsersQuery(queryParams);
        CompletableFuture<List<User>> query = queryGateway.query(
                findUsersQuery, ResponseTypes.multipleInstancesOf(User.class));
        return Flux.fromStream(query.get().stream());
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/bigquery")
    public Flux<User> getUsersFromBigQuery() throws ExecutionException, InterruptedException {
        CompletableFuture<List<User>> users = queryGateway.query(
                new FindAllUsersFromBigQuery(), ResponseTypes.multipleInstancesOf(User.class));
        return Flux.fromStream(users.get().stream());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<User> saveUser(@RequestBody User user) throws InvocationTargetException, IllegalAccessException {
        user.setId(UUID.randomUUID().toString());
        AddUserCommand addUserCommand = new AddUserCommand();
        BeanUtils.copyProperties(addUserCommand, user);
        commandGateway.send(addUserCommand);
        return Mono.just(user);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<User> updateUser(@PathVariable String id, @RequestBody User user) throws InvocationTargetException, IllegalAccessException {
        user.setId(id);
        UpdateUserCommand updateUserCommand = new UpdateUserCommand();
        BeanUtils.copyProperties(updateUserCommand, user);
        commandGateway.send(updateUserCommand);
        return Mono.just(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id) {
        DeleteUserCommand deleteUserCommand = new DeleteUserCommand(id);
        commandGateway.send(deleteUserCommand);
    }
}
