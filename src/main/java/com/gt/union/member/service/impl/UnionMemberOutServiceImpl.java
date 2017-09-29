package com.gt.union.member.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.amqp.entity.PhoneMessage;
import com.gt.union.common.amqp.sender.PhoneMessageSender;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.*;
import com.gt.union.main.entity.UnionMain;
import com.gt.union.main.service.IUnionMainService;
import com.gt.union.member.constant.MemberConstant;
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.entity.UnionMemberOut;
import com.gt.union.member.mapper.UnionMemberOutMapper;
import com.gt.union.member.service.IUnionMemberOutService;
import com.gt.union.member.service.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * <p>
 * 联盟成员退盟申请 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@Service
public class UnionMemberOutServiceImpl extends ServiceImpl<UnionMemberOutMapper, UnionMemberOut> implements IUnionMemberOutService {
    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private PhoneMessageSender phoneMessageSender;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - get *********************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - list ********************************************
     ******************************************************************************************************************/

    @Override
    public Page pageApplyOutMapByBusIdAndMemberId(Page page, Integer busId, Integer memberId) throws Exception {
        if (page == null || busId == null || memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        final UnionMember unionOwner = this.unionMemberService.getByIdAndBusId(memberId, busId);
        if (unionOwner == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionMainValid(unionOwner.getUnionId());
        //(3)判断是否具有读权限
        if (!this.unionMemberService.hasReadAuthority(unionOwner)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_READ_REJECT);
        }
        //(4)判断盟主身份
        if (!unionOwner.getIsUnionOwner().equals(MemberConstant.IS_UNION_OWNER_YES)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_NEED_OWNER);
        }
        //(5)查询操作
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" mo")
                        .append(" LEFT JOIN t_union_member m ON m.id = mo.apply_member_id")
                        .append(" WHERE mo.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("  AND m.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("  AND m.status = ").append(MemberConstant.STATUS_APPLY_OUT)
                        .append("  AND m.union_id = ").append(unionOwner.getUnionId());
                return sbSqlSegment.toString();
            }
        };
        StringBuilder sbSqlSelect = new StringBuilder(" mo.id outId") //退盟申请id
                .append(", m.enterprise_name outEnterpriseName") //退盟企业名称
                .append(", mo.type outType") //退盟类型
                .append(", DATE_FORMAT(mo.createtime, '%Y-%m-%d %T') applyOutTime") //申请退盟时间
                .append(", mo.apply_out_reason applyOutReason"); //退盟理由
        wrapper.setSqlSelect(sbSqlSelect.toString());
        return this.selectMapsPage(page, wrapper);
    }

    @Override
    public Page pageOutingMapByBusIdAndMemberId(Page page, Integer busId, Integer memberId) throws Exception {
        if (page == null || busId == null || memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        final UnionMember unionOwner = this.unionMemberService.getByIdAndBusId(memberId, busId);
        if (unionOwner == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionMainValid(unionOwner.getUnionId());
        //(3)判断是否具有读权限
        if (!this.unionMemberService.hasReadAuthority(unionOwner)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_READ_REJECT);
        }
        //(4)判断盟主权限
        if (!unionOwner.getIsUnionOwner().equals(MemberConstant.IS_UNION_OWNER_YES)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_NEED_OWNER);
        }
        //(5)查询操作
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" mo")
                        .append(" LEFT JOIN t_union_member m ON m.id = mo.apply_member_id")
                        .append(" WHERE mo.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("  AND m.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("  AND m.status = ").append(MemberConstant.STATUS_OUTING)
                        .append("  AND m.union_id = ").append(unionOwner.getUnionId());
                return sbSqlSegment.toString();
            }
        };
        StringBuilder sbSqlSelect = new StringBuilder(" mo.id outId") //退盟申请id
                .append(", m.enterprise_name outEnterpriseName") //退盟企业名称
                .append(", mo.type outType") //退盟类型
                .append(", DATE_FORMAT(mo.createtime, '%Y-%m-%d %T') applyOutTime") //申请退盟时间
                .append(", DATE_FORMAT(mo.confirm_out_time, '%Y-%m-%d %T') confirmOutTime") //盟主确认退盟时间
                .append(", DATEDIFF(mo.actual_out_time, now()) remainDay") //剩余天数
                .append(", mo.apply_out_reason applyOutReason"); //退盟理由
        wrapper.setSqlSelect(sbSqlSelect.toString());
        return this.selectMapsPage(page, wrapper);
    }

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - save ********************************************
     ******************************************************************************************************************/

    @Override
    @Transactional
    public void saveApplyOutByBusIdAndMemberId(Integer busId, Integer memberId, String applyOutReason) throws Exception {
        if (busId == null || memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        UnionMember member = this.unionMemberService.getByIdAndBusId(memberId, busId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionMainValid(member.getUnionId());
        //(3)判断是否具有写权限
        if (!this.unionMemberService.hasWriteAuthority(member)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_WRITE_REJECT);
        }
        //(4)判断当前状态
        if (member.getStatus() == MemberConstant.STATUS_APPLY_OUT || member.getStatus() == MemberConstant.STATUS_OUTING) {
            throw new BusinessException("已申请退盟或正在退盟过渡期");
        }
        UnionMain unionMain = this.unionMainService.getById(member.getUnionId());
        if (unionMain == null) {
            throw new BusinessException("联盟不存在或已过期");
        }
        //(5)要保存的退盟申请信息
        UnionMemberOut saveOut = new UnionMemberOut();
        saveOut.setCreatetime(DateUtil.getCurrentDate()); //申请时间
        saveOut.setDelStatus(CommonConstant.DEL_STATUS_NO); //删除状态
        saveOut.setType(MemberConstant.OUT_TYPE_APPLY); //退盟类型
        saveOut.setApplyMemberId(memberId); //申请退盟的盟员id
        saveOut.setApplyOutReason(applyOutReason); //退盟理由
        //(6)更新盟员状态为申请退盟状态
        UnionMember updateMember = new UnionMember();
        updateMember.setId(memberId);
        updateMember.setStatus(MemberConstant.STATUS_APPLY_OUT);
        //(7)短信通知
        UnionMember unionOwner = this.unionMemberService.getOwnerByUnionId(unionMain.getId());
        if (unionOwner == null) {
            throw new BusinessException("盟主帐号不存在或已过期");
        }
        String content = new StringBuilder("\"")
                .append(member.getEnterpriseAddress())
                .append("\"申请退出\"")
                .append(unionMain.getName())
                .append("\",请到退盟审核处查看并处理").toString();
        String phone = StringUtil.isNotEmpty(unionOwner.getNotifyPhone()) ? unionOwner.getNotifyPhone() : unionOwner.getDirectorPhone();
        PhoneMessage phoneMessage = new PhoneMessage(busId, phone, content);
        //(8)事务操作
        this.save(saveOut);
        this.unionMemberService.update(updateMember);
        this.phoneMessageSender.sendMsg(phoneMessage);
    }

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - remove ******************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - update ******************************************
     ******************************************************************************************************************/

    @Override
    @Transactional
    public void updateByBusIdAndMemberIdAndOutId(Integer busId, Integer memberId, Integer outId, Integer isOK) throws Exception {
        if (busId == null || memberId == null || outId == null || isOK == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        UnionMember unionOwner = this.unionMemberService.getByIdAndBusId(memberId, busId);
        if (unionOwner == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionMainValid(unionOwner.getUnionId());
        //(3)判断是否具有写权限
        if (!this.unionMemberService.hasWriteAuthority(unionOwner)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_WRITE_REJECT);
        }
        //(4)判断盟主权限
        if (!unionOwner.getIsUnionOwner().equals(MemberConstant.IS_UNION_OWNER_YES)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_NEED_OWNER);
        }
        //(5)判断申请信息是否过期
        UnionMemberOut out = this.getById(outId);
        if (out == null) {
            throw new BusinessException("退盟申请不存在或已处理");
        }
        UnionMemberOut updateOut = new UnionMemberOut();
        UnionMember updateMember = new UnionMember();
        if (isOK == CommonConstant.COMMON_YES) { //同意退盟
            //(6)退盟申请更新内容
            updateOut.setId(outId); //退盟申请id
            updateOut.setConfirmOutTime(DateUtil.getCurrentDate()); //盟主审核退盟时间
            updateOut.setActualOutTime(DateUtil.addDays(DateUtil.getCurrentDate(), 15)); //实际退盟时间
            //(7)退盟的盟员要更新的内容
            updateMember.setId(out.getApplyMemberId()); //申请退盟的盟员id
            updateMember.setStatus(MemberConstant.STATUS_OUTING); //退盟过渡期
        } else {
            //(6)退盟申请更新内容
            updateOut.setId(outId); //退盟申请id
            updateOut.setConfirmOutTime(DateUtil.getCurrentDate()); //盟主审核退盟时间
            updateOut.setDelStatus(CommonConstant.DEL_STATUS_YES); //废弃掉这条申请
            //(7)退盟的盟员要更新的内容
            updateMember.setId(out.getApplyMemberId()); //申请退盟的盟员id
            updateMember.setStatus(MemberConstant.STATUS_IN); //返回正式盟员状态
        }
        //(8)事务化操作
        this.update(updateOut);
        this.unionMemberService.update(updateMember);
    }

    /**
     * 根据商家id、盟员身份id和目标盟员身份id，直接设置目标盟员为退盟过渡期
     *
     * @param busId       {not null} 商家id
     * @param memberId    {not null} 盟员身份id
     * @param tgtMemberId {not null} 目标盟员身份id
     * @throws Exception
     */
    @Override
    @Transactional
    public void updateByBusIdAndMemberIdAndTgtMemberId(Integer busId, Integer memberId, Integer tgtMemberId) throws Exception {
        if (busId == null || memberId == null || tgtMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        UnionMember unionOwner = this.unionMemberService.getByIdAndBusId(memberId, busId);
        if (unionOwner == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionMainValid(unionOwner.getUnionId());
        //(3)判断是否具有写权限
        if (!this.unionMemberService.hasWriteAuthority(unionOwner)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_WRITE_REJECT);
        }
        //(4)判断盟主权限
        if (!unionOwner.getIsUnionOwner().equals(MemberConstant.IS_UNION_OWNER_YES)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_NEED_OWNER);
        }
        //(5)检查操作的对象信息
        UnionMember tgtMember = this.unionMemberService.getById(tgtMemberId);
        if (tgtMember == null) {
            throw new BusinessException("要移出的对象不存在");
        }
        if (!tgtMember.getUnionId().equals(unionOwner.getUnionId())) {
            throw new BusinessException("要移出的对象不在该联盟下");
        }
        //(6)更新操作
        Integer tgtMemberStatus = tgtMember.getStatus();
        switch (tgtMemberStatus) {
            case MemberConstant.STATUS_OUTING:
                throw new BusinessException("要移出的对象正处于退盟过渡期");
            case MemberConstant.STATUS_APPLY_OUT: //已申请退盟，移出操作变成审核通过退盟操作
                List<UnionMemberOut> tgtOutList = this.listByApplyMemberId(tgtMemberId);
                if (ListUtil.isEmpty(tgtOutList)) {
                    throw new BusinessException("要移出的对象已申请退盟，但找不到退盟申请信息");
                }
                this.updateByBusIdAndMemberIdAndOutId(busId, memberId, tgtOutList.get(0).getId(), CommonConstant.COMMON_YES);
                break;
            case MemberConstant.STATUS_APPLY_IN: //目标对象直接设置为退盟过渡期
                //目标对象的伪退盟申请
                UnionMemberOut saveMemberOut = new UnionMemberOut();
                Date currentDate = DateUtil.getCurrentDate();
                saveMemberOut.setCreatetime(currentDate); //创建时间
                saveMemberOut.setDelStatus(CommonConstant.DEL_STATUS_NO); //删除状态
                saveMemberOut.setType(MemberConstant.OUT_TYPE_REMOVE); //退盟类型
                saveMemberOut.setApplyMemberId(tgtMemberId); //退盟盟员id
                saveMemberOut.setApplyOutReason("盟主移出"); //退盟理由
                saveMemberOut.setConfirmOutTime(currentDate); //盟主审核确认时间
                saveMemberOut.setActualOutTime(DateUtil.addDays(currentDate, 15)); //实际退盟时间
                //目标对象的更新状态
                UnionMember updateMember = new UnionMember();
                updateMember.setId(tgtMemberId); //目标盟员id
                updateMember.setStatus(MemberConstant.STATUS_OUTING); //目标盟员状态为退盟过渡期
                //事务化操作
                this.insert(saveMemberOut);
                this.unionMemberService.update(updateMember);
        }
    }

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - count *******************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - boolean *****************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Object As a Service - get **********************************************
     ******************************************************************************************************************/

    @Override
    public UnionMemberOut getById(Integer outId) throws Exception {
        if (outId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMemberOut result;
        //(1)cache
        String outIdKey = RedisKeyUtil.getMemberOutIdKey(outId);
        if (this.redisCacheUtil.exists(outIdKey)) {
            result = (UnionMemberOut) this.redisCacheUtil.get(outIdKey);
            return result;
        }
        //(2)db
        EntityWrapper<UnionMemberOut> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", outId)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = this.selectOne(entityWrapper);
        setCache(result, outId);
        return result;
    }

    /*******************************************************************************************************************
     ****************************************** Object As a Service - list *********************************************
     ******************************************************************************************************************/

    @Override
    public List<UnionMemberOut> listByApplyMemberId(Integer applyMemberId) throws Exception {
        if (applyMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMemberOut> result;
        //(1)get in cache
        String applyMemberIdKey = RedisKeyUtil.getMemberOutApplyMemberIdKey(applyMemberId);
        if (this.redisCacheUtil.exists(applyMemberIdKey)) {
            result = (List<UnionMemberOut>) this.redisCacheUtil.get(applyMemberIdKey);
            return result;
        }
        //(2)get in db
        EntityWrapper<UnionMemberOut> entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("apply_member_id", applyMemberId);
        result = this.selectList(entityWrapper);
        setCache(result, applyMemberId, MemberConstant.REDIS_KEY_OUT_APPLY_MEMBER_ID);
        return result;
    }

    /*******************************************************************************************************************
     ****************************************** Object As a Service - save *********************************************
     ******************************************************************************************************************/

    @Override
    @Transactional
    public void save(UnionMemberOut newOut) throws Exception {
        if (newOut == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        this.insert(newOut);
        this.removeCache(newOut);
    }

    @Override
    @Transactional
    public void saveBatch(List<UnionMemberOut> newOutList) throws Exception {
        if (newOutList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        this.insertBatch(newOutList);
        this.removeCache(newOutList);
    }

    /*******************************************************************************************************************
     ****************************************** Object As a Service - remove *******************************************
     ******************************************************************************************************************/

    @Override
    @Transactional
    public void removeById(Integer outId) throws Exception {
        if (outId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        UnionMemberOut out = this.getById(outId);
        removeCache(out);
        //(2)remove in db logically
        UnionMemberOut removeOut = new UnionMemberOut();
        removeOut.setId(outId);
        removeOut.setDelStatus(CommonConstant.DEL_STATUS_YES);
        this.updateById(removeOut);
    }

    @Override
    @Transactional
    public void removeBatchById(List<Integer> outIdList) throws Exception {
        if (outIdList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        List<UnionMemberOut> outList = new ArrayList<>();
        for (Integer outId : outIdList) {
            UnionMemberOut out = this.getById(outId);
            outList.add(out);
        }
        removeCache(outList);
        //(2)remove in db logically
        List<UnionMemberOut> removeOutList = new ArrayList<>();
        for (Integer outId : outIdList) {
            UnionMemberOut removeOut = new UnionMemberOut();
            removeOut.setId(outId);
            removeOut.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeOutList.add(removeOut);
        }
        this.updateBatchById(removeOutList);
    }

    /*******************************************************************************************************************
     ****************************************** Object As a Service - update *******************************************
     ******************************************************************************************************************/

    @Override
    @Transactional
    public void update(UnionMemberOut updateOut) throws Exception {
        if (updateOut == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        Integer outId = updateOut.getId();
        UnionMemberOut out = this.getById(outId);
        removeCache(out);
        //(2)update db
        this.updateById(updateOut);
    }

    @Override
    @Transactional
    public void updateBatch(List<UnionMemberOut> updateOutList) throws Exception {
        if (updateOutList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        List<Integer> outIdList = new ArrayList<>();
        for (UnionMemberOut updateOut : updateOutList) {
            outIdList.add(updateOut.getId());
        }
        List<UnionMemberOut> outList = new ArrayList<>();
        for (Integer outId : outIdList) {
            UnionMemberOut out = this.getById(outId);
            outList.add(out);
        }
        removeCache(outList);
        //(2)update db
        this.updateBatchById(updateOutList);
    }

    /*******************************************************************************************************************
     ****************************************** Object As a Service - cache support ************************************
     ******************************************************************************************************************/

    private void setCache(UnionMemberOut newOut, Integer outId) {
        if (outId == null) {
            return; //do nothing,just in case
        }
        String outIdKey = RedisKeyUtil.getMemberOutIdKey(outId);
        this.redisCacheUtil.set(outIdKey, newOut);
    }

    private void setCache(List<UnionMemberOut> newOutList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            return; //do nothing,just in case
        }
        String foreignIdKey;
        switch (foreignIdType) {
            case MemberConstant.REDIS_KEY_OUT_APPLY_MEMBER_ID:
                foreignIdKey = RedisKeyUtil.getMemberOutApplyMemberIdKey(foreignId);
                this.redisCacheUtil.set(foreignIdKey, newOutList);
                break;
            default:
                break;
        }
    }

    private void removeCache(UnionMemberOut out) {
        if (out == null) {
            return;
        }
        Integer outId = out.getId();
        String outIdKey = RedisKeyUtil.getMemberOutIdKey(outId);
        this.redisCacheUtil.remove(outIdKey);
        Integer applyMemberId = out.getApplyMemberId();
        if (applyMemberId != null) {
            String applyMemberIdKey = RedisKeyUtil.getMemberOutApplyMemberIdKey(applyMemberId);
            this.redisCacheUtil.remove(applyMemberIdKey);
        }
    }

    private void removeCache(List<UnionMemberOut> outList) {
        if (ListUtil.isEmpty(outList)) {
            return;
        }
        List<Integer> outIdList = new ArrayList<>();
        for (UnionMemberOut out : outList) {
            outIdList.add(out.getId());
        }
        List<String> outIdKeyList = RedisKeyUtil.getMemberOutIdKey(outIdList);
        this.redisCacheUtil.remove(outIdKeyList);
        List<String> applyMemberIdKeyList = getForeignIdKeyList(outList, MemberConstant.REDIS_KEY_OUT_APPLY_MEMBER_ID);
        if (ListUtil.isNotEmpty(applyMemberIdKeyList)) {
            this.redisCacheUtil.remove(applyMemberIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionMemberOut> outList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case MemberConstant.REDIS_KEY_OUT_APPLY_MEMBER_ID:
                for (UnionMemberOut out : outList) {
                    Integer applyMemberId = out.getApplyMemberId();
                    if (applyMemberId != null) {
                        String applyMemberIdKey = RedisKeyUtil.getMemberOutApplyMemberIdKey(applyMemberId);
                        result.add(applyMemberIdKey);
                    }
                }
                break;
            default:
                break;
        }
        return result;
    }
}
