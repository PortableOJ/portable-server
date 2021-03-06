package com.portable.server.service;

import com.portable.server.exception.PortableException;
import com.portable.server.model.request.PageRequest;
import com.portable.server.model.request.batch.BatchRequest;
import com.portable.server.model.response.PageResponse;
import com.portable.server.model.response.batch.BatchListResponse;
import com.portable.server.model.response.batch.CreateBatchResponse;
import com.portable.server.type.BatchStatusType;

/**
 * @author shiroha
 */
public interface BatchService {

    /**
     * 分页获取批量用户列表
     *
     * @param pageRequest 分页配置
     * @return 批量用户列表
     */
    PageResponse<BatchListResponse, Void> getList(PageRequest<Void> pageRequest);

    /**
     * 创建批量用户
     *
     * @param request 请求创建的内容
     * @return 创建的用户信息
     * @throws PortableException 已经存在则抛出错误
     */
    CreateBatchResponse create(BatchRequest request) throws PortableException;

    /**
     * 更新批量用户的状态
     *
     * @param id         批量用户的 ID
     * @param statusType 新的状态
     * @throws PortableException 无权限则抛出
     */
    void changeStatus(Long id, BatchStatusType statusType) throws PortableException;

    /**
     * 查找自己拥有的批量用户组
     *
     * @param id 用户组 ID
     * @return 用户组信息
     * @throws PortableException 无权限则抛出
     */
    BatchListResponse getBatch(Long id) throws PortableException;

    /**
     * 更新批量用户组的 ip 锁状态
     *
     * @param id     用户组的 ID
     * @param ipLock 新的锁状态
     * @throws PortableException 不存在此组则抛出
     */
    void changeBatchIpLock(Long id, Boolean ipLock) throws PortableException;
}
