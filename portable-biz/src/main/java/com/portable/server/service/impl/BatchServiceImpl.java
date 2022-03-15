package com.portable.server.service.impl;

import com.portable.server.encryption.BCryptEncoder;
import com.portable.server.exception.PortableException;
import com.portable.server.manager.BatchManager;
import com.portable.server.manager.UserManager;
import com.portable.server.model.batch.Batch;
import com.portable.server.model.request.PageRequest;
import com.portable.server.model.request.batch.BatchRequest;
import com.portable.server.model.response.PageResponse;
import com.portable.server.model.response.batch.BatchResponse;
import com.portable.server.model.user.User;
import com.portable.server.service.BatchService;
import com.portable.server.type.BatchStatusType;
import com.portable.server.util.UserContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * @author shiroha
 */
@Component
public class BatchServiceImpl implements BatchService {

    @Resource
    private UserManager userManager;

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
    public PageResponse<Batch, Void> getList(PageRequest<Void> pageRequest) {
        Long userId = UserContext.ctx().getId();
        Integer batchCount = batchManager.countBatchListByOwnerId(userId);
        PageResponse<Batch, Void> response = PageResponse.of(pageRequest, batchCount);
        List<Batch> batchList = batchManager.selectBatchByPage(userId, response.getPageSize(), response.offset());
        response.setData(batchList);
        return response;
    }

    @Override
    public BatchResponse create(BatchRequest request) throws PortableException {
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
        BatchResponse batchResponse = BatchResponse.of(batch.getId());
        IntStream.rangeClosed(1, request.getCount())
                .boxed()
                .forEachOrdered(i -> {


                            User user = userManager.newBatchAccount();
                            user.setHandle(String.format(BATCH_FORMAT, request.getPrefix(), i));
                            StringBuilder stringBuilder = new StringBuilder();
                            IntStream.range(0, PASSWORD_LEN)
                                    .forEach(t -> stringBuilder.append(RANDOM.nextInt(10)));
                            user.setPassword(stringBuilder.toString());
                            batchResponse.add(user);
                            user.setPassword(BCryptEncoder.encoder(user.getPassword()));

                        }
                );
        return batchResponse;
    }

    @Override
    public void changeStatus(Long id, BatchStatusType statusType) throws PortableException {
    }
}
