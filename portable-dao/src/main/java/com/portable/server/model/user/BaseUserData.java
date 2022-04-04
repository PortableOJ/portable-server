package com.portable.server.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;

/**
 * @author shiroha
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseUserData {

    /**
     * 数据库主键
     */
    @Id
    @SuppressWarnings("AlibabaAvoidStartWithDollarAndUnderLineNaming")
    private String _id;

}
