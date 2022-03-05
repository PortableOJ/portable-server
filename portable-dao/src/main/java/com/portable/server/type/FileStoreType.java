package com.portable.server.type;

import com.portable.server.exception.ExceptionTextType;
import com.portable.server.model.fs.FileData;
import lombok.Getter;

import java.util.regex.Pattern;

/**
 * @author shiroha
 */
@Getter
public enum FileStoreType implements ExceptionTextType {

    /**
     * 头像
     */
    AVATAR("头像", "image/.+", "/file/alkaline.jpg", "image/jpeg"),

    /**
     * 图片
     */
    IMAGE("图片", "image/.+", "/file/deathImage.jpeg", "image/jpeg"),
    ;

    private final String text;
    private final Pattern contentTypePattern;
    private final String defaultFile;
    private final String defaultFileContentType;

    FileStoreType(String text, String contentTypePattern, String defaultFile, String defaultFileContentType) {
        this.text = text;
        this.defaultFileContentType = defaultFileContentType;
        this.contentTypePattern = Pattern.compile(contentTypePattern);
        this.defaultFile = defaultFile;
    }

    public FileData getFile() {
        return FileData.builder()
                .inputStream(this.getClass().getResourceAsStream(this.defaultFile))
                .contentType(defaultFileContentType)
                .build();
    }
}
