package com.gt.union.schedule.union;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.gt.api.bean.session.BusUser;
import com.gt.union.api.amqp.entity.PhoneMessage;
import com.gt.union.api.amqp.sender.PhoneMessageSender;
import com.gt.union.api.client.user.IBusUserService;
import com.gt.union.api.client.user.bean.UserUnionAuthority;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.util.DateTimeKit;
import com.gt.union.common.util.DateUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.PropertiesUtil;
import com.gt.union.union.main.constant.UnionConstant;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.main.entity.UnionMainPackage;
import com.gt.union.union.main.entity.UnionMainPermit;
import com.gt.union.union.main.service.IUnionMainPackageService;
import com.gt.union.union.main.service.IUnionMainPermitService;
import com.gt.union.union.main.service.IUnionMainService;
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
    private IUnionMainService unionMainService;

    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionMemberOutService unionMemberOutService;

    @Autowired
    private IBusUserService busUserService;

    @Autowired
    private IUnionMainPermitService unionMainPermitService;

    @Autowired
    private PhoneMessageSender phoneMessageSender;

    @Autowired
    private IUnionMainPackageService unionMainPackageService;

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
            phoneMessageSender.sendMsg(new PhoneMessage(PropertiesUtil.getDuofenBusId(), ConfigConstant.DEVELOPER_PHONE,
                    "时间：" + DateTimeKit.format(new Date(), DateTimeKit.DEFAULT_DATETIME_FORMAT)+",执行<我的联盟>定时任务器：退盟过渡期结束后，逻辑删除盟员信息和退盟申请->出现异常(" + e.getMessage() + ")"));
        }
    }

    /**
     * 联盟过期前一天，自动更新无需付费的联盟有效期
     */
    @Scheduled(cron = "0 0 1 * * *")
    @Transactional(rollbackFor = Exception.class)
    public void autoRenewUnionValidityBeforeBusUserExpired() {
        try {
            Date currentDate = DateUtil.getCurrentDate();
            System.out.println(currentDate + " 开始执行<我的联盟>定时任务器：联盟过期前一天，自动更新无需付费的联盟有效期");
            EntityWrapper<UnionMain> entityWrapper = new EntityWrapper<>();
            entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                    .between("validity", DateUtil.addDays(currentDate, -1), currentDate);
            List<UnionMain> unionList = unionMainService.listSupport(entityWrapper);
            if (ListUtil.isNotEmpty(unionList)) {
                autoRenewUnionValidity(currentDate, unionList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("", e);
            phoneMessageSender.sendMsg(new PhoneMessage(PropertiesUtil.getDuofenBusId(), ConfigConstant.DEVELOPER_PHONE,
                    "时间：" + DateTimeKit.format(new Date(), DateTimeKit.DEFAULT_DATETIME_FORMAT)+",执行<我的联盟>定时任务器：联盟过期前一天，自动更新无需付费的联盟有效期->出现异常(" + e.getMessage() + ")"));
        }
    }

    /**
     * 商家过期后续费，自动更新无需付费的联盟有效期
     */
    @Scheduled(cron = "0 30 0/1 * * *")
    @Transactional(rollbackFor = Exception.class)
    public void autoRenewUnionValidityAfterBusUserExpired() {
        try {
            Date currentDate = DateUtil.getCurrentDate();
            System.out.println(currentDate + " 开始执行<我的联盟>定时任务器：商家过期后续费，自动更新无需付费的联盟有效期");
            EntityWrapper<UnionMain> entityWrapper = new EntityWrapper<>();
            entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                    .lt("validity", currentDate);
            List<UnionMain> unionList = unionMainService.listSupport(entityWrapper);
            if (ListUtil.isNotEmpty(unionList)) {
                autoRenewUnionValidity(currentDate, unionList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("", e);
            phoneMessageSender.sendMsg(new PhoneMessage(PropertiesUtil.getDuofenBusId(), ConfigConstant.DEVELOPER_PHONE,
                    "时间：" + DateTimeKit.format(new Date(), DateTimeKit.DEFAULT_DATETIME_FORMAT)+",执行<我的联盟>定时任务器：商家过期后续费，自动更新无需付费的联盟有效期->出现异常(" + e.getMessage() + ")"));
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void autoRenewUnionValidity(Date currentDate, List<UnionMain> unionList) throws Exception {
        if (ListUtil.isNotEmpty(unionList)) {
            for (UnionMain union : unionList) {
                Integer unionId = union.getId();

                UnionMember owner = unionMemberService.getValidOwnerByUnionId(unionId);
                Integer busId = owner.getBusId();

                BusUser busUser = busUserService.getBusUserById(busId);
                if (busUser == null || busUser.getEndTime().compareTo(union.getValidity()) < 0) {
                    continue;
                }
                UserUnionAuthority authority = busUserService.getUserUnionAuthority(busId);
                if (authority == null || !authority.getAuthority() || authority.getPay()) {
                    continue;
                }

                UnionMainPackage unionPackage = unionMainPackageService.getValidByLevel(busUser.getLevel());
                UnionMainPermit savePermit = new UnionMainPermit();
                savePermit.setDelStatus(CommonConstant.DEL_STATUS_NO);
                savePermit.setCreateTime(currentDate);
                savePermit.setBusId(busId);
                savePermit.setPackageId(unionPackage.getId());
                savePermit.setValidity(busUser.getEndTime());

                UnionMain updateUnion = new UnionMain();
                updateUnion.setId(unionId);
                updateUnion.setModifyTime(currentDate);
                updateUnion.setValidity(busUser.getEndTime());
                Integer unionMemberCount = unionMemberService.countValidReadByUnionId(unionId);
                Integer packageMemberLimit = unionPackage.getNumber();
                updateUnion.setMemberLimit(unionMemberCount > packageMemberLimit ? unionMemberCount : packageMemberLimit);

                unionMainPermitService.save(savePermit);
                unionMainService.update(updateUnion);
                UnionMainPermit removePermit = unionMainPermitService.getValidByBusIdAndOrderStatus(busId, UnionConstant.PERMIT_ORDER_STATUS_SUCCESS);
                if (removePermit != null) {
                    unionMainPermitService.removeById(removePermit.getId());
                }
            }
        }
    }
}
