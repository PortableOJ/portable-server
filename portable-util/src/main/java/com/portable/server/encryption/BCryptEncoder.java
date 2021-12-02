package com.portable.server.encryption;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptEncoder {

    private static final BCryptPasswordEncoder bCryptPasswordEncoder;

    static {
        bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    public String encoder(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    public Boolean match(String inputPassword, String password) {
        return bCryptPasswordEncoder.matches(inputPassword, password);
    }
}
