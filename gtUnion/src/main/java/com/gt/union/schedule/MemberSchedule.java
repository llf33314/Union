package com.gt.union.schedule;

import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.util.ListUtil;
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
 * 定时任务器 member模块
 *
 * @author linweicong
 * @version 2017-10-23 14:51:10
 */
@Component
public class MemberSchedule {
    private Logger logger = LoggerFactory.getLogger(MemberSchedule.class);

    @Autowired
    private IUnionMemberService unionMemberService;

    /**
     * 每天00:00:00执行
     * 盟员退盟后，当退盟过渡期时间已过，需改为无效状态
     */
//    @Scheduled(cron = "0 0 0 * * ?")
    public void disableExpiredMember() {
        try {
            List<UnionMember> expiredMemberList = this.unionMemberService.listExpired();
            if (ListUtil.isNotEmpty(expiredMemberList)) {
                List<UnionMember> updateMemberList = new ArrayList<>();
                for (UnionMember expiredMember : expiredMemberList) {
                    UnionMember updateMember = new UnionMember();
                    //对应的联盟创建记录id
                    updateMember.setId(expiredMember.getId());
                    //删除状态
                    updateMember.setDelStatus(CommonConstant.DEL_STATUS_YES);
                    updateMemberList.add(updateMember);
                }
                this.unionMemberService.updateBatchById(updateMemberList);
            }
        } catch (Exception e) {
            logger.error("", e);
        }
    }

}
