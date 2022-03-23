package com.portable.server.controller;

import com.portable.server.annotation.NeedLogin;
import com.portable.server.annotation.PermissionRequirement;
import com.portable.server.exception.PortableException;
import com.portable.server.model.request.PageRequest;
import com.portable.server.model.request.batch.BatchIpLockUpdateRequest;
import com.portable.server.model.request.batch.BatchRequest;
import com.portable.server.model.request.batch.BatchStatusUpdateRequest;
import com.portable.server.model.response.PageResponse;
import com.portable.server.model.response.Response;
import com.portable.server.model.response.batch.BatchListResponse;
import com.portable.server.model.response.batch.CreateBatchResponse;
import com.portable.server.service.BatchService;
import com.portable.server.type.PermissionType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author shiroha
 */
@Validated
@RestController
@RequestMapping("/api/batch")
public class BatchController {

    @Resource
    private BatchService batchService;

    @NeedLogin
    @GetMapping("/get")
    @PermissionRequirement(PermissionType.CREATE_AND_EDIT_BATCH)
    public Response<PageResponse<BatchListResponse, Void>> getBatchList(Integer pageNum, Integer pageSize) {
        PageRequest<Void> pageRequest = PageRequest.<Void>builder()
                .pageNum(pageNum)
                .pageSize(pageSize)
                .build();
        pageRequest.verify();
        return Response.ofOk(batchService.getList(pageRequest));
    }

    @NeedLogin
    @PostMapping("/new")
    @PermissionRequirement(PermissionType.CREATE_AND_EDIT_BATCH)
    public Response<CreateBatchResponse> create(@Validated @RequestBody BatchRequest batchRequest) throws PortableException {
        return Response.ofOk(batchService.create(batchRequest));
    }

    @NeedLogin
    @PostMapping("/updateStatus")
    @PermissionRequirement(PermissionType.CREATE_AND_EDIT_BATCH)
    public Response<Void> updateStatus(@Validated @RequestBody BatchStatusUpdateRequest request) throws PortableException {
        batchService.changeStatus(request.getId(), request.getNewStatus());
        return Response.ofOk();
    }

    @NeedLogin
    @GetMapping("/check")
    @PermissionRequirement(PermissionType.CREATE_AND_EDIT_BATCH)
    public Response<BatchListResponse> getBatch(Long id) throws PortableException {
        return Response.ofOk(batchService.getBatch(id));
    }

    @NeedLogin
    @PostMapping("/updateStatus")
    @PermissionRequirement(PermissionType.CREATE_AND_EDIT_BATCH)
    public Response<Void> updateIpLock(@Validated @RequestBody BatchIpLockUpdateRequest request) throws PortableException {
        batchService.changeBatchIpLock(request.getId(), request.getIpLock());
        return Response.ofOk();
    }
}
