package com.portable.server.support.impl;

import com.portable.server.exception.PortableException;
import com.portable.server.kit.RedisHashKit;
import com.portable.server.kit.RedisListKit;
import com.portable.server.manager.ContestDataManager;
import com.portable.server.manager.ContestManager;
import com.portable.server.manager.SolutionManager;
import com.portable.server.model.contest.BaseContestData;
import com.portable.server.model.contest.Contest;
import com.portable.server.model.contest.ContestRankItem;
import com.portable.server.model.solution.Solution;
import com.portable.server.support.ContestSupport;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author shiroha
 */
@Component
public class ContestSupportImpl implements ContestSupport {

    @Resource
    private RedisHashKit redisHashKit;

    @Resource
    private RedisListKit redisListKit;

    @Resource
    private SolutionManager solutionManager;

    @Resource
    private ContestManager contestManager;

    @Resource
    private ContestDataManager contestDataManager;

    /**
     * 分页获取提交时，每页的数量
     */
    private static final Integer MAKE_RANK_PAGE_SIZE = 128;

    /**
     * rank 的缓存时间(分钟)
     */
    private static final Integer RANK_CACHE_TIME = 1440;

    /**
     * rank 的缓存的 key
     */
    private static final String RANK_HASH_PREFIX = "RANK_USER_HASH";
    private static final String RANK_LIST_PREFIX = "RANK_LIST";

    /**
     * 同时能够创建的比赛榜单数量
     */
    private static final Integer MAX_RANK_MAKER = 3;

    /**
     * 以及缓存了榜单的 id
     */
    private static Map<Long, Date> CACHED_RANK;

    /**
     * 正在创建 rank 的比赛 id
     */
    private static final Set<Long> ON_MAKING_CONTEST;

    /**
     * 正在创建 rank 的数量
     */
    private static Integer RANK_MAKER = 0;

    static {
        CACHED_RANK = new ConcurrentHashMap<>();
        ON_MAKING_CONTEST = new ConcurrentSkipListSet<>();
    }

    @Scheduled(fixedDelayString = "${portable.contest.rank.update}")
    public void updateRank() {
        Date now = new Date();
        CACHED_RANK = CACHED_RANK.entrySet().stream()
                .sequential()
                .map(longDateEntry -> {
                    if (longDateEntry.getValue().before(now)) {
                        redisListKit.clear(RANK_LIST_PREFIX, longDateEntry.getKey());
                        redisHashKit.clear(RANK_HASH_PREFIX, longDateEntry.getKey());
                    }
                    try {
                        makeRank(longDateEntry.getKey());
                        return longDateEntry;
                    } catch (PortableException e) {
                        return null;
                    }
                })
                .filter(longDateEntry -> !Objects.isNull(longDateEntry))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public void addTraceRank(Long contestId) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, RANK_CACHE_TIME);
        CACHED_RANK.put(contestId, calendar.getTime());
    }

    @Override
    public List<ContestRankItem> getContestRank(Long contestId, Integer pageSize, Integer offset) throws PortableException {
        if (CACHED_RANK.containsKey(contestId)) {
            return redisListKit.getPage(RANK_LIST_PREFIX, contestId, pageSize, offset, ContestRankItem.class);
        }
        boolean needMake = false;
        try {
            synchronized (this) {
                if (ON_MAKING_CONTEST.contains(contestId)) {
                    do {
                        this.wait();
                    } while (ON_MAKING_CONTEST.contains(contestId));
                } else {
                    needMake = true;
                    ON_MAKING_CONTEST.add(contestId);
                }
            }
            if (needMake) {
                synchronized (MAX_RANK_MAKER) {
                    while (RANK_MAKER >= MAX_RANK_MAKER) {
                        this.wait();
                    }
                    RANK_MAKER++;
                }
                makeRank(contestId);
                this.notifyAll();
                MAX_RANK_MAKER.notifyAll();
            }
        } catch (InterruptedException ignore) {
            throw PortableException.of("S-00-000");
        }
        return redisListKit.getPage(RANK_LIST_PREFIX, contestId, pageSize, offset, ContestRankItem.class);
    }

    @Override
    public ContestRankItem getContestByUserId(Long contestId, Long userId) {
        Optional<Integer> rank = redisHashKit.get(RANK_HASH_PREFIX, contestId, userId);
        Optional<ContestRankItem> optionalContestRankItem = rank.flatMap(integer -> redisListKit.get(RANK_LIST_PREFIX, contestId, integer, ContestRankItem.class));
        return optionalContestRankItem.orElse(null);
    }

    private void makeRank(Long contestId) throws PortableException {
        Contest contest = contestManager.getContestById(contestId);
        if (contest == null) {
            throw PortableException.of("A-08-002", contestId);
        }
        BaseContestData contestData = contestDataManager.getBaseContestDataById(contest.getDataId(), contest.getAccessType());
        if (contestData == null) {
            throw PortableException.of("S-07-002", contestId);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(contest.getStartTime());
        calendar.add(Calendar.MINUTE, contest.getDuration() - contestData.getFreezeTime());
        Date freezeTime = calendar.getTime();

        Map<Long, Integer> problemIndexMap = IntStream.range(0, contestData.getProblemList().size())
                .parallel()
                .boxed()
                .collect(Collectors.toMap(i -> contestData.getProblemList().get(i).getProblemId(), i -> i));

        // 通过分页获取，减轻 io 负担
        Integer totalSolution = solutionManager.countSolutionByContest(contestId);
        // + 1 导致多取出一页空白并不影响使用，同时可以尽量避免并发导致的少了部分提交的问题，此处计算页数不需要精确
        int totalPageNum = totalSolution / MAKE_RANK_PAGE_SIZE + 1;
        Map<Long, ContestRankItem> userIdContestRankMap = new ConcurrentHashMap<>(128);
        for (int i = 0; i < totalPageNum; i++) {
            Integer offset = i * MAKE_RANK_PAGE_SIZE;
            List<Solution> solutionList = solutionManager.selectSolutionByContestAndPage(MAKE_RANK_PAGE_SIZE, offset, contestId);
            // 先并行创建不存在的参加者
            solutionList.stream()
                    .parallel()
                    .unordered()
                    .map(Solution::getUserId)
                    .distinct()
                    .filter(userIdContestRankMap::containsKey)
                    .forEach(aLong -> userIdContestRankMap.put(aLong, ContestRankItem.builder()
                            .rank(0)
                            .userId(aLong)
                            .totalCost(0L)
                            .totalSolve(0)
                            .submitStatus(new ConcurrentHashMap<>(0))
                            .build()));
            solutionList.stream()
                    .parallel()
                    .forEach(solution -> {
                        ContestRankItem contestRankItem = userIdContestRankMap.get(solution.getUserId());
                        contestRankItem.addSolution(solution,
                                problemIndexMap.get(solution.getProblemId()),
                                contest.getStartTime(),
                                freezeTime,
                                contestData.getPenaltyTime());
                    });
        }
        List<ContestRankItem> contestRankItemList = userIdContestRankMap.values().stream()
                .parallel()
                .sorted()
                .collect(Collectors.toList());
        saveRank(contestId, contestRankItemList);
    }

    private void saveRank(Long contestId, List<ContestRankItem> contestRankItemList) {
        Map<Long, Integer> userRankMap = IntStream.range(0, contestRankItemList.size())
                .parallel()
                .boxed()
                .map(i -> {
                    ContestRankItem contestRankItem = contestRankItemList.get(i);
                    contestRankItem.setRank(i);
                    return contestRankItem;
                })
                .collect(Collectors.toMap(ContestRankItem::getUserId, ContestRankItem::getRank));
        redisHashKit.create(RANK_HASH_PREFIX, contestId, userRankMap);
        redisListKit.create(RANK_LIST_PREFIX, contestId, contestRankItemList);
    }
}
