package com.gt.union.member.service.impl;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.api.bean.session.BusUser;
import com.gt.union.api.client.user.IBusUserService;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.DateUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.main.constant.MainConstant;
import com.gt.union.main.entity.UnionMain;
import com.gt.union.main.entity.UnionMainDict;
import com.gt.union.main.service.IUnionMainDictService;
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

import java.util.List;


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

    @Autowired
    private IUnionMainDictService unionMainDictService;

    @Autowired
    private IBusUserService busUserService;

    //-------------------------------------------------- get ----------------------------------------------------------
    //------------------------------------------ list(include page) ---------------------------------------------------

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

    //------------------------------------------------- update --------------------------------------------------------

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
        //(4)判断盟主身份
        if (!unionMember.getIsUnionOwner().equals(MemberConstant.IS_UNION_OWNER_YES)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_NEED_OWNER);
        }
        //(5)判断申请信息是否过期
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
        //(6)入盟申请信息更新内容
        UnionMemberJoin updateUnionMemberJoin = new UnionMemberJoin();
        updateUnionMemberJoin.setId(unionMemberJoin.getId());
        updateUnionMemberJoin.setDelStatus(CommonConstant.DEL_STATUS_YES);
        //(7)入盟申请盟员更新内容
        UnionMember updateUnionJoiner = new UnionMember();
        updateUnionJoiner.setId(unionJoiner.getId());
        if (isOK == CommonConstant.COMMON_YES) {
            updateUnionJoiner.setStatus(MemberConstant.STATUS_IN);
        } else {
            updateUnionJoiner.setDelStatus(CommonConstant.DEL_STATUS_YES);
        }
        //(8)事务化操作
        this.updateById(updateUnionMemberJoin);
        this.unionMemberService.updateById(updateUnionJoiner);
    }

    //------------------------------------------------- save ----------------------------------------------------------

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
        //(1)判断是否已加入或申请加入了联盟
        UnionMember unionMember = this.unionMemberService.getByBusIdAndUnionId(busId, unionId);
        if (unionMember != null) {
            throw new BusinessException("已加入联盟或已申请加入联盟");
        }
        UnionMain unionMain = this.unionMainService.getById(unionId);
        if (unionMain == null) {
            throw new BusinessException("联盟不存在或已过期");
        }
        //(2)判断是否已达联盟成员总数上限
        Integer validMemberCount = this.unionMemberService.countReadByUnionId(unionId);
        if (unionMain.getLimitMember() <= validMemberCount) {
            throw new ParamException("联盟成员数已达上限，无法加入");
        }
        //(3)判断申请商家的有效盟员身份数是否低于有效盟员身份数上限
        Integer applyValidMemberCount = this.unionMemberService.countReadByBusId(busId);
        if (applyValidMemberCount >= ConfigConstant.MAX_UNION_APPLY) {
            throw new BusinessException("加盟数已达上限");
        }
        //(4)判断联盟申请必填信息
        List<UnionMainDict> unionMainDictList = this.unionMainDictService.listByUnionId(unionId);
        checkVoByUnionDictList(unionMemberJoinVO, unionMainDictList);
        //(5)申请信息
        UnionMemberJoin saveUnionMemberJoin = new UnionMemberJoin();
        saveUnionMemberJoin.setCreatetime(DateUtil.getCurrentDate()); //申请时间
        saveUnionMemberJoin.setDelStatus(CommonConstant.DEL_STATUS_NO); //删除状态
        saveUnionMemberJoin.setType(MemberConstant.JOIN_TYPE_JOIN); //加盟类型
        saveUnionMemberJoin.setReason(unionMemberJoinVO.getReason()); //申请理由
        //(6)申请加入的准盟员信息
        UnionMember saveUnionMember = new UnionMember();
        saveUnionMember.setDelStatus(CommonConstant.DEL_STATUS_NO); //删除状态
        saveUnionMember.setCreatetime(DateUtil.getCurrentDate()); //加入时间
        saveUnionMember.setUnionId(unionId); //联盟id
        saveUnionMember.setBusId(busId); //商家id
        saveUnionMember.setIsUnionOwner(MemberConstant.IS_UNION_OWNER_NO); //是否盟主
        saveUnionMember.setStatus(MemberConstant.STATUS_APPLY_IN); //盟员状态
        saveUnionMember.setEnterpriseName(unionMemberJoinVO.getEnterpriseName()); //企业名称
        saveUnionMember.setDirectorName(unionMemberJoinVO.getDirectorName()); //负责人名称
        saveUnionMember.setDirectorPhone(unionMemberJoinVO.getDirectorPhone()); //负责人电话
        saveUnionMember.setDirectorEmail(unionMemberJoinVO.getDirectorEmail()); //负责人邮箱
        //(7)事务化处理
        this.unionMemberService.insert(saveUnionMember); //保存准盟员信息
        saveUnionMemberJoin.setApplyMemberId(saveUnionMember.getId());
        this.insert(saveUnionMemberJoin); //保存申请信息
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
        if (busId == null || memberId == null || unionMemberJoinVO == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        UnionMember unionMemberRecommend = this.unionMemberService.getByIdAndBusId(memberId, busId);
        if (unionMemberRecommend == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionMainValid(unionMemberRecommend.getUnionId());
        //(3)判断是否具有写权限
        if (!this.unionMemberService.hasWriteAuthority(unionMemberRecommend)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_WRITE_REJECT);
        }
        //(4)判断联盟是否设置为允许推荐加盟
        Integer unionId = unionMemberRecommend.getUnionId();
        UnionMain unionMain = this.unionMainService.getById(unionId); //推荐者所在联盟
        if (unionMain == null) {
            throw new BusinessException("联盟不存在或已过期");
        }
        if (unionMain.getJoinType() != MainConstant.MAIN_JOIN_TYPE_BOTH) {
            throw new BusinessException("联盟被设置为不支持推荐入盟");
        }
        //(5)判断被推荐的商家是否有效
        String enterpriseName = unionMemberJoinVO.getEnterpriseName();
        BusUser busUserRecommended = this.busUserService.getBusUserByName(enterpriseName);
        if (busUserRecommended == null) {
            throw new BusinessException("被推荐的帐号不存在");
        }
        if (busUserRecommended.getPid() != null && busUserRecommended.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            throw new BusinessException("请填写被推荐主体的主帐号");
        }
        if (this.busUserService.isBusUserValid(busUserRecommended)) {
            throw new BusinessException("被推荐的帐号已过期");
        }
        //(6)判断被推荐的商家是否已经是盟员
        Integer recommendedBusId = busUserRecommended.getId();
        UnionMember unionMemberRecommended = this.unionMemberService.getByBusIdAndUnionId(recommendedBusId, busId);
        if (unionMemberRecommended != null) {
            throw new BusinessException("被推荐的帐号已加入或已申请加入了该联盟");
        }
        // (5)判断是否已达联盟成员总数上限
        Integer validMemberCount = this.unionMemberService.countReadByUnionId(unionId);
        if (unionMain.getLimitMember() <= validMemberCount) {
            throw new ParamException("联盟成员数已达上限，无法推荐");
        }
        //(7)判断被推荐的商家的有效盟员身份数是否低于有效盟员身份数上限
        Integer recommendedValidMemberCount = this.unionMemberService.countReadByBusId(recommendedBusId);
        if (recommendedValidMemberCount >= ConfigConstant.MAX_UNION_APPLY) {
            throw new BusinessException("被推荐帐号的加盟数已达上限");
        }
        //(8)判断联盟申请必填信息
        List<UnionMainDict> unionMainDictList = this.unionMainDictService.listByUnionId(unionId);
        checkVoByUnionDictList(unionMemberJoinVO, unionMainDictList);
        //(9)根据推荐者是否是盟主，保存推荐信息。如果是盟主，则直接入盟成功
        UnionMemberJoin saveUnionMemberJoin = new UnionMemberJoin();
        UnionMember saveUnionMember = new UnionMember();
        if (unionMemberRecommend.getIsUnionOwner() == MemberConstant.IS_UNION_OWNER_YES) { //盟主
            //(10)申请信息
            saveUnionMemberJoin.setCreatetime(DateUtil.getCurrentDate()); //申请时间
            saveUnionMemberJoin.setDelStatus(CommonConstant.DEL_STATUS_YES); //删除状态
            saveUnionMemberJoin.setType(MemberConstant.JOIN_TYPE_RECOMMEND); //加盟类型
            saveUnionMemberJoin.setRecommendMemberId(memberId); //推荐者id
            saveUnionMemberJoin.setIsRecommendAgree(CommonConstant.COMMON_YES); // 默认同意
            saveUnionMemberJoin.setReason(unionMemberJoinVO.getReason()); //推荐理由
            //(11)申请加入的准盟员信息
            saveUnionMember.setDelStatus(CommonConstant.DEL_STATUS_NO); //删除状态
            saveUnionMember.setCreatetime(DateUtil.getCurrentDate()); //加入时间
            saveUnionMember.setUnionId(unionId); //联盟id
            saveUnionMember.setBusId(recommendedBusId); //商家id
            saveUnionMember.setIsUnionOwner(MemberConstant.IS_UNION_OWNER_NO); //是否盟主
            saveUnionMember.setStatus(MemberConstant.STATUS_IN); //盟员状态
            saveUnionMember.setEnterpriseName(unionMemberJoinVO.getEnterpriseName()); //企业名称
            saveUnionMember.setDirectorName(unionMemberJoinVO.getDirectorName()); //负责人名称
            saveUnionMember.setDirectorPhone(unionMemberJoinVO.getDirectorPhone()); //负责人电话
            saveUnionMember.setDirectorEmail(unionMemberJoinVO.getDirectorEmail()); //负责人邮箱
        } else {
            //(10)申请信息
            saveUnionMemberJoin.setCreatetime(DateUtil.getCurrentDate()); //申请时间
            saveUnionMemberJoin.setDelStatus(CommonConstant.DEL_STATUS_NO); //删除状态
            saveUnionMemberJoin.setType(MemberConstant.JOIN_TYPE_RECOMMEND); //加盟类型
            saveUnionMemberJoin.setRecommendMemberId(memberId); //推荐者id
            saveUnionMemberJoin.setIsRecommendAgree(CommonConstant.COMMON_YES); // 默认同意
            saveUnionMemberJoin.setReason(unionMemberJoinVO.getReason()); //推荐理由
            //(11)申请加入的准盟员信息
            saveUnionMember.setDelStatus(CommonConstant.DEL_STATUS_NO); //删除状态
            saveUnionMember.setCreatetime(DateUtil.getCurrentDate()); //加入时间
            saveUnionMember.setUnionId(unionId); //联盟id
            saveUnionMember.setBusId(recommendedBusId); //商家id
            saveUnionMember.setIsUnionOwner(MemberConstant.IS_UNION_OWNER_NO); //是否盟主
            saveUnionMember.setStatus(MemberConstant.STATUS_APPLY_IN); //盟员状态
            saveUnionMember.setEnterpriseName(unionMemberJoinVO.getEnterpriseName()); //企业名称
            saveUnionMember.setDirectorName(unionMemberJoinVO.getDirectorName()); //负责人名称
            saveUnionMember.setDirectorPhone(unionMemberJoinVO.getDirectorPhone()); //负责人电话
            saveUnionMember.setDirectorEmail(unionMemberJoinVO.getDirectorEmail()); //负责人邮箱
        }
        //(12)事务化处理
        this.unionMemberService.insert(saveUnionMember); //保存准盟员信息
        saveUnionMemberJoin.setApplyMemberId(saveUnionMember.getId());
        this.insert(saveUnionMemberJoin); //保存推荐信息
    }

    //------------------------------------------------- count ---------------------------------------------------------
    //------------------------------------------------ boolean --------------------------------------------------------

    //根据联盟申请填写信息设置检查vo
    private void checkVoByUnionDictList(UnionMemberJoinVO unionMemberJoinVO, List<UnionMainDict> unionMainDictList) throws Exception {
        if (ListUtil.isNotEmpty(unionMainDictList)) {
            for (UnionMainDict unionMainDict : unionMainDictList) {
                String itemKey = unionMainDict.getItemKey();
                switch (itemKey) {
                    case "directorName":
                        if (StringUtil.isEmpty(unionMemberJoinVO.getDirectorName())) {
                            throw new ParamException("负责人名称不能为空");
                        }
                        break;
                    case "directorEmail":
                        if (StringUtil.isEmpty(unionMemberJoinVO.getDirectorEmail())) {
                            throw new ParamException("负责人邮箱不能为空");
                        }
                        break;
                    case "reason":
                        if (StringUtil.isEmpty(unionMemberJoinVO.getReason())) {
                            throw new ParamException("申请理由或推荐理由不能为空");
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
