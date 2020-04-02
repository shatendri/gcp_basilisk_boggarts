package com.example.connector.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private String id;
    private String first_name;
    private String last_name;
    private String email;
    private String gender;
    private String ip_address;
}
