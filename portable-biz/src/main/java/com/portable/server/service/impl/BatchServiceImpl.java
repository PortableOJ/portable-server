package com.portable.server.service.impl;

import com.portable.server.encryption.BCryptEncoder;
import com.portable.server.exception.PortableException;
import com.portable.server.manager.BatchManager;
import com.portable.server.manager.ContestManager;
import com.portable.server.manager.UserDataManager;
import com.portable.server.manager.UserManager;
import com.portable.server.model.batch.Batch;
import com.portable.server.model.contest.Contest;
import com.portable.server.model.request.PageRequest;
import com.portable.server.model.request.batch.BatchRequest;
import com.portable.server.model.response.PageResponse;
import com.portable.server.model.response.batch.BatchListResponse;
import com.portable.server.model.response.batch.CreateBatchResponse;
import com.portable.server.model.user.BatchUserData;
import com.portable.server.model.user.User;
import com.portable.server.service.BatchService;
import com.portable.server.type.BatchStatusType;
import com.portable.server.util.UserContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author shiroha
 */
@Component
public class BatchServiceImpl implements BatchService {

    @Resource
    private UserManager userManager;

    @Resource
    private UserDataManager userDataManager;

    @Resource
    private ContestManager contestManager;

    @Resource
    private BatchManager batchManager;

    /**
     * 批量用户的账号格式
     */
    private static final String BATCH_FORMAT = "%s@%03d";

    /**
     * 随机函数
     */
    private static final Random RANDOM = new Random();

    /**
     * 随机密码的长度
     */
    private static final Integer PASSWORD_LEN = 8;

    @Override
    public PageResponse<BatchListResponse, Void> getList(PageRequest<Void> pageRequest) {
        Long userId = UserContext.ctx().getId();
        Integer batchCount = batchManager.countBatchListByOwnerId(userId);
        PageResponse<BatchListResponse, Void> response = PageResponse.of(pageRequest, batchCount);
        List<Batch> batchList = batchManager.selectBatchByPage(userId, response.getPageSize(), response.offset());
        List<BatchListResponse> batchListResponseList = batchList.stream()
                .parallel()
                .map(batch -> {
                    if (batch.getContestId() == null) {
                        return BatchListResponse.of(batch, null);
                    }
                    Contest contest = contestManager.getContestById(batch.getContestId());
                    return BatchListResponse.of(batch, contest);
                })
                .collect(Collectors.toList());
        response.setData(batchListResponseList);
        return response;
    }

    @Override
    public CreateBatchResponse create(BatchRequest request) throws PortableException {
        Batch batch;
        synchronized (this) {
            batch = batchManager.selectBatchByPrefix(request.getPrefix());
            if (batch != null) {
                throw PortableException.of("A-10-001", request.getPrefix());
            }
            batch = batchManager.newBatch();
            request.toBatch(batch);
            batch.setOwner(UserContext.ctx().getId());
            batchManager.insertBatch(batch);
        }
        final Batch finalBatch = batch;
        CreateBatchResponse createBatchResponse = CreateBatchResponse.of(batch.getId());
        IntStream.rangeClosed(1, request.getCount())
                .boxed()
                .forEachOrdered(i -> {
                            BatchUserData batchUserData = userDataManager.newBatchUserData();
                            batchUserData.setBatchId(finalBatch.getId());
                            userDataManager.insertUserData(batchUserData);

                            User user = userManager.newBatchAccount();
                            user.setDataId(batchUserData.get_id());
                            user.setHandle(String.format(BATCH_FORMAT, request.getPrefix(), i));
                            StringBuilder stringBuilder = new StringBuilder();
                            IntStream.range(0, PASSWORD_LEN)
                                    .forEach(t -> stringBuilder.append(RANDOM.nextInt(10)));
                            // 在密码还是原来的非加密态时保存至返回值
                            user.setPassword(stringBuilder.toString());

                            createBatchResponse.add(user);

                            user.setPassword(BCryptEncoder.encoder(user.getPassword()));

                            userManager.insertAccount(user);
                        }
                );
        return createBatchResponse;
    }

    @Override
    public void changeStatus(Long id, BatchStatusType statusType) throws PortableException {
        Batch batch = batchManager.selectBatchById(id);
        if (batch == null) {
            throw PortableException.of("A-10-006", id);
        }
        if (!Objects.equals(batch.getOwner(), UserContext.ctx().getId())) {
            throw PortableException.of("A-10-002");
        }
        batchManager.updateBatchStatus(id, statusType);
    }

    @Override
    public BatchListResponse getBatch(Long id) throws PortableException {
        Batch batch = batchManager.selectBatchById(id);
        if (batch == null) {
            throw PortableException.of("A-10-006", id);
        }
        if (!Objects.equals(batch.getOwner(), UserContext.ctx().getId())) {
            throw PortableException.of("A-10-002");
        }
        Contest contest = null;
        if (batch.getContestId() != null) {
            contest = contestManager.getContestById(batch.getContestId());
        }
        return BatchListResponse.of(batch, contest);
    }
}
