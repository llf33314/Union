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
import com.gt.union.common.util.DateUtil;
import com.gt.union.common.util.StringUtil;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;


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

    //-------------------------------------------------- get ----------------------------------------------------------

    /**
     * 根据退盟申请id，获取退盟申请信息
     *
     * @param outId {not null} 退盟申请id
     * @return
     * @throws Exception
     */
    @Override
    public UnionMemberOut getById(Integer outId) throws Exception {
        if (outId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("id", outId);
        return this.selectOne(entityWrapper);
    }

    /**
     * 根据退盟申请的盟员身份id，获取退盟申请信息
     *
     * @param applyMemberId {not null} 退盟申请id
     * @return
     * @throws Exception
     */
    @Override
    public UnionMemberOut getByApplyMemberId(Integer applyMemberId) throws Exception {
        if (applyMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("apply_member_id", applyMemberId);
        return this.selectOne(entityWrapper);
    }

    //------------------------------------------ list(include page) ---------------------------------------------------

    /**
     * 根据商家id和盟员身份id，分页获取申请退盟列表信息
     *
     * @param page     {not null} 分页对象
     * @param busId    {not null} 商家id
     * @param memberId {not null} 盟员身份id
     * @return
     * @throws Exception
     */
    @Override
    public Page pageApplyOutMapByBusIdAndMemberId(Page page, Integer busId, Integer memberId) throws Exception {
        if (page == null || busId == null || memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        final UnionMember unionMember = this.unionMemberService.getByIdAndBusId(memberId, busId);
        if (unionMember == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionMainValid(unionMember.getUnionId());
        //(3)判断是否具有读权限
        if (!this.unionMemberService.hasReadAuthority(unionMember)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_READ_REJECT);
        }
        //(4)判断盟主身份
        if (!unionMember.getIsUnionOwner().equals(MemberConstant.IS_UNION_OWNER_YES)) {
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
                        .append("  AND m.union_id = ").append(unionMember.getUnionId());
                return sbSqlSegment.toString();
            }
        };
        StringBuilder sbSqlSelect = new StringBuilder(" mo.id outId") //退盟申请id
                .append(", m.enterprise_name outEnterpriseName") //退盟企业名称
                .append(", DATE_FORMAT(mo.createtime, '%Y-%m-%d %T') applyOutTime") //申请退盟时间
                .append(", mo.apply_out_reason applyOutReason"); //退盟理由
        wrapper.setSqlSelect(sbSqlSelect.toString());
        return this.selectMapsPage(page, wrapper);
    }

    /**
     * 根据商家id和盟员身份id，分页获取退盟过渡期列表信息
     *
     * @param page     {not null} 分页对象
     * @param busId    {not null} 商家id
     * @param memberId {not null} 盟员身份id
     * @return
     * @throws Exception
     */
    @Override
    public Page pageOutingMapByBusIdAndMemberId(Page page, Integer busId, Integer memberId) throws Exception {
        if (page == null || busId == null || memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        final UnionMember unionMember = this.unionMemberService.getByIdAndBusId(memberId, busId);
        if (unionMember == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionMainValid(unionMember.getUnionId());
        //(3)判断是否具有读权限
        if (!this.unionMemberService.hasReadAuthority(unionMember)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_READ_REJECT);
        }
        //(4)判断盟主权限
        if (!unionMember.getIsUnionOwner().equals(MemberConstant.IS_UNION_OWNER_YES)) {
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
                        .append("  AND m.union_id = ").append(unionMember.getUnionId());
                return sbSqlSegment.toString();
            }
        };
        StringBuilder sbSqlSelect = new StringBuilder(" mo.id outId") //退盟申请id
                .append(", m.enterprise_name outEnterpriseName") //退盟企业名称
                .append(", DATE_FORMAT(mo.createtime, '%Y-%m-%d %T') applyOutTime") //申请退盟时间
                .append(", DATE_FORMAT(mo.confirm_out_time, '%Y-%m-%d %T') confirmOutTime") //盟主确认退盟时间
                .append(", DATEDIFF(mo.actual_out_time, now()) remainDay") //剩余天数
                .append(", mo.apply_out_reason applyOutReason"); //退盟理由
        wrapper.setSqlSelect(sbSqlSelect.toString());
        return this.selectMapsPage(page, wrapper);
    }

    //------------------------------------------------- update --------------------------------------------------------

    /**
     * 根据商家id、盟员身份id和退盟申请id，审批退盟申请
     *
     * @param busId    {not null} 商家id
     * @param memberId {not null} 盟员身份id
     * @param outId    {not null} 退盟申请id
     * @param isOK     是否允许退盟，1为是， 0为否
     * @throws Exception
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateByBusIdAndMemberIdAndOutId(Integer busId, Integer memberId, Integer outId, Integer isOK) throws Exception {
        if (busId == null || memberId == null || outId == null || isOK == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        UnionMember unionMember = this.unionMemberService.getByIdAndBusId(memberId, busId);
        if (unionMember == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionMainValid(unionMember.getUnionId());
        //(3)判断是否具有写权限
        if (!this.unionMemberService.hasWriteAuthority(unionMember)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_WRITE_REJECT);
        }
        //(4)判断盟主权限
        if (!unionMember.getIsUnionOwner().equals(MemberConstant.IS_UNION_OWNER_YES)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_NEED_OWNER);
        }
        //(5)判断申请信息是否过期
        UnionMemberOut unionMemberOut = this.getById(outId);
        if (unionMemberOut == null) {
            throw new BusinessException("退盟申请不存在或已处理");
        }
        UnionMemberOut updateUnionMemberOut = new UnionMemberOut();
        UnionMember updateUnionMember = new UnionMember();
        if (isOK == CommonConstant.COMMON_YES) { //同意退盟
            //(6)退盟申请更新内容
            updateUnionMemberOut.setId(outId); //退盟申请id
            updateUnionMemberOut.setConfirmOutTime(DateUtil.getCurrentDate()); //盟主审核退盟时间
            updateUnionMemberOut.setActualOutTime(DateUtil.addDays(DateUtil.getCurrentDate(), 15)); //实际退盟时间
            //(7)退盟的盟员要更新的内容
            updateUnionMember.setId(unionMemberOut.getApplyMemberId()); //申请退盟的盟员id
            updateUnionMember.setStatus(MemberConstant.STATUS_OUTING); //退盟过渡期
        } else {
            //(6)退盟申请更新内容
            updateUnionMemberOut.setId(outId); //退盟申请id
            updateUnionMemberOut.setConfirmOutTime(DateUtil.getCurrentDate()); //盟主审核退盟时间
            updateUnionMemberOut.setDelStatus(CommonConstant.DEL_STATUS_YES); //废弃掉这条申请
            //(7)退盟的盟员要更新的内容
            updateUnionMember.setId(unionMemberOut.getApplyMemberId()); //申请退盟的盟员id
            updateUnionMember.setStatus(MemberConstant.STATUS_IN); //返回正式盟员状态
        }
        //(8)事务化操作
        this.updateById(updateUnionMemberOut);
        this.unionMemberService.updateById(updateUnionMember);
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
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateByBusIdAndMemberIdAndTgtMemberId(Integer busId, Integer memberId, Integer tgtMemberId) throws Exception {
        if (busId == null || memberId == null || tgtMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        UnionMember unionMember = this.unionMemberService.getByIdAndBusId(memberId, busId);
        if (unionMember == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionMainValid(unionMember.getUnionId());
        //(3)判断是否具有写权限
        if (!this.unionMemberService.hasWriteAuthority(unionMember)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_WRITE_REJECT);
        }
        //(4)判断盟主权限
        if (!unionMember.getIsUnionOwner().equals(MemberConstant.IS_UNION_OWNER_YES)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_NEED_OWNER);
        }
        //(5)检查操作的对象信息
        UnionMember tgtUnionMember = this.unionMemberService.getById(tgtMemberId);
        if (tgtUnionMember == null) {
            throw new BusinessException("要移出的对象不存在");
        }
        if (!tgtUnionMember.getUnionId().equals(unionMember.getUnionId())) {
            throw new BusinessException("要移出的对象不在该联盟下");
        }
        //(6)更新操作
        Integer tgtMemberStatus = tgtUnionMember.getStatus();
        switch (tgtMemberStatus) {
            case MemberConstant.STATUS_OUTING:
                throw new BusinessException("要移出的对象正处于退盟过渡期");
            case MemberConstant.STATUS_APPLY_OUT: //已申请退盟，移出操作变成审核通过退盟操作
                UnionMemberOut tgtUnionMemberOut = this.getByApplyMemberId(tgtMemberId);
                if (tgtUnionMemberOut == null) {
                    throw new BusinessException("要移出的对象已申请退盟，但找不到退盟申请信息");
                }
                this.updateByBusIdAndMemberIdAndOutId(busId, memberId, tgtUnionMemberOut.getId(), CommonConstant.COMMON_YES);
                break;
            case MemberConstant.STATUS_APPLY_IN: //目标对象直接设置为退盟过渡期
                //目标对象的伪退盟申请
                UnionMemberOut saveUnionMemberOut = new UnionMemberOut();
                Date currentDate = DateUtil.getCurrentDate();
                saveUnionMemberOut.setCreatetime(currentDate); //创建时间
                saveUnionMemberOut.setDelStatus(CommonConstant.DEL_STATUS_NO); //删除状态
                saveUnionMemberOut.setApplyMemberId(tgtMemberId); //退盟盟员id
                saveUnionMemberOut.setApplyOutReason("盟主移出"); //退盟理由
                saveUnionMemberOut.setConfirmOutTime(currentDate); //盟主审核确认时间
                saveUnionMemberOut.setActualOutTime(DateUtil.addDays(currentDate, 15)); //实际退盟时间
                //目标对象的更新状态
                UnionMember updateUnionMember = new UnionMember();
                updateUnionMember.setId(tgtMemberId); //目标盟员id
                updateUnionMember.setStatus(MemberConstant.STATUS_OUTING); //目标盟员状态为退盟过渡期
                //事务化操作
                this.insert(saveUnionMemberOut);
                this.unionMemberService.updateById(updateUnionMember);
        }

    }

    //------------------------------------------------- save ----------------------------------------------------------

    /**
     * 根据商家id、盟员身份id和退盟理由，保存申请退盟信息
     *
     * @param busId          {not null} 商家id
     * @param memberId       {not null} 盟员身份id
     * @param applyOutReason {not null} 退盟理由
     * @throws Exception
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveApplyOutByBusIdAndMemberId(Integer busId, Integer memberId, String applyOutReason) throws Exception {
        if (busId == null || memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        UnionMember unionMember = this.unionMemberService.getByIdAndBusId(memberId, busId);
        if (unionMember == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionMainValid(unionMember.getUnionId());
        //(3)判断是否具有写权限
        if (!this.unionMemberService.hasWriteAuthority(unionMember)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_WRITE_REJECT);
        }
        //(4)判断当前状态
        if (unionMember.getStatus() == MemberConstant.STATUS_APPLY_OUT || unionMember.getStatus() == MemberConstant.STATUS_OUTING) {
            throw new BusinessException("已申请退盟或正在退盟过渡期");
        }
        UnionMain unionMain = this.unionMainService.getById(unionMember.getUnionId());
        if (unionMain == null) {
            throw new BusinessException("联盟不存在或已过期");
        }
        //(5)要保存的退盟申请信息
        UnionMemberOut saveUnionMemberOut = new UnionMemberOut();
        saveUnionMemberOut.setCreatetime(DateUtil.getCurrentDate()); //申请时间
        saveUnionMemberOut.setDelStatus(CommonConstant.DEL_STATUS_NO); //删除状态
        saveUnionMemberOut.setApplyMemberId(memberId); //申请退盟的盟员id
        saveUnionMemberOut.setApplyOutReason(applyOutReason); //退盟理由
        //(6)更新盟员状态为申请退盟状态
        UnionMember updateUnionMember = new UnionMember();
        updateUnionMember.setId(memberId);
        updateUnionMember.setStatus(MemberConstant.STATUS_APPLY_OUT);
        //(7)短信通知
        UnionMember unionOwner = this.unionMemberService.getUnionOwnerByUnionId(unionMain.getId());
        if (unionOwner == null) {
            throw new BusinessException("盟主帐号不存在或已过期");
        }
        String content = new StringBuilder("\"")
                .append(unionMember.getEnterpriseAddress())
                .append("\"申请退出\"")
                .append(unionMain.getName())
                .append("\",请到退盟审核处查看并处理").toString();
        String phone = StringUtil.isNotEmpty(unionOwner.getNotifyPhone()) ? unionOwner.getNotifyPhone() : unionOwner.getDirectorPhone();
        PhoneMessage phoneMessage = new PhoneMessage(busId, phone, content);
        //(8)事务操作
        this.insert(saveUnionMemberOut);
        this.unionMemberService.updateById(updateUnionMember);
        this.phoneMessageSender.sendMsg(phoneMessage);
    }

    //------------------------------------------------- count ---------------------------------------------------------
    //------------------------------------------------ boolean --------------------------------------------------------
}
