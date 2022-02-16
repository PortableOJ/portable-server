package com.portable.server.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.portable.server.exception.PortableException;
import com.portable.server.model.user.User;
import com.portable.server.model.user.NormalUserData;
import com.portable.server.type.AccountType;
import com.portable.server.type.OrganizationType;
import com.portable.server.type.PermissionType;
import lombok.Data;
import lombok.NonNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author shiroha
 */
@Data
public class UserContext implements AutoCloseable {

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
     * 当前登陆的用户信息
     */
    private static final ThreadLocal<UserContext> LOCAL;

    /**
     * 缓存的登陆过的用户信息
     */
    private static final LoadingCache<Long, UserContext> USER_CACHE;

    /**
     * 用户信息缓存大小
     */
    public static final Integer USER_CONTEXT_CACHE_SIZE = 500;

    /**
     * 用户信息缓存过期时间(s)
     */
    public static final Integer USER_CONTEXT_EXPIRE_AFTER_ACCESS = 3600;

    static {
        LOCAL = ThreadLocal.withInitial(UserContext::new);
        USER_CACHE = CacheBuilder.newBuilder()
                .maximumSize(USER_CONTEXT_CACHE_SIZE)
                .expireAfterAccess(USER_CONTEXT_EXPIRE_AFTER_ACCESS, TimeUnit.SECONDS)
                .build(new CacheLoader<Long, UserContext>() {
                    @Override
                    public UserContext load(@NonNull Long aLong) {
                        return getNullUser();
                    }
                });
    }

    public static UserContext ctx() {
        return LOCAL.get();
    }

    public static void restore(Long userId) {
        try {
            UserContext userContext = USER_CACHE.get(userId);
            LOCAL.set(userContext);
        } catch (ExecutionException ignored) {
        }
    }

    public static void set(UserContext userContext) {
        LOCAL.set(userContext);
        if (userContext.getId() != null) {
            USER_CACHE.put(userContext.getId(), userContext);
        }
    }

    public static void set(User user) throws PortableException {
        if (Objects.isNull(user)) {
            throw PortableException.of("A-02-001");
        }

        UserContext userContext = LOCAL.get();
        userContext.setId(user.getId());
        userContext.setHandle(user.getHandle());
        userContext.setDataId(user.getDataId());
        userContext.setType(user.getType());
        set(userContext);
    }

    public static void set(NormalUserData normalUserData) throws PortableException {
        if (normalUserData == null) {
            throw PortableException.of("A-02-001");
        }

        UserContext userContext = LOCAL.get();
        userContext.setOrganization(normalUserData.getOrganization());
        userContext.setPermissionTypeSet(normalUserData.getPermissionTypeSet());
        set(userContext);
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
        userContext.setPermissionTypeSet(new HashSet<>());
        return userContext;
    }

    public Boolean isLogin() {
        return id != null;
    }

    @Override
    public void close() {
        LOCAL.remove();
    }
}
