package com.example.connector.axon.event;

import lombok.Data;

@Data
public abstract class BaseUserEvent {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private String ipAddress;
}
