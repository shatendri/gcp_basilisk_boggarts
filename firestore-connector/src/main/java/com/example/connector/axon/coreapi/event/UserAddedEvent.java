package com.example.connector.axon.coreapi.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAddedEvent {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private String ipAddress;
}
