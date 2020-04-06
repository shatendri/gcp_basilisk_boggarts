package com.examples.pubsub.streaming.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

public class UserDtoValidator {

    private final static Logger LOG = LoggerFactory.getLogger(UserDtoValidator.class);

    private static final String EMAIL_PATTERN = "^[(a-zA-Z-0-9-\\_\\+\\.)]+@[(a-z-A-z)]+\\.[(a-zA-z)]{2,3}$";
    private static final String NAME_PATTERN = "[A-Z][a-z]*";
    private static final String IP_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    public static boolean isUserDtoValid(UserDto userDto) {
        return emailIsValid(userDto.getEmail()) &&
                ipAddressIsValid(userDto.getIp_address()) &&
                nameIsValid(userDto.getFirst_name()) &&
                nameIsValid(userDto.getLast_name());
    }

    private static boolean ipAddressIsValid(String ip) {
        if (ip == null || !Pattern.compile(IP_PATTERN).matcher(ip).matches()) {
            LOG.error(ip + " : is not valid ip address");
            return false;
        }
        return true;
    }

    private static boolean nameIsValid(String name) {
        if (name == null || name.isEmpty() || !Pattern.compile(NAME_PATTERN).matcher(name).matches()) {
            LOG.error(name + " : is not valid part of name");
            return false;
        }
        return true;
    }

    private static boolean emailIsValid(String email) {
        if (email == null || !Pattern.compile(EMAIL_PATTERN).matcher(email).matches()) {
            LOG.error(email + " : is not valid email");
            return false;
        }
        return true;
    }
}

