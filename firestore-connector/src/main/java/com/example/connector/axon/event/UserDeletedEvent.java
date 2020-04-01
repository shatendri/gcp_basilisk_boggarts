package com.example.connector.axon.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDeletedEvent {

    private String id;
}
