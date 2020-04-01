package com.example.connector.axon;

import com.example.connector.axon.command.DeleteUserCommand;
import com.example.connector.axon.command.AddUserCommand;
import com.example.connector.axon.command.UpdateUserCommand;
import com.example.connector.axon.event.UserAddedEvent;
import com.example.connector.axon.event.UserDeletedEvent;
import com.example.connector.axon.event.UserUpdatedEvent;
import lombok.Data;
import org.apache.commons.beanutils.BeanUtils;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.lang.reflect.InvocationTargetException;

@Data
@Aggregate
public class UserAggregate {

    @AggregateIdentifier
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private String ipAddress;

    public UserAggregate() {
        // For Axon instantiation
    }


    @CommandHandler
    public UserAggregate(AddUserCommand addUserCommand) throws InvocationTargetException, IllegalAccessException {
        UserAddedEvent userAddedEvent = new UserAddedEvent();
        BeanUtils.copyProperties(userAddedEvent, addUserCommand);
        AggregateLifecycle.apply(userAddedEvent);
    }

    @EventSourcingHandler
    private void on(UserAddedEvent userAddedEvent) throws InvocationTargetException, IllegalAccessException {
        BeanUtils.copyProperties(this, userAddedEvent);
    }

    @CommandHandler
    public void handle(UpdateUserCommand updateUserCommand) throws InvocationTargetException, IllegalAccessException {
        UserUpdatedEvent userUpdatedEvent = new UserUpdatedEvent();
        BeanUtils.copyProperties(userUpdatedEvent, updateUserCommand);
        AggregateLifecycle.apply(userUpdatedEvent);
    }

    @EventSourcingHandler
    private void on(UserUpdatedEvent userUpdatedEvent) throws InvocationTargetException, IllegalAccessException {
        BeanUtils.copyProperties(this, userUpdatedEvent);
    }

    @CommandHandler
    public void handle(DeleteUserCommand deleteUserCommand) {
        UserDeletedEvent userDeletedEvent = new UserDeletedEvent(deleteUserCommand.getId());
        AggregateLifecycle.apply(userDeletedEvent);
    }

    @EventSourcingHandler
    private void on(UserDeletedEvent userDeletedEvent) {
        this.setId(userDeletedEvent.getId());
        AggregateLifecycle.markDeleted();
    }
}
