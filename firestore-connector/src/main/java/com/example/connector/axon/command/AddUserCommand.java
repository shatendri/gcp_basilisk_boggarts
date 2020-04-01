package com.example.connector.axon.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddUserCommand extends BaseUserCommand {

    @TargetAggregateIdentifier
    private String id;
}
