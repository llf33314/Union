package com.gt.union.member.service.impl;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.StringUtil;
import com.gt.union.main.entity.UnionMain;
import com.gt.union.main.service.IUnionMainService;
import com.gt.union.member.constant.MemberConstant;
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.entity.UnionMemberJoin;
import com.gt.union.member.mapper.UnionMemberJoinMapper;
import com.gt.union.member.service.IUnionMemberJoinService;
import com.gt.union.member.service.IUnionMemberService;
import com.gt.union.member.vo.UnionMemberJoinVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * <p>
 * 联盟成员入盟申请 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@Service
public class UnionMemberJoinServiceImpl extends ServiceImpl<UnionMemberJoinMapper, UnionMemberJoin> implements IUnionMemberJoinService {
    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionMainService unionMainService;

    /**
     * * 根据商家id和盟员id，分页获取入盟申请列表信息
     *
     * @param page                 分页对象
     * @param busId                {not null} 商家id
     * @param memberId             {not null} 盟员id
     * @param optionEnterpriseName 可选项 企业名称
     * @param optionDirectorPhone  可选项 负责人电话
     * @return
     * @throws Exception
     */
    @Override
    public Page pageMapByBusIdAndMemberId(Page page, Integer busId, Integer memberId, final String optionEnterpriseName
            , final String optionDirectorPhone) throws Exception {
        if (page == null || busId == null || memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        if (!this.unionMemberService.isUnionMemberValid(busId, memberId)) {
            throw new BusinessException("不具有盟员身份或已过期");
        }
        final UnionMember unionMember = this.unionMemberService.getById(memberId);
        if (unionMember == null || unionMember.getIsUnionOwner() != MemberConstant.IS_UNION_OWNER_YES) {
            throw new BusinessException("非盟主身份无法操作");
        }
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" mj")
                        .append(" LEFT JOIN t_union_member ma ON m.id = mj.apply_member_id")
                        .append(" LEFT JOIN t_union_member mr ON mr.id = mj.recommend_member_id")
                        .append(" WHERE mj.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("  AND (")
                        .append("    (mj.type = ").append(MemberConstant.JOIN_TYPE_JOIN)
                        .append("      AND mj.recommend_member_id = null)")
                        .append("    OR (mj.type = ").append(MemberConstant.JOIN_TYPE_RECOMMEND)
                        .append("      AND mj.is_recommend_agree = ").append(CommonConstant.COMMON_YES)
                        .append("      AND mj.recommend_member_id != null)")
                        .append("  )")
                        .append("  AND ma.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("  AND ma.status = ").append(MemberConstant.STATUS_APPLY_IN)
                        .append("  AND EXISTS (")
                        .append("    SELECT m2.id FROM t_union_member m2")
                        .append("    WHERE m2.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("      AND m2.id = ").append(unionMember.getId())
                        .append("      AND m2.union_id = ma.union_id");
                if (StringUtil.isNotEmpty(optionEnterpriseName)) {
                    sbSqlSegment.append(" AND m.enterprise_name LIKE %").append(optionEnterpriseName).append("%");
                }
                if (StringUtil.isNotEmpty(optionDirectorPhone)) {
                    sbSqlSegment.append(" AND m.director_phone LIKE %").append(optionDirectorPhone).append("%");
                }
                sbSqlSegment.append(" ORDER BY mj.id ASC");
                return sbSqlSegment.toString();
            }
        };
        StringBuilder sbSqlSelect = new StringBuilder(" mj.id joinId") //申请加入申请id
                .append(", m.enterprise_name joinEnterpriseName") //企业名称
                .append(", m.director_name joinDirectorName") //负责人名称
                .append(", m.director_phone joinDirectorPhone") //负责人电话
                .append(", m.director_email joinDirectorEmail") //负责人邮箱
                .append(", mj.reason joinReason") //申请加入理由
                .append(", DATE_FORMAT(mj.createtime, '%Y-%m-%d %T') joinTime") //申请加入时间
                .append(", mr.enterprise_name recommendEnterpriseName"); //推荐人名称
        wrapper.setSqlSelect(sbSqlSelect.toString());
        return this.selectPage(page, wrapper);
    }

    /**
     * 入盟审核操作，同意入盟或不同意入盟
     *
     * @param busId    {not null} 商家id
     * @param memberId {not null} 盟员身份id
     * @param joinId   {not null} 入盟申请id
     * @param isOK     {not null} 是否同意入盟
     * @throws Exception
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateJoinStatus(Integer busId, Integer memberId, Integer joinId, Integer isOK) throws Exception {
        if (busId == null || memberId == null || joinId == null || isOK == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        if (this.unionMemberService.isUnionMemberValid(busId, memberId)) {
            throw new BusinessException("不具有盟员身份或已过期");
        }
        UnionMember unionMember = this.unionMemberService.getById(memberId);
        if (unionMember == null || unionMember.getIsUnionOwner() != MemberConstant.IS_UNION_OWNER_YES) {
            throw new BusinessException("非盟主身份无法操作");
        }
        UnionMemberJoin unionMemberJoin = this.selectById(joinId);
        if (unionMemberJoin == null) {
            throw new BusinessException("入盟申请不存在或已处理");
        }
        UnionMember unionJoiner = this.unionMemberService.getById(unionMemberJoin.getApplyMemberId());
        if (unionJoiner == null) {
            throw new BusinessException("入盟申请人信息不存在");
        }
        if (!unionMember.getUnionId().equals(unionJoiner.getUnionId())) {
            throw new BusinessException("无法审核其他联盟的入盟申请");
        }
        if (!unionJoiner.getStatus().equals(MemberConstant.JOIN_TYPE_JOIN)) {
            throw new BusinessException("申请人已加入联盟");
        }
        // (1)入盟申请信息更新内容
        UnionMemberJoin updateUnionMemberJoin = new UnionMemberJoin();
        updateUnionMemberJoin.setId(unionMemberJoin.getId());
        updateUnionMemberJoin.setDelStatus(CommonConstant.DEL_STATUS_YES);
        // (2)入盟申请盟员更新内容
        UnionMember updateUnionJoiner = new UnionMember();
        updateUnionJoiner.setId(unionJoiner.getId());
        if (isOK == CommonConstant.COMMON_YES) {
            updateUnionJoiner.setStatus(MemberConstant.STATUS_IN);
        } else {
            updateUnionJoiner.setDelStatus(CommonConstant.DEL_STATUS_YES);
        }
        //事务化操作
        this.updateById(updateUnionMemberJoin);
        this.unionMemberService.updateById(updateUnionJoiner);
    }

    /**
     * 保存自由加入联盟的申请信息
     *
     * @param busId             {not null} 商家id
     * @param unionId           {not null} 联盟id
     * @param unionMemberJoinVO {not null} 申请信息实体
     * @throws Exception
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveTypeJoin(Integer busId, Integer unionId, UnionMemberJoinVO unionMemberJoinVO) throws Exception {
        if (busId == null || unionId == null || unionMemberJoinVO == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //1、判断是否已加入或申请加入了该盟
        UnionMember unionMember = this.unionMemberService.getByUnionIdAndBusId(unionId, busId);
        if (unionMember != null) {
            throw new BusinessException("已加入联盟或已申请加入联盟");
        }
        UnionMain unionMain = this.unionMainService.getById(unionId);
        if (unionMain == null) {
            throw new BusinessException("联盟不存在或已过期");
        }
        //TODO
        //this.unionMemberService.countByUnionIdAndStatus()
    }

    /**
     * 保存推荐加入联盟的申请信息
     *
     * @param busId             {not null} 商家id
     * @param memberId          {not null} 推荐人盟员身份id
     * @param unionMemberJoinVO {not null} 申请信息实体
     * @throws Exception
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveTypeRecommend(Integer busId, Integer memberId, UnionMemberJoinVO unionMemberJoinVO) throws Exception {

    }
}
