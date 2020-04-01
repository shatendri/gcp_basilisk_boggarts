package com.example.connector.axon.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@AllArgsConstructor
public class DeleteUserCommand {

    @TargetAggregateIdentifier
    private String id;
}
