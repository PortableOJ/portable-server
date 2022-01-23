package com.portable.server.type;

import com.portable.server.exception.ExceptionTextType;
import lombok.Getter;

/**
 * @author shiroha
 */

@Getter
public enum LanguageType implements ExceptionTextType {
    C89("GNU GCC C89", "C", "gnu89", ".c"),
    C99("GNU GCC C99","C", "gnu99", ".c"),
    C11("GNU GCC C11", "C","gnu11", ".c"),
    CPP98("GNU G++ c++98", "CPP","c++98", ".cpp"),
    CPP11("GNU G++ c++11", "CPP", "c++11", ".cpp"),
    CPP14("GNU G++ c++14", "CPP", "c++14", ".cpp");

    private final String text;
    private final String language;
    private final String params;
    private final String extension;

    LanguageType(String text, String language, String params, String extension) {
        this.text = text;
        this.language = language;
        this.params = params;
        this.extension = extension;
    }
}
