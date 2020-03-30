package com.example.connector.axon.coreapi.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserCommand {

    @TargetAggregateIdentifier
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private String ipAddress;
}
