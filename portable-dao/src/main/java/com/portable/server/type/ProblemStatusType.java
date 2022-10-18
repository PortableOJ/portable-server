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
    NORMAL("正常", false, true, true) {

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
    UNTREATED("未处理", false, false, false) {

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
     * 等待处理
     */
    PENDING("等待处理", true, false, false),

    /**
     * 处理中
     */
    TREATING("处理中", true, false, false),

    /**
     * 未校验
     */
    UNCHECK("未校验", false, true, false) {

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
    CHECKING("校验中", true, true, false),

    /**
     * 处理失败
     */
    TREAT_FAILED("处理失败", false, false, false) {
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
    CHECK_FAILED("校验失败", false, true, false) {
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
    private final Boolean treated;
    private final Boolean checked;

    ProblemStatusType(String text, Boolean onTreatedOrCheck, Boolean treated, Boolean checked) {
        this.text = text;
        this.onTreatedOrCheck = onTreatedOrCheck;
        this.treated = treated;
        this.checked = checked;
    }

    public ProblemStatusType toUntreated() {
        throw PortableException.of("S-01-004", this, "toUntreated");
    }

    public ProblemStatusType toUncheck() {
        throw PortableException.of("S-01-004", this, "toUncheck");
    }

    public ProblemStatusType toBuild() {
        throw PortableException.of("S-01-004", this, "toBuild");
    }
}
