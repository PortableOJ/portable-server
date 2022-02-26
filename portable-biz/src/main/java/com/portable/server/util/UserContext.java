package com.portable.server.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.portable.server.exception.PortableException;
import com.portable.server.type.ContestVisitPermission;
import com.portable.server.model.user.NormalUserData;
import com.portable.server.model.user.User;
import com.portable.server.type.AccountType;
import com.portable.server.type.OrganizationType;
import com.portable.server.type.PermissionType;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author shiroha
 */
@Data
public class UserContext implements AutoCloseable {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

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
     * 用户验证过的比赛情况
     */
    private Map<Long, ContestVisitPermission> contestVisitPermissionMap;

    /**
     * 当前登陆的用户信息
     */
    private static final ThreadLocal<UserContext> LOCAL;

    /**
     * 缓存的登陆过的用户信息
     */
    private static final LoadingCache<Long, UserContext> USER_CACHE;

    /**
     * 用户信息使用的 redis 缓存
     */
    private static StringRedisTemplate redisTemplate;

    /**
     * 用户信息一级缓存容量
     */
    private static final Integer USER_CONTEXT_CACHE_SIZE = 500;

    /**
     * 用户信息一级缓存过期时间(s)
     */
    private static final Integer USER_CONTEXT_EXPIRE_AFTER_ACCESS = 3600;

    /**
     * 用户信息二级缓存过期时间(m)
     */
    private static final Integer USER_CONTEXT_EXPIRE_LEVEL_2 = 30;

    static {
        LOCAL = ThreadLocal.withInitial(UserContext::new);
        USER_CACHE = CacheBuilder.newBuilder()
                .maximumSize(USER_CONTEXT_CACHE_SIZE)
                .expireAfterAccess(USER_CONTEXT_EXPIRE_AFTER_ACCESS, TimeUnit.SECONDS)
                .build(new CacheLoader<Long, UserContext>() {
                    @Override
                    public UserContext load(@NonNull Long aLong) {
                        String redisResult = redisTemplate.opsForValue().get(aLong.toString());
                        if (Objects.isNull(redisResult) || redisResult.isEmpty()) {
                            return getNullUser();
                        }
                        return JsonUtils.toObject(redisResult, UserContext.class);
                    }
                });
    }

    @PostConstruct
    public void  init() {
        UserContext.redisTemplate = this.stringRedisTemplate;
    }

    public static UserContext ctx() {
        return LOCAL.get();
    }

    public static Boolean restore(Long userId) {
        try {
            UserContext userContext = USER_CACHE.get(userId);
            LOCAL.set(userContext);
            return !Objects.isNull(userContext.getId());
        } catch (ExecutionException ignored) {
        }
        return false;
    }

    public static void set(UserContext userContext) {
        LOCAL.set(userContext);
        if (userContext.getId() != null) {
            USER_CACHE.put(userContext.getId(), userContext);
            redisTemplate.opsForValue().set(
                    userContext.getId().toString(),
                    JsonUtils.toString(userContext),
                    USER_CONTEXT_EXPIRE_LEVEL_2,
                    TimeUnit.MINUTES);
        }
    }

    public static void set(User user) throws PortableException {
        if (Objects.isNull(user)) {
            throw PortableException.of("A-02-001");
        }

        UserContext userContext = ctx();
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

        UserContext userContext = ctx();
        userContext.setOrganization(normalUserData.getOrganization());
        userContext.setPermissionTypeSet(normalUserData.getPermissionTypeSet());
        set(userContext);
    }

    public static void addCurUserContestVisit(Long contestId, ContestVisitPermission contestVisitPermission) {
        UserContext userContext = ctx();
        userContext.addContestVisit(contestId, contestVisitPermission);
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
        userContext.setContestVisitPermissionMap(new HashMap<>(0));
        return userContext;
    }

    public Boolean isLogin() {
        return id != null;
    }

    public void addContestVisit(Long contestId, ContestVisitPermission contestVisitPermission) {
        contestVisitPermissionMap.put(contestId, contestVisitPermission);
        set(this);
    }

    @Override
    public void close() {
        LOCAL.remove();
    }
}
