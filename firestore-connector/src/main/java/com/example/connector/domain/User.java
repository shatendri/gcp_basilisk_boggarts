package com.example.connector.domain;

import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    private String id;
    private String first_name;
    private String last_name;
    private String email;
    private String gender;
    private String ip_address;
}
