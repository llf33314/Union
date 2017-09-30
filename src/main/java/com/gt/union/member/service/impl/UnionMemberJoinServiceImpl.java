package com.gt.union.member.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
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
import com.gt.union.common.util.*;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - get *********************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - list ********************************************
     ******************************************************************************************************************/

    @Override
    public Page pageMapByBusIdAndMemberId(Page page, Integer busId, Integer memberId, final String optionEnterpriseName
            , final String optionDirectorPhone) throws Exception {
        if (page == null || busId == null || memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        final UnionMember unionOwner = this.unionMemberService.getByIdAndBusId(memberId, busId);
        if (unionOwner == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionValid(unionOwner.getUnionId());
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
                StringBuilder sbSqlSegment = new StringBuilder(" mj")
                        .append(" LEFT JOIN t_union_member ma ON ma.id = mj.apply_member_id")
                        .append(" LEFT JOIN t_union_member mr ON mr.id = mj.recommend_member_id")
                        .append(" WHERE mj.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("  AND (")
                        .append("    (mj.type = ").append(MemberConstant.JOIN_TYPE_JOIN)
                        .append("      AND mj.recommend_member_id IS NULL)")
                        .append("    OR (mj.type = ").append(MemberConstant.JOIN_TYPE_RECOMMEND)
                        .append("      AND mj.is_recommend_agree = ").append(CommonConstant.COMMON_YES)
                        .append("      AND mj.recommend_member_id IS NOT NULL)")
                        .append("  )")
                        .append("  AND ma.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("  AND ma.status = ").append(MemberConstant.STATUS_APPLY_IN)
                        .append("  AND EXISTS (")
                        .append("    SELECT m2.id FROM t_union_member m2")
                        .append("    WHERE m2.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("      AND m2.id = ").append(unionOwner.getId())
                        .append("      AND m2.union_id = ma.union_id")
                        .append("  )");
                if (StringUtil.isNotEmpty(optionEnterpriseName)) {
                    sbSqlSegment.append(" AND ma.enterprise_name LIKE '%").append(optionEnterpriseName).append("%'");
                }
                if (StringUtil.isNotEmpty(optionDirectorPhone)) {
                    sbSqlSegment.append(" AND ma.director_phone LIKE '%").append(optionDirectorPhone).append("%'");
                }
                sbSqlSegment.append(" ORDER BY mj.id ASC");
                return sbSqlSegment.toString();
            }
        };
        StringBuilder sbSqlSelect = new StringBuilder(" mj.id joinId") //申请加入申请id
                .append(", ma.enterprise_name joinEnterpriseName") //企业名称
                .append(", ma.director_name joinDirectorName") //负责人名称
                .append(", ma.director_phone joinDirectorPhone") //负责人电话
                .append(", ma.director_email joinDirectorEmail") //负责人邮箱
                .append(", mj.reason joinReason") //申请加入理由
                .append(", DATE_FORMAT(mj.createtime, '%Y-%m-%d %T') joinTime") //申请加入时间
                .append(", mr.enterprise_name recommendEnterpriseName"); //推荐人名称
        wrapper.setSqlSelect(sbSqlSelect.toString());
        return this.selectMapsPage(page, wrapper);
    }

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - save ********************************************
     ******************************************************************************************************************/

    @Override
    @Transactional
    public void saveTypeJoin(Integer busId, Integer unionId, UnionMemberJoinVO unionMemberJoinVO) throws Exception {
        if (busId == null || unionId == null || unionMemberJoinVO == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否已加入或申请加入了联盟
        UnionMember joiner = this.unionMemberService.getByBusIdAndUnionId(busId, unionId);
        if (joiner != null) {
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
        UnionMemberJoin saveJoin = new UnionMemberJoin();
        saveJoin.setCreatetime(DateUtil.getCurrentDate()); //申请时间
        saveJoin.setDelStatus(CommonConstant.DEL_STATUS_NO); //删除状态
        saveJoin.setType(MemberConstant.JOIN_TYPE_JOIN); //加盟类型
        saveJoin.setReason(unionMemberJoinVO.getReason()); //申请理由
        //(6)申请加入的准盟员信息
        UnionMember saveMember = new UnionMember();
        saveMember.setDelStatus(CommonConstant.DEL_STATUS_NO); //删除状态
        saveMember.setCreatetime(DateUtil.getCurrentDate()); //加入时间
        saveMember.setUnionId(unionId); //联盟id
        saveMember.setBusId(busId); //商家id
        saveMember.setIsUnionOwner(MemberConstant.IS_UNION_OWNER_NO); //是否盟主
        saveMember.setStatus(MemberConstant.STATUS_APPLY_IN); //盟员状态
        saveMember.setEnterpriseName(unionMemberJoinVO.getEnterpriseName()); //企业名称
        saveMember.setDirectorName(unionMemberJoinVO.getDirectorName()); //负责人名称
        saveMember.setDirectorPhone(unionMemberJoinVO.getDirectorPhone()); //负责人电话
        saveMember.setDirectorEmail(unionMemberJoinVO.getDirectorEmail()); //负责人邮箱
        //(7)事务化处理
        this.unionMemberService.save(saveMember); //保存准盟员信息
        saveJoin.setApplyMemberId(saveMember.getId());
        this.save(saveJoin); //保存申请信息
    }

    @Override
    @Transactional
    public void saveTypeRecommend(Integer busId, Integer memberId, UnionMemberJoinVO unionMemberJoinVO) throws Exception {
        if (busId == null || memberId == null || unionMemberJoinVO == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        UnionMember memberRecommend = this.unionMemberService.getByIdAndBusId(memberId, busId);
        if (memberRecommend == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionValid(memberRecommend.getUnionId());
        //(3)判断是否具有写权限
        if (!this.unionMemberService.hasWriteAuthority(memberRecommend)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_WRITE_REJECT);
        }
        //(4)判断联盟是否设置为允许推荐加盟
        Integer unionId = memberRecommend.getUnionId();
        UnionMain unionMain = this.unionMainService.getById(unionId); //推荐者所在联盟
        if (unionMain == null) {
            throw new BusinessException("联盟不存在或已过期");
        }
        if (unionMain.getJoinType() != MainConstant.MAIN_JOIN_TYPE_BOTH) {
            throw new BusinessException("联盟被设置为不支持推荐入盟");
        }
        //(5)判断被推荐的商家是否有效
        BusUser busUserRecommended = this.busUserService.getBusUserByName(unionMemberJoinVO.getBusUserName());
        if (busUserRecommended == null) {
            throw new BusinessException("被推荐的帐号不存在");
        }
        if (busUserRecommended.getPid() != null && busUserRecommended.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            throw new BusinessException("请填写被推荐主体的主帐号");
        }
        //(6)判断被推荐的商家是否已经是盟员
        Integer recommendedBusId = busUserRecommended.getId();
        UnionMember unionMemberRecommended = this.unionMemberService.getByBusIdAndUnionId(recommendedBusId, busId);
        if (unionMemberRecommended != null) {
            throw new BusinessException("被推荐的帐号已加入或已申请加入了该联盟");
        }
        //(7)判断是否已达联盟成员总数上限
        Integer validMemberCount = this.unionMemberService.countReadByUnionId(unionId);
        if (unionMain.getLimitMember() <= validMemberCount) {
            throw new ParamException("联盟成员数已达上限，无法推荐");
        }
        //(8)判断被推荐的商家的有效盟员身份数是否低于有效盟员身份数上限
        Integer recommendedValidMemberCount = this.unionMemberService.countReadByBusId(recommendedBusId);
        if (recommendedValidMemberCount >= ConfigConstant.MAX_UNION_APPLY) {
            throw new BusinessException("被推荐帐号的加盟数已达上限");
        }
        //(9)判断联盟申请必填信息
        List<UnionMainDict> unionMainDictList = this.unionMainDictService.listByUnionId(unionId);
        checkVoByUnionDictList(unionMemberJoinVO, unionMainDictList);
        //(10)根据推荐者是否是盟主，保存推荐信息。如果是盟主，则直接入盟成功
        UnionMemberJoin saveJoin = new UnionMemberJoin();
        UnionMember saveMember = new UnionMember();
        if (memberRecommend.getIsUnionOwner() == MemberConstant.IS_UNION_OWNER_YES) { //盟主
            //(11-1)申请信息
            saveJoin.setCreatetime(DateUtil.getCurrentDate()); //申请时间
            saveJoin.setDelStatus(CommonConstant.DEL_STATUS_YES); //删除状态
            saveJoin.setType(MemberConstant.JOIN_TYPE_RECOMMEND); //加盟类型
            saveJoin.setRecommendMemberId(memberId); //推荐者id
            saveJoin.setIsRecommendAgree(CommonConstant.COMMON_YES); // 默认同意
            saveJoin.setReason(unionMemberJoinVO.getReason()); //推荐理由
            //(11-2)申请加入的准盟员信息
            saveMember.setDelStatus(CommonConstant.DEL_STATUS_NO); //删除状态
            saveMember.setCreatetime(DateUtil.getCurrentDate()); //加入时间
            saveMember.setUnionId(unionId); //联盟id
            saveMember.setBusId(recommendedBusId); //商家id
            saveMember.setIsUnionOwner(MemberConstant.IS_UNION_OWNER_NO); //是否盟主
            saveMember.setStatus(MemberConstant.STATUS_IN); //盟员状态
            saveMember.setEnterpriseName(unionMemberJoinVO.getEnterpriseName()); //企业名称
            saveMember.setDirectorName(unionMemberJoinVO.getDirectorName()); //负责人名称
            saveMember.setDirectorPhone(unionMemberJoinVO.getDirectorPhone()); //负责人电话
            saveMember.setDirectorEmail(unionMemberJoinVO.getDirectorEmail()); //负责人邮箱
        } else {
            //(11-1)申请信息
            saveJoin.setCreatetime(DateUtil.getCurrentDate()); //申请时间
            saveJoin.setDelStatus(CommonConstant.DEL_STATUS_NO); //删除状态
            saveJoin.setType(MemberConstant.JOIN_TYPE_RECOMMEND); //加盟类型
            saveJoin.setRecommendMemberId(memberId); //推荐者id
            saveJoin.setIsRecommendAgree(CommonConstant.COMMON_YES); // 默认同意
            saveJoin.setReason(unionMemberJoinVO.getReason()); //推荐理由
            //(11-2)申请加入的准盟员信息
            saveMember.setDelStatus(CommonConstant.DEL_STATUS_NO); //删除状态
            saveMember.setCreatetime(DateUtil.getCurrentDate()); //加入时间
            saveMember.setUnionId(unionId); //联盟id
            saveMember.setBusId(recommendedBusId); //商家id
            saveMember.setIsUnionOwner(MemberConstant.IS_UNION_OWNER_NO); //是否盟主
            saveMember.setStatus(MemberConstant.STATUS_APPLY_IN); //盟员状态
            saveMember.setEnterpriseName(unionMemberJoinVO.getEnterpriseName()); //企业名称
            saveMember.setDirectorName(unionMemberJoinVO.getDirectorName()); //负责人名称
            saveMember.setDirectorPhone(unionMemberJoinVO.getDirectorPhone()); //负责人电话
            saveMember.setDirectorEmail(unionMemberJoinVO.getDirectorEmail()); //负责人邮箱
        }
        //(12)事务化处理
        this.unionMemberService.save(saveMember); //保存准盟员信息
        saveJoin.setApplyMemberId(saveMember.getId());
        this.save(saveJoin); //保存推荐信息
    }

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

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - remove ******************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - update ******************************************
     ******************************************************************************************************************/

    @Override
    @Transactional
    public void updateJoinStatus(Integer busId, Integer memberId, Integer joinId, Integer isOK) throws Exception {
        if (busId == null || memberId == null || joinId == null || isOK == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        UnionMember unionOwner = this.unionMemberService.getByIdAndBusId(memberId, busId);
        if (unionOwner == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionValid(unionOwner.getUnionId());
        //(3)判断是否具有写权限
        if (!this.unionMemberService.hasWriteAuthority(unionOwner)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_WRITE_REJECT);
        }
        //(4)判断盟主身份
        if (!unionOwner.getIsUnionOwner().equals(MemberConstant.IS_UNION_OWNER_YES)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_NEED_OWNER);
        }
        //(5)判断申请信息是否过期
        UnionMemberJoin join = this.selectById(joinId);
        if (join == null) {
            throw new BusinessException("入盟申请不存在或已处理");
        }
        UnionMember joiner = this.unionMemberService.getById(join.getApplyMemberId());
        if (joiner == null) {
            throw new BusinessException("入盟申请人信息不存在");
        }
        if (!unionOwner.getUnionId().equals(joiner.getUnionId())) {
            throw new BusinessException("无法审核其他联盟的入盟申请");
        }
        if (!joiner.getStatus().equals(MemberConstant.JOIN_TYPE_JOIN)) {
            throw new BusinessException("申请人已加入联盟");
        }
        //(6)入盟申请信息更新内容
        UnionMemberJoin updateJoin = new UnionMemberJoin();
        updateJoin.setId(join.getId());
        updateJoin.setDelStatus(CommonConstant.DEL_STATUS_YES);
        //(7)入盟申请盟员更新内容
        UnionMember updateJoiner = new UnionMember();
        updateJoiner.setId(joiner.getId());
        if (isOK == CommonConstant.COMMON_YES) {
            updateJoiner.setStatus(MemberConstant.STATUS_IN);
        } else {
            updateJoiner.setDelStatus(CommonConstant.DEL_STATUS_YES);
        }
        //(8)事务化操作
        this.update(updateJoin);
        this.unionMemberService.update(updateJoiner);
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
    public UnionMemberJoin getById(Integer joinId) throws Exception {
        if (joinId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMemberJoin result;
        //(1)cache
        String joinIdKey = RedisKeyUtil.getMemberJoinIdKey(joinId);
        if (this.redisCacheUtil.exists(joinIdKey)) {
            String tempStr = this.redisCacheUtil.get(joinIdKey);
            result = JSONArray.parseObject(tempStr, UnionMemberJoin.class);
            return result;
        }
        //(2)db
        EntityWrapper<UnionMemberJoin> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", joinId)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = this.selectOne(entityWrapper);
        setCache(result, joinId);
        return result;
    }

    /*******************************************************************************************************************
     ****************************************** Object As a Service - list *********************************************
     ******************************************************************************************************************/

    @Override
    public List<UnionMemberJoin> listByApplyMemberId(Integer applyMemberId) throws Exception {
        if (applyMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMemberJoin> result;
        //(1)get in cache
        String applyMemberIdKey = RedisKeyUtil.getMemberJoinApplyMemberIdKey(applyMemberId);
        if (this.redisCacheUtil.exists(applyMemberIdKey)) {
            String tempStr = this.redisCacheUtil.get(applyMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionMemberJoin.class);
            return result;
        }
        //(2)get in db
        EntityWrapper<UnionMemberJoin> entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("apply_member_id", applyMemberId);
        result = this.selectList(entityWrapper);
        setCache(result, applyMemberId, MemberConstant.REDIS_KEY_JOIN_APPLY_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionMemberJoin> listByRecommendMemberId(Integer recommendMemberId) throws Exception {
        if (recommendMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMemberJoin> result;
        //(1)get in cache
        String recommendMemberIdKey = RedisKeyUtil.getMemberJoinRecommendMemberIdKey(recommendMemberId);
        if (this.redisCacheUtil.exists(recommendMemberIdKey)) {
            String tempStr = this.redisCacheUtil.get(recommendMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionMemberJoin.class);
            return result;
        }
        //(2)get in db
        EntityWrapper<UnionMemberJoin> entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("recommend_member_id", recommendMemberId);
        result = this.selectList(entityWrapper);
        setCache(result, recommendMemberId, MemberConstant.REDIS_KEY_JOIN_RECOMMEND_MEMBER_ID);
        return result;
    }

    /*******************************************************************************************************************
     ****************************************** Object As a Service - save *********************************************
     ******************************************************************************************************************/

    @Override
    @Transactional
    public void save(UnionMemberJoin newJoin) throws Exception {
        if (newJoin == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        this.insert(newJoin);
        this.removeCache(newJoin);
    }

    @Override
    @Transactional
    public void saveBatch(List<UnionMemberJoin> newJoinList) throws Exception {
        if (newJoinList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        this.insertBatch(newJoinList);
        this.removeCache(newJoinList);
    }

    /*******************************************************************************************************************
     ****************************************** Object As a Service - remove *******************************************
     ******************************************************************************************************************/

    @Override
    @Transactional
    public void removeById(Integer joinId) throws Exception {
        if (joinId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        UnionMemberJoin join = this.getById(joinId);
        removeCache(join);
        //(2)remove in db logically
        UnionMemberJoin removeJoin = new UnionMemberJoin();
        removeJoin.setId(joinId);
        removeJoin.setDelStatus(CommonConstant.DEL_STATUS_YES);
        this.updateById(removeJoin);
    }

    @Override
    @Transactional
    public void removeBatchById(List<Integer> joinIdList) throws Exception {
        if (joinIdList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        List<UnionMemberJoin> joinList = new ArrayList<>();
        for (Integer joinId : joinIdList) {
            UnionMemberJoin join = this.getById(joinId);
            joinList.add(join);
        }
        removeCache(joinList);
        //(2)remove in db logically
        List<UnionMemberJoin> removeJoinList = new ArrayList<>();
        for (Integer joinId : joinIdList) {
            UnionMemberJoin removeJoin = new UnionMemberJoin();
            removeJoin.setId(joinId);
            removeJoin.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeJoinList.add(removeJoin);
        }
        this.updateBatchById(removeJoinList);
    }

    /*******************************************************************************************************************
     ****************************************** Object As a Service - update *******************************************
     ******************************************************************************************************************/

    @Override
    @Transactional
    public void update(UnionMemberJoin updateJoin) throws Exception {
        if (updateJoin == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        Integer joinId = updateJoin.getId();
        UnionMemberJoin join = this.getById(joinId);
        removeCache(join);
        //(2)update db
        this.updateById(updateJoin);
    }

    @Override
    @Transactional
    public void updateBatch(List<UnionMemberJoin> updateJoinList) throws Exception {
        if (updateJoinList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        List<Integer> joinIdList = new ArrayList<>();
        for (UnionMemberJoin updateJoin : updateJoinList) {
            joinIdList.add(updateJoin.getId());
        }
        List<UnionMemberJoin> joinList = new ArrayList<>();
        for (Integer joinId : joinIdList) {
            UnionMemberJoin join = this.getById(joinId);
            joinList.add(join);
        }
        removeCache(joinList);
        //(2)update db
        this.updateBatchById(updateJoinList);
    }

    /*******************************************************************************************************************
     ****************************************** Object As a Service - cache support ************************************
     ******************************************************************************************************************/

    private void setCache(UnionMemberJoin newJoin, Integer joinId) {
        if (joinId == null) {
            return; //do nothing,just in case
        }
        String joinIdKey = RedisKeyUtil.getMemberJoinIdKey(joinId);
        this.redisCacheUtil.set(joinIdKey, newJoin);
    }

    private void setCache(List<UnionMemberJoin> newJoinList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            return; //do nothing,just in case
        }
        String foreignIdKey;
        switch (foreignIdType) {
            case MemberConstant.REDIS_KEY_JOIN_APPLY_MEMBER_ID:
                foreignIdKey = RedisKeyUtil.getMemberJoinApplyMemberIdKey(foreignId);
                this.redisCacheUtil.set(foreignIdKey, newJoinList);
                break;
            case MemberConstant.REDIS_KEY_JOIN_RECOMMEND_MEMBER_ID:
                foreignIdKey = RedisKeyUtil.getMemberJoinRecommendMemberIdKey(foreignId);
                this.redisCacheUtil.set(foreignIdKey, newJoinList);
                break;
            default:
                break;
        }
    }

    private void removeCache(UnionMemberJoin join) {
        if (join == null) {
            return;
        }
        Integer joinId = join.getId();
        String joinIdKey = RedisKeyUtil.getMemberJoinIdKey(joinId);
        this.redisCacheUtil.remove(joinIdKey);
        Integer applyMemberId = join.getApplyMemberId();
        if (applyMemberId != null) {
            String applyMemberIdKey = RedisKeyUtil.getMemberJoinApplyMemberIdKey(applyMemberId);
            this.redisCacheUtil.remove(applyMemberIdKey);
        }
        Integer recommendMemberId = join.getRecommendMemberId();
        if (recommendMemberId != null) {
            String recommendMemberIdKey = RedisKeyUtil.getMemberJoinRecommendMemberIdKey(recommendMemberId);
            this.redisCacheUtil.remove(recommendMemberIdKey);
        }
    }

    private void removeCache(List<UnionMemberJoin> joinList) {
        if (ListUtil.isEmpty(joinList)) {
            return;
        }
        List<Integer> joinIdList = new ArrayList<>();
        for (UnionMemberJoin join : joinList) {
            joinIdList.add(join.getId());
        }
        List<String> joinIdKeyList = RedisKeyUtil.getMemberJoinIdKey(joinIdList);
        this.redisCacheUtil.remove(joinIdKeyList);
        List<String> applyMemberIdKeyList = getForeignIdKeyList(joinList, MemberConstant.REDIS_KEY_JOIN_APPLY_MEMBER_ID);
        if (ListUtil.isNotEmpty(applyMemberIdKeyList)) {
            this.redisCacheUtil.remove(applyMemberIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionMemberJoin> joinList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case MemberConstant.REDIS_KEY_JOIN_APPLY_MEMBER_ID:
                for (UnionMemberJoin join : joinList) {
                    Integer applyMemberId = join.getApplyMemberId();
                    if (applyMemberId != null) {
                        String applyMemberIdKey = RedisKeyUtil.getMemberJoinApplyMemberIdKey(applyMemberId);
                        result.add(applyMemberIdKey);
                    }
                }
                break;
            case MemberConstant.REDIS_KEY_JOIN_RECOMMEND_MEMBER_ID:
                for (UnionMemberJoin join : joinList) {
                    Integer recommendMemberId = join.getRecommendMemberId();
                    if (recommendMemberId != null) {
                        String recommendMemberIdKey = RedisKeyUtil.getMemberJoinRecommendMemberIdKey(recommendMemberId);
                        result.add(recommendMemberIdKey);
                    }
                }
                break;
            default:
                break;
        }
        return result;
    }

}
