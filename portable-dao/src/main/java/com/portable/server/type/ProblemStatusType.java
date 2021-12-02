package com.portable.server.type;

import com.portable.server.exception.ExceptionTextType;
import com.portable.server.exception.PortableException;
import lombok.Getter;

/**
 * @author shiroha
 */
@Getter
public enum ProblemStatusType implements ExceptionTextType {

    /**
     * 正常
     */
    NORMAL("正常", false) {
        @Override
        public ProblemStatusType toUntreated() {
            return UNTREATED;
        }

        @Override
        public ProblemStatusType toUncheck() {
            return UNCHECK;
        }

        @Override
        public ProblemStatusType toBuild() {
            return NORMAL;
        }
    },

    /**
     * 没有处理过的题目
     */
    UNTREATED("未处理", false) {
        @Override
        public ProblemStatusType toUntreated() {
            return UNTREATED;
        }

        @Override
        public ProblemStatusType toUncheck() {
            return UNTREATED;
        }

        @Override
        public ProblemStatusType toBuild() {
            return TREATING;
        }
    },

    /**
     * 处理中
     */
    TREATING("处理中", true) {
        @Override
        public ProblemStatusType toUntreated() throws PortableException {
            throw PortableException.of("S-01-004", TREATING, "toUntreated");
        }

        @Override
        public ProblemStatusType toUncheck() throws PortableException {
            throw PortableException.of("S-01-004", TREATING, "toUncheck");
        }

        @Override
        public ProblemStatusType toBuild() throws PortableException {
            throw PortableException.of("S-01-004", TREATING, "toBuild");
        }
    },

    /**
     * 未校验
     */
    UNCHECK("未校验", false) {
        @Override
        public ProblemStatusType toUntreated() {
            return UNTREATED;
        }

        @Override
        public ProblemStatusType toUncheck() {
            return UNCHECK;
        }

        @Override
        public ProblemStatusType toBuild() {
            return CHECKING;
        }
    },

    /**
     * 校验中
     */
    CHECKING("校验中", true) {
        @Override
        public ProblemStatusType toUntreated() throws PortableException {
            throw PortableException.of("S-01-004", CHECKING, "toUntreated");
        }

        @Override
        public ProblemStatusType toUncheck() throws PortableException {
            throw PortableException.of("S-01-004", CHECKING, "toUncheck");
        }

        @Override
        public ProblemStatusType toBuild() throws PortableException {
            throw PortableException.of("S-01-004", CHECKING, "toBuild");
        }
    },

    /**
     * 处理失败
     */
    TREAT_FAILED("处理失败", false) {
        @Override
        public ProblemStatusType toUntreated() {
            return UNTREATED;
        }

        @Override
        public ProblemStatusType toUncheck() {
            return UNTREATED;
        }

        @Override
        public ProblemStatusType toBuild() {
            return TREATING;
        }
    },

    /**
     * 校验失败
     */
    CHECK_FAILED("校验失败", false) {
        @Override
        public ProblemStatusType toUntreated() {
            return UNTREATED;
        }

        @Override
        public ProblemStatusType toUncheck() {
            return UNCHECK;
        }

        @Override
        public ProblemStatusType toBuild() {
            return CHECKING;
        }
    },
    ;

    private final String text;
    private final Boolean onTreatedOrCheck;

    ProblemStatusType(String text, Boolean onTreatedOrCheck) {
        this.text = text;
        this.onTreatedOrCheck = onTreatedOrCheck;
    }

    public abstract ProblemStatusType toUntreated() throws PortableException;

    public abstract ProblemStatusType toUncheck() throws PortableException;

    public abstract ProblemStatusType toBuild() throws PortableException;
}
