package com.gt.union.schedule;

import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.util.DateUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.log.service.IUnionLogErrorService;
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.service.IUnionMemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/9/27 0027.
 */
@Component
public class MemberSchedule {
    private Logger logger = LoggerFactory.getLogger(MemberSchedule.class);

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
     * 每2秒执行一次
     */
    @Scheduled(cron = "0/2 * * * * ?")
    public void testRedisCluster() {
        final IUnionMemberService unionMemberService_ = this.unionMemberService;
        ExecutorService es = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            es.submit(new Runnable() {
                @Override
                public void run() {
                    Random random = new Random();
                    int unionId = 990 + random.nextInt(10);
                    try {
                        unionMemberService_.listByUnionId(unionId);
                        System.out.println(DateUtil.getCurrentDateString() + " - " + Thread.currentThread().getId() + " - testRedisCluster()");
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            });
        }

    }
}
