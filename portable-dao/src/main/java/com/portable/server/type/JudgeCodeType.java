package com.portable.server.type;

import com.portable.server.exception.ExceptionTextType;
import com.portable.server.exception.PortableException;
import lombok.Getter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 使用的 Judge 模式
 *
 * @author shiroha
 */
@Getter
public enum JudgeCodeType implements ExceptionTextType {

    /**
     * 自定义
     */
    DIY("自定义"),
    ALL_SAME("完全一致"),
    IGNORE_END_OF_FILE("无视文件尾的换行"),
    WORD_SAME("按顺序比较有效字符串，全部相同"),
    YES_NO("按顺序比较 yes 和 no，全部相同"),
    INTEGER_SAME("按顺序比较 64 位整数，全部相同"),
    FLOAT_4("按顺序比较浮点数，允许 1e-4 的精度误差"),
    FLOAT_6("按顺序比较浮点数，允许 1e-6 的精度误差"),
    ;

    private final String text;

    JudgeCodeType(String text) {
        this.text = text;
    }

    public File getCode() throws PortableException {
        if (this == DIY) {
            throw PortableException.of("S-01-006");
        }
        ClassPathResource resource = new ClassPathResource(String.format("judge%s%s.cpp", File.separator, this.name()));
        try {
            return resource.getFile();
        } catch (IOException e) {
            throw PortableException.of("S-01-005", name());
        }
    }

    public static File getTestLib() throws PortableException {
        ClassPathResource resource = new ClassPathResource(String.format("judge%stestlib.h", File.separator));
        try {
            return resource.getFile();
        } catch (IOException e) {
            throw PortableException.of("S-01-005", "testlib");
        }
    }
}
