package com.portable.server.type;

import com.portable.server.exception.ExceptionTextType;
import lombok.Getter;

/**
 * @author shiroha
 */

@Getter
public enum LanguageType implements ExceptionTextType {

    /**
     * C99
     */
    C99("GNU GCC C99", "C"),

    /**
     * C++11
     */
    CPP11("GNU G++ C++11", "CPP"),

    /**
     * C++17
     */
    CPP17("GNU G++ C++17", "CPP"),
    ;

    private final String text;
    private final String language;

    LanguageType(String text, String language) {
        this.text = text;
        this.language = language;
    }
}
