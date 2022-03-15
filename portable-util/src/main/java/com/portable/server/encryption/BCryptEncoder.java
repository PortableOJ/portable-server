package com.portable.server.encryption;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @author shiroha
 */
@Component
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class BCryptEncoder {

    private static final BCryptPasswordEncoder B_CRYPT_PASSWORD_ENCODER;

    static {
        B_CRYPT_PASSWORD_ENCODER = new BCryptPasswordEncoder();
    }

    public static String encoder(String password) {
        return B_CRYPT_PASSWORD_ENCODER.encode(password);
    }

    public static Boolean match(String inputPassword, String password) {
        return B_CRYPT_PASSWORD_ENCODER.matches(inputPassword, password);
    }
}
