package com.portable.server.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import com.portable.server.model.batch.Batch;
import com.portable.server.model.user.NormalUserData;
import com.portable.server.model.user.User;
import com.portable.server.type.AccountType;
import com.portable.server.type.ContestVisitType;
import com.portable.server.type.OrganizationType;
import com.portable.server.type.PermissionType;

import lombok.Data;

/**
 * @author shiroha
 */
@Data
public class UserContext {

    /// region 用户信息

    /**
     * 当前用户 id
     */
    private Long id;

    /**
     * 当前用户的 dataId
     */
    private String dataId;

    /**
     * 用户名
     */
    private String handle;

    /**
     * 当前用户的类型
     */
    private AccountType type;

    /**
     * 当前用户的所在组织
     */
    private OrganizationType organization;

    /**
     * 当前用户的权限集合
     */
    private Set<PermissionType> permissionTypeSet;

    /**
     * 批量用户绑定至的比赛 ID
     */
    private Long contestId;

    /**
     * 用户验证过的比赛情况
     */
    private Map<Long, ContestVisitType> contestVisitPermissionMap;

    /**
     * 用户上一次使用某个业务的统计器
     */
    private Map<String, Long> userCaptchaMap;

    /// endregion

    /// region 静态变量

    /**
     * 当前登陆的用户信息
     */
    private static final ThreadLocal<UserContext> LOCAL;

    /**
     * 用户信息一级缓存容量
     */
    private static final Integer USER_CONTEXT_CACHE_SIZE = 500;

    /**
     * 用户信息一级缓存过期时间(s)
     */
    private static final Integer USER_CONTEXT_EXPIRE_AFTER_ACCESS = 600;

    /**
     * 用户信息二级缓存过期时间(m)
     */
    private static final Integer USER_CONTEXT_EXPIRE_LEVEL_2 = 60;

    /**
     * 用户二级缓存的前缀
     */
    private static final String USER_CONTEST_CACHE_PREFIX = "USER_CONTEST";

    /// endregion

    static {
        LOCAL = ThreadLocal.withInitial(UserContext::new);
    }

    public static UserContext ctx() {
        return LOCAL.get();
    }

    public static Boolean restore(Long userId) {
        try {
            UserContext userContext = USER_CACHE.get(userId);
            LOCAL.set(userContext);
            return Objects.nonNull(userContext.getId());
        } catch (ExecutionException ignored) {
        }
        return false;
    }

    public static void set(UserContext userContext) {
        LOCAL.set(userContext);
        if (userContext.getId() != null) {
            USER_CACHE.put(userContext.getId(), userContext);
            staticRedisValueHelper.set(USER_CONTEST_CACHE_PREFIX, userContext.getId(), userContext, Long.valueOf(USER_CONTEXT_EXPIRE_LEVEL_2));
        }
    }

    public static void set(User user) {
        if (Objects.isNull(user)) {
            throw PortableErrors.of("A-02-001");
        }

        UserContext userContext = ctx();
        userContext.setId(user.getId());
        userContext.setHandle(user.getHandle());
        userContext.setDataId(user.getDataId());
        userContext.setType(user.getType());
        set(userContext);
    }

    public static void set(NormalUserData normalUserData) {
        if (normalUserData == null) {
            throw PortableErrors.of("A-02-001");
        }

        UserContext userContext = ctx();
        userContext.setOrganization(normalUserData.getOrganization());
        userContext.setPermissionTypeSet(normalUserData.getPermissionTypeSet());
        set(userContext);
    }

    public static void set(Batch batch) {
        if (batch == null) {
            throw PortableErrors.of("A-02-001");
        }

        UserContext userContext = ctx();
        userContext.setContestId(batch.getContestId());
        set(userContext);
    }

    public static void addCurUserContestVisit(Long contestId, ContestVisitType contestVisitType) {
        UserContext userContext = ctx();
        userContext.addContestVisit(contestId, contestVisitType);
    }

    public static void remove() {
        LOCAL.remove();
    }

    public static UserContext getNullUser() {
        UserContext userContext = new UserContext();
        userContext.setId(null);
        userContext.setDataId(null);
        userContext.setType(null);
        userContext.setOrganization(null);
        userContext.setContestId(null);
        userContext.setPermissionTypeSet(new HashSet<>());
        userContext.setContestVisitPermissionMap(new HashMap<>(0));
        userContext.setUserCaptchaMap(new HashMap<>(0));
        return userContext;
    }

    public Boolean isLogin() {
        return id != null;
    }

    public void addContestVisit(Long contestId, ContestVisitType contestVisitType) {
        contestVisitPermissionMap.put(contestId, contestVisitType);
        set(this);
    }
}
