package com.example.connector.controller;

import com.example.connector.axon.coreapi.command.AddUserCommand;
import com.example.connector.axon.coreapi.command.DeleteUserCommand;
import com.example.connector.axon.coreapi.command.UpdateUserCommand;
import com.example.connector.axon.coreapi.query.FindAllUsersFromBigQuery;
import com.example.connector.axon.coreapi.query.FindUsersQuery;
import com.example.connector.domain.User;
import org.apache.commons.beanutils.BeanUtils;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
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

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<List<User>> getUsers(@RequestParam Map<String, String> queryParams) {
        FindUsersQuery findUsersQuery = new FindUsersQuery(queryParams);
        return queryGateway.query(findUsersQuery, ResponseTypes.multipleInstancesOf(User.class));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/bigquery")
    public CompletableFuture<List<User>> getUsersFromBigQuery() {
        return queryGateway.query(new FindAllUsersFromBigQuery(), ResponseTypes.multipleInstancesOf(User.class));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public void saveUser(@RequestBody User user) throws ExecutionException, InterruptedException, InvocationTargetException, IllegalAccessException {
        AddUserCommand addUserCommand = new AddUserCommand();
        BeanUtils.copyProperties(addUserCommand, user);
        commandGateway.send(addUserCommand);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateUser(@PathVariable String id, @RequestBody User user) throws InvocationTargetException, IllegalAccessException {
        UpdateUserCommand updateUserCommand = new UpdateUserCommand();
        BeanUtils.copyProperties(updateUserCommand, user);
        commandGateway.send(updateUserCommand);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id) {
        DeleteUserCommand deleteUserCommand = new DeleteUserCommand(id);
        commandGateway.send(deleteUserCommand);
    }
}
