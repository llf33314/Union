package com.gt.union.schedule;

import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.util.ListUtil;
import com.gt.union.log.service.IUnionLogErrorService;
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.service.IUnionMemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/27 0027.
 */
@Component
public class MemberSchedule {
    private Logger logger = LoggerFactory.getLogger(MemberSchedule.class);

//    private static final int nThread = 10;
//
//    public static AtomicInteger successCount = new AtomicInteger();
//    public static volatile AtomicInteger failCount = new AtomicInteger();

    @Autowired
    private IUnionLogErrorService unionLogErrorService;

    @Autowired
    private IUnionMemberService unionMemberService;

    /**
     * 每天00:00:00执行
     * 盟员退盟后，当退盟过渡期时间已过，需改为无效状态
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void disableExpiredMember() {
        try {
            List<UnionMember> expiredMemberList = this.unionMemberService.listExpired();
            if (ListUtil.isNotEmpty(expiredMemberList)) {
                List<UnionMember> updateMemberList = new ArrayList<>();
                for (UnionMember expiredMember : expiredMemberList) {
                    UnionMember updateMember = new UnionMember();
                    updateMember.setId(expiredMember.getId()); //对应的联盟创建记录id
                    updateMember.setDelStatus(CommonConstant.DEL_STATUS_YES); //删除状态
                    updateMemberList.add(updateMember);
                }
                this.unionMemberService.updateBatchById(updateMemberList);
            }
        } catch (Exception e) {
            logger.error("", e);
            this.unionLogErrorService.saveIfNotNull(e);
        }
    }

    /**
     * 每秒执行一次
     */
    /*@Scheduled(cron = "0/1 * * * * ?")
    public void testRedisCluster() {
        final IUnionMemberService unionMemberService_ = this.unionMemberService;
        final CountDownLatch latch = new CountDownLatch(nThread);
        final Random random = new Random();
        ExecutorService es = Executors.newFixedThreadPool(nThread);
        for (int i = 0; i < nThread; i++) {
            es.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                    }
                    int unionId;
                    for (int j = 0; j < 100; j++) {
                        unionId = 990 + random.nextInt(10);
                        try {
                            unionMemberService_.listByUnionId(unionId);
                            successCount.addAndGet(1);
                        } catch (Exception e) {
                            failCount.addAndGet(1);
                            System.out.println(e.getMessage());
                        }
                    }
                    System.out.println(DateUtil.getCurrentDateString()
                            + " " + Thread.currentThread().getName()
                            + " successCount:" + successCount.toString()
                            + " failCount:" + failCount.toString());
                }
            });
            latch.countDown();
        }
        es.shutdown();
    }*/
}
