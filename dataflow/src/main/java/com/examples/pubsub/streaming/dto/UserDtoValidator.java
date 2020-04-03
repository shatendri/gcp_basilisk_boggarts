package com.examples.pubsub.streaming.dto;

import java.util.regex.Pattern;

public class UserDtoValidator {

    private static final String EMAIL_PATTERN = "^[(a-zA-Z-0-9-\\_\\+\\.)]+@[(a-z-A-z)]+\\.[(a-zA-z)]{2,3}$";
    private static final String NAME_PATTERN = "[A-Z][a-z]*";
    private static final String IP_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    private static Pattern regexPattern;

    public static boolean isUserDtoValid(UserDto userDto) {
        return emailIsValid(userDto.getEmail()) &&
                ipAddressIsValid(userDto.getIp_address()) &&
                nameIsValid(userDto.getFirst_name()) &&
                nameIsValid(userDto.getLast_name());
    }

    private static boolean ipAddressIsValid(String ip) {
        regexPattern = Pattern.compile(IP_PATTERN);
        return regexPattern.matcher(ip).matches();
    }

    private static boolean nameIsValid(String name) {
        regexPattern = Pattern.compile(NAME_PATTERN);
        return name.length() > 0 && regexPattern.matcher(name).matches();
    }

    private static boolean emailIsValid(String email) {
        regexPattern = Pattern.compile(EMAIL_PATTERN);
        return regexPattern.matcher(email).matches();
    }
}

