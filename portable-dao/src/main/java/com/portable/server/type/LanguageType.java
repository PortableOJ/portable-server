package com.portable.server.type;

import com.portable.server.exception.ExceptionTextType;
import lombok.Getter;

/**
 * @author shiroha
 */

@Getter
public enum LanguageType implements ExceptionTextType {
    C89("GNU GCC C89", "C"),
    C99("GNU GCC C99", "C"),
    C11("GNU GCC C11", "C"),
    CPP98("GNU G++ c++98", "CPP"),
    CPP11("GNU G++ c++11", "CPP"),
    CPP14("GNU G++ c++14", "CPP"),
    CPP17("GNU G++ c++17", "CPP"),
    ;

    private final String text;
    private final String language;

    LanguageType(String text, String language) {
        this.text = text;
        this.language = language;
    }
}
