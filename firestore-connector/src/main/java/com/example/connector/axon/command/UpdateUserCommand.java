package com.example.connector.axon.command;

import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserCommand extends BaseUserCommand {

    @TargetAggregateIdentifier
    private String id;
}
