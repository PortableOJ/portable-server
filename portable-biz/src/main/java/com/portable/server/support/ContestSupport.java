package com.portable.server.support;

import com.portable.server.exception.PortableException;
import com.portable.server.model.contest.ContestRankItem;

import java.util.List;

/**
 * @author shiroha
 */
public interface ContestSupport {

    /**
     * 添加/刷新比赛的榜单生成跟踪
     * @param contestId 比赛的 id
     */
    void addTraceRank(Long contestId);

    /**
     * 分页获取 rank 信息
     * @param contestId 比赛的 id
     * @param pageSize 每页几个
     * @param offset 偏移量
     * @return rank 列表
     */
    List<ContestRankItem> getContestRank(Long contestId, Integer pageSize, Integer offset) throws PortableException;

    /**
     * 获取某一个用户的比赛情况
     * @param contestId 比赛 id
     * @param userId 用户的 id
     * @return 当前用户的 rank 情况
     */
    ContestRankItem getContestByUserId(Long contestId, Long userId);
}
