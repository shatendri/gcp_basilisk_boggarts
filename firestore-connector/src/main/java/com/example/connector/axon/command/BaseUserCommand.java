package com.example.connector.axon.command;

import lombok.Data;

@Data
public abstract class BaseUserCommand {

    protected String firstName;
    protected String lastName;
    protected String email;
    protected String gender;
    protected String ipAddress;
}
