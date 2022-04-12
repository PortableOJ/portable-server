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
import com.portable.server.type.SolutionStatusType;
import com.portable.server.type.SolutionType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
    private static final String NOFREEZE_RANK_HASH_PREFIX = "NOFREEZE_RANK_USER_HASH";
    private static final String NOFREEZE_RANK_LIST_PREFIX = "NOFREEZE_RANK_LIST";

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
        // entrySet 完全没有拷贝，所以这里替换的方式可以保证后续的数据仍然能够正确保存至此
        Set<Map.Entry<Long, Date>> cachedRankList = CACHED_RANK.entrySet();
        CACHED_RANK = new ConcurrentHashMap<>(CACHED_RANK.size());
        cachedRankList.stream()
                .sequential()
                .forEach(longDateEntry -> {
                    // 超过了更新时间，则清理缓存
                    if (longDateEntry.getValue().before(now)) {
                        redisListKit.clear(RANK_LIST_PREFIX, longDateEntry.getKey());
                        redisListKit.clear(NOFREEZE_RANK_LIST_PREFIX, longDateEntry.getKey());
                        redisHashKit.clear(RANK_HASH_PREFIX, longDateEntry.getKey());
                        redisHashKit.clear(NOFREEZE_RANK_HASH_PREFIX, longDateEntry.getKey());
                        return;
                    }
                    try {
                        makeRank(longDateEntry.getKey());
                        CACHED_RANK.put(longDateEntry.getKey(), longDateEntry.getValue());
                    } catch (PortableException ignore) {
                    }
                });
    }

    @Override
    public void addTraceRank(Long contestId) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, RANK_CACHE_TIME);
        CACHED_RANK.put(contestId, calendar.getTime());
    }

    @Override
    public void ensureRank(Long contestId) throws PortableException {
        if (CACHED_RANK.containsKey(contestId)) {
            addTraceRank(contestId);
            return;
        }
        boolean needMake = false;
        try {
            synchronized (ON_MAKING_CONTEST) {
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
                addTraceRank(contestId);
                synchronized (MAX_RANK_MAKER) {
                    RANK_MAKER--;
                    MAX_RANK_MAKER.notifyAll();
                }
                synchronized (ON_MAKING_CONTEST) {
                    ON_MAKING_CONTEST.remove(contestId);
                    ON_MAKING_CONTEST.notifyAll();
                }
            }
        } catch (InterruptedException ignore) {
            throw PortableException.of("S-00-000");
        }
    }

    @Override
    public Integer getContestRankLen(Long contestId, Boolean freeze) {
        return redisListKit.getLen(freeze ? RANK_LIST_PREFIX : NOFREEZE_RANK_LIST_PREFIX, contestId);
    }

    @Override
    public List<ContestRankItem> getContestRank(Long contestId, Integer pageSize, Integer offset, Boolean freeze) {
        return redisListKit.getPage(freeze ? RANK_LIST_PREFIX : NOFREEZE_RANK_LIST_PREFIX, contestId, pageSize, offset, ContestRankItem.class);
    }

    @Override
    public ContestRankItem getContestByUserId(Long contestId, Long userId, Boolean freeze) {
        Optional<Integer> rank = redisHashKit.get(freeze ? RANK_HASH_PREFIX : NOFREEZE_RANK_HASH_PREFIX, contestId, userId);
        Optional<ContestRankItem> optionalContestRankItem = rank.flatMap(
                integer -> redisListKit.get(freeze ? RANK_LIST_PREFIX : NOFREEZE_RANK_LIST_PREFIX, contestId, integer, ContestRankItem.class)
        );
        return optionalContestRankItem.orElse(null);
    }

    private void makeRank(Long contestId) throws PortableException {
        Contest contest = contestManager.getContestById(contestId)
                .orElseThrow(PortableException.from("A-08-002", contestId));
        BaseContestData contestData = contestDataManager.getBaseContestDataById(contest.getDataId(), contest.getAccessType());
        if (contestData == null) {
            throw PortableException.of("S-07-002");
        }
        // 删除之前记录的通过数量
        contestData.getProblemList().forEach(BaseContestData.ContestProblemData::init);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(contest.getStartTime());
        calendar.add(Calendar.MINUTE, contest.getDuration() - contestData.getFreezeTime());
        Date freezeTime = calendar.getTime();

        Map<Long, Integer> problemIndexMap = contestData.idToIndex();
        // 通过分页获取，减轻 io 负担
        Integer totalSolution = solutionManager.countSolution(SolutionType.CONTEST, null, contestId, null, null);
        // + 1 导致多取出一页空白并不影响使用，同时可以尽量避免并发导致的少了部分提交的问题，此处计算页数不需要精确
        int totalPageNum = totalSolution / MAKE_RANK_PAGE_SIZE + 1;
        Map<Long, ContestRankItem> userIdContestRankMap = new ConcurrentHashMap<>(128);
        for (int i = 0; i < totalPageNum; i++) {
            Integer offset = i * MAKE_RANK_PAGE_SIZE;
            List<Solution> solutionList = solutionManager.selectSolutionByPage(MAKE_RANK_PAGE_SIZE, offset,
                    SolutionType.CONTEST, null, contestId, null, null);
            // 先并行创建不存在的参加者
            solutionList.stream()
                    .parallel()
                    .peek(solution -> {
                        if (!SolutionStatusType.ACCEPT.equals(solution.getStatus())) {
                            return;
                        }
                        Integer problemIndex = problemIndexMap.get(solution.getProblemId());
                        contestData.getProblemList().get(problemIndex).addAccept();
                    })
                    .unordered()
                    .map(Solution::getUserId)
                    .distinct()
                    .filter(aLong -> !userIdContestRankMap.containsKey(aLong))
                    .forEach(aLong -> userIdContestRankMap.put(aLong, ContestRankItem.builder()
                            .rank(0)
                            .userId(aLong)
                            .totalCost(0L)
                            .totalSolve(0)
                            .submitStatus(new ConcurrentHashMap<>(0))
                            .noFreezeSubmitStatus(new ConcurrentHashMap<>(0))
                            .build()));
            solutionList.forEach(solution -> {
                ContestRankItem contestRankItem = userIdContestRankMap.get(solution.getUserId());
                contestRankItem.addSolution(solution,
                        problemIndexMap.get(solution.getProblemId()),
                        contest.getStartTime(),
                        freezeTime
                );
            });
        }
        contestDataManager.saveContestData(contestData);

        List<ContestRankItem> contestRankItemList = userIdContestRankMap.values().stream()
                .parallel()
                .peek(contestRankItem -> contestRankItem.calCost(contestData.getPenaltyTime(), true))
                .sorted()
                .collect(Collectors.toList());
        saveRank(RANK_HASH_PREFIX, RANK_LIST_PREFIX, contestId, contestRankItemList);

        // 可能存在封榜数据的时候，则生成不封榜的数据，否则不生成
        if (!Integer.valueOf(0).equals(contestData.getFreezeTime())) {
            contestRankItemList = userIdContestRankMap.values().stream()
                    .parallel()
                    .peek(contestRankItem -> contestRankItem.calCost(contestData.getPenaltyTime(), false))
                    .sorted()
                    .collect(Collectors.toList());
            saveRank(NOFREEZE_RANK_HASH_PREFIX, NOFREEZE_RANK_LIST_PREFIX, contestId, contestRankItemList);
        }
    }

    private void saveRank(String hashPre, String listPre, Long contestId, List<ContestRankItem> contestRankItemList) {
        Map<Long, Integer> userRankMap = IntStream.range(0, contestRankItemList.size())
                .parallel()
                .boxed()
                .map(i -> {
                    ContestRankItem contestRankItem = contestRankItemList.get(i);
                    contestRankItem.setRank(i);
                    return contestRankItem;
                })
                .collect(Collectors.toMap(ContestRankItem::getUserId, ContestRankItem::getRank));
        redisHashKit.clear(hashPre, contestId);
        redisHashKit.create(hashPre, contestId, userRankMap);
        redisListKit.clear(listPre, contestId);
        redisListKit.create(listPre, contestId, contestRankItemList);
    }
}
