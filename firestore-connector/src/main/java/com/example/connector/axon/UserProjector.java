package com.example.connector.axon;

import com.example.connector.axon.event.UserAddedEvent;
import com.example.connector.axon.event.UserDeletedEvent;
import com.example.connector.axon.event.UserUpdatedEvent;
import com.example.connector.axon.query.FindAllUsersFromBigQuery;
import com.example.connector.axon.query.FindUserQuery;
import com.example.connector.axon.query.FindUsersQuery;
import com.example.connector.domain.User;
import com.example.connector.repository.BigQueryUserRepository;
import com.example.connector.repository.UserRepositoryImpl;
import org.apache.commons.beanutils.BeanUtils;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.modelling.command.Repository;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
public class UserProjector {

    private final UserRepositoryImpl userRepository;
    private final BigQueryUserRepository bigQueryUserRepository;
    private final Repository<UserAggregate> repository;

    public UserProjector(UserRepositoryImpl userRepository,
                         BigQueryUserRepository bigQueryUserRepository,
                         Repository<UserAggregate> repository) {
        this.userRepository = userRepository;
        this.bigQueryUserRepository = bigQueryUserRepository;
        this.repository = repository;
    }

    @EventHandler
    public void handle(UserAddedEvent userAddedEvent) throws InvocationTargetException, IllegalAccessException {
        User user = new User();
        BeanUtils.copyProperties(user, userAddedEvent);
        userRepository.save(user);
    }

    @EventHandler
    public void handle(UserDeletedEvent userDeletedEvent) throws ExecutionException, InterruptedException {
        userRepository.delete(userDeletedEvent.getId());
    }

    @EventHandler
    public void handle(UserUpdatedEvent userUpdatedEvent) throws InvocationTargetException, IllegalAccessException, ExecutionException, InterruptedException {
        User user = new User();
        BeanUtils.copyProperties(user, userUpdatedEvent);
        userRepository.update(user);
    }

    @QueryHandler
    public List<User> getUsers(FindUsersQuery query) throws ExecutionException, InterruptedException {
        return userRepository.findAll(query.getQueryParams());
    }

    @QueryHandler
    public User getUser(FindUserQuery findUserQuery) throws InterruptedException, ExecutionException, InvocationTargetException, IllegalAccessException {
        CompletableFuture<UserAggregate> future = new CompletableFuture<>();
        repository.load(findUserQuery.getId()).execute(future::complete);
        User user = new User();
        BeanUtils.copyProperties(user, future.get());
        return user;
    }

    @QueryHandler
    public List<User> getUsersFromBigQuery(FindAllUsersFromBigQuery getUsersFromBigQuery) {
        try {
            return bigQueryUserRepository.findAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
