package com.portable.server.model.request.batch;

import com.portable.server.model.batch.Batch;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author shiroha
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchRequest {

    /**
     * 批量用户的前缀词
     */
    @NotNull(message = "A-10-003")
    @Pattern(message = "A-10-003", regexp = "^[a-zA-Z0-9_\\-]{3,10}$")
    private String prefix;

    /**
     * 批量用户的数量
     */
    @NotNull(message = "A-10-004")
    @Range(min = 10, max = 500, message = "A-10-004")
    private Integer count;

    /**
     * 是否进行 ip 锁
     */
    @NotNull(message = "A-10-005")
    private Boolean ipLock;

    public void toBatch(Batch batch) {
        batch.setPrefix(this.prefix);
        batch.setCount(this.count);
        batch.setIpLock(this.ipLock);
    }
}
