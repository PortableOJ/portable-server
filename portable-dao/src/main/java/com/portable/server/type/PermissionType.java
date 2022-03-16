package com.portable.server.type;

import com.portable.server.exception.ExceptionTextType;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

/**
 * 权限类型
 *
 * @author shiroha
 */
@Getter
public enum PermissionType implements ExceptionTextType {

    /**
     * 修改其他用户的所属用户组
     */
    CHANGE_ORGANIZATION("修改用户组"),

    /**
     * 授权/收回其他用户拥有的权力
     */
    GRANT("授权"),

    /**
     * 查看隐藏题目
     */
    VIEW_HIDDEN_PROBLEM("查看隐藏题目"),

    /**
     * 变更(创建/编辑/删除)题目权限
     * <p>
     * 仅对自己拥有对题目生效
     * </p>
     */
    CREATE_AND_EDIT_PROBLEM("创建编辑自己拥有的题目"),

    /**
     * 编辑所有可访问的题目
     */
    EDIT_NOT_OWNER_PROBLEM("编辑所有可访问的题目"),

    /**
     * 查看所有公开提交的提交详情
     */
    VIEW_PUBLIC_SOLUTION("查看所有公开提交的提交详情"),

    /**
     * 查看提交的运行信息
     */
    VIEW_SOLUTION_MESSAGE("查看提交的运行信息"),

    /**
     * 查看所有比赛
     */
    VIEW_ALL_CONTEST("查看所有比赛"),

    /**
     * 创建和更新比赛内容权利
     */
    CREATE_AND_EDIT_CONTEST("创建和更新比赛内容"),

    /**
     * 管理其他人拥有的比赛内容权利
     */
    EDIT_NOT_OWNER_CONTEST("管理其他人拥有的比赛内容"),

    /**
     * 创建和管理批量用户的权利
     */
    CREATE_AND_EDIT_BATCH("创建和管理批量用户"),

    /**
     * 管理 Judge 系统的权利
     */
    MANAGER_JUDGE("管理 Judge 系统的权利"),
    ;

    private final String text;

    PermissionType(String text) {
        this.text = text;
    }

    public static Set<PermissionType> defaultPermission() {
        return new HashSet<>();
    }
}
