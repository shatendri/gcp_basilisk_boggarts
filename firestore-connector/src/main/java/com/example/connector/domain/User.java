package com.example.connector.domain;

import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private String ipAddress;
}
