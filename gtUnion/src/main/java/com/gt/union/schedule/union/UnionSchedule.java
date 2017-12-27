package com.gt.union.schedule.union;

import com.gt.union.common.util.DateUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.union.member.constant.MemberConstant;
import com.gt.union.union.member.entity.UnionMember;
import com.gt.union.union.member.entity.UnionMemberOut;
import com.gt.union.union.member.service.IUnionMemberOutService;
import com.gt.union.union.member.service.IUnionMemberService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 我的联盟相关定时任务器
 *
 * @author linweicong
 * @version 2017-12-23 14:14:59
 */
@Component
public class UnionSchedule {
    private Logger logger = Logger.getLogger(UnionSchedule.class);

    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionMemberOutService unionMemberOutService;

    /**
     * 退盟过渡期结束后，逻辑删除盟员信息和退盟申请
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional(rollbackFor = Exception.class)
    public void removeMemberAfterPeriod() {
        try {
            Date currentDate = DateUtil.getCurrentDate();
            System.out.println(currentDate + " 开始执行<我的联盟>定时任务器：退盟过渡期结束后，逻辑删除盟员信息和退盟申请");
            List<UnionMember> periodMemberList = unionMemberService.listValidByStatus(MemberConstant.STATUS_OUT_PERIOD);
            if (ListUtil.isNotEmpty(periodMemberList)) {
                for (UnionMember periodMember : periodMemberList) {
                    UnionMemberOut out = unionMemberOutService.getValidByUnionIdAndApplyMemberId(periodMember.getUnionId(), periodMember.getId());
                    if (out == null) {
                        continue;
                    }
                    if (currentDate.compareTo(out.getActualOutTime()) > 0) {
                        unionMemberService.removeById(periodMember.getId());
                        unionMemberOutService.removeById(out.getId());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("", e);
        }
    }
}
