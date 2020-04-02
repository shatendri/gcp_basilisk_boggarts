package com.example.connector.util;

import com.example.connector.domain.User;

import java.util.HashMap;
import java.util.Map;

public class UserMapToDtoConverter {

    public static Map<String, String> convertFrom(User user) {
        Map<String, String> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("first_name", user.getFirstName());
        map.put("last_name", user.getLastName());
        map.put("email", user.getEmail());
        map.put("gender", user.getGender());
        map.put("ip_address", user.getIpAddress());
        return map;
    }

    public static User convertFrom(Map<String, String> userMap) {
        return User.builder()
                .id(userMap.get("id"))
                .firstName(userMap.get("first_name"))
                .lastName(userMap.get("last_name"))
                .email(userMap.get("email"))
                .gender(userMap.get("gender"))
                .ipAddress(userMap.get("ip_address"))
                .build();
    }

}
