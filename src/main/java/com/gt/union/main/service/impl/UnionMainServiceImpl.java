package com.gt.union.main.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.api.bean.session.BusUser;
import com.gt.union.api.client.dict.IDictService;
import com.gt.union.api.client.user.IBusUserService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.*;
import com.gt.union.main.constant.MainConstant;
import com.gt.union.main.entity.UnionMain;
import com.gt.union.main.entity.UnionMainCharge;
import com.gt.union.main.entity.UnionMainDict;
import com.gt.union.main.mapper.UnionMainMapper;
import com.gt.union.main.service.IUnionMainChargeService;
import com.gt.union.main.service.IUnionMainDictService;
import com.gt.union.main.service.IUnionMainService;
import com.gt.union.main.vo.UnionMainChargeVO;
import com.gt.union.main.vo.UnionMainVO;
import com.gt.union.member.constant.MemberConstant;
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.service.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * <p>
 * 联盟主表 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@Service
public class UnionMainServiceImpl extends ServiceImpl<UnionMainMapper, UnionMain> implements IUnionMainService {

    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IBusUserService busUserService;

    @Autowired
    private IUnionMainChargeService unionMainChargeService;

    @Autowired
    private IUnionMainDictService unionMainDictService;

    @Autowired
    private IDictService dictService;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - get *********************************************
     ******************************************************************************************************************/

    @Override
    public UnionMain getByBusIdAndMemberId(Integer busId, Integer memberId) throws Exception {
        if (busId == null || memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        UnionMember member = this.unionMemberService.getByIdAndBusId(memberId, busId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.checkUnionValid(member.getUnionId());
        //(3)判断是否具有读权限
        if (!this.unionMemberService.hasReadAuthority(member)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_READ_REJECT);
        }
        return this.getById(member.getUnionId());
    }

    @Override
    public Integer getLimitMemberByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        Integer result = Integer.valueOf(0);
        List<Map> dictList = this.dictService.getCreateUnionDict();
        BusUser busUser = this.busUserService.getBusUserById(busId);
        for (Map dict : dictList) {
            if (dict.get("item_key").equals(busUser.getLevel())) {
                String itemValue = dict.get("item_value").toString();
                String unionMember = itemValue.split(",")[1];
                result = Integer.valueOf(unionMember); //联盟成员总数上限
                break;
            }
        }
        return result;
    }

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - list ********************************************
     ******************************************************************************************************************/

    @Override
    public Page<UnionMain> pageReadByBusId(Page page, Integer busId) throws Exception {
        if (page == null || busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .exists(new StringBuilder("SELECT m.id FROM t_union_member m")
                        .append(" WHERE m.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("  AND m.union_id = t_union_main.id")
                        .append("  AND m.bus_id = ").append(busId)
                        .append("  AND m.status in(").append(MemberConstant.STATUS_IN)
                        .append("    , ").append(MemberConstant.STATUS_APPLY_OUT)
                        .append("    , ").append(MemberConstant.STATUS_OUTING)
                        .append("    )")
                        .toString());
        return this.selectPage(page, entityWrapper);
    }

    @Override
    public Page<UnionMain> pageOtherUnionByBusId(Page page, Integer busId) throws Exception {
        if (page == null || busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .notExists(new StringBuilder(" SELECT m.id FROM t_union_member m")
                        .append(" WHERE m.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("  AND m.union_id = t_union_main.id")
                        .append("  AND m.bus_id = ").append(busId)
                        .toString());
        return this.selectPage(page, entityWrapper);
    }

    @Override
    public List<UnionMain> listByIds(List<Integer> unionIdList) throws Exception {
        List<UnionMain> result = new ArrayList<>();

        if (ListUtil.isNotEmpty(unionIdList)) {
            for (Integer unionId : unionIdList) {
                UnionMain union = this.getById(unionId);
                result.add(union);
            }
        }

        return result;
    }

    @Override
    public List<UnionMain> intersection(List<UnionMain> unionList, List<UnionMain> unionList2) throws Exception {
        List<UnionMain> result = new ArrayList<>();
        if (ListUtil.isEmpty(unionList) || ListUtil.isEmpty(unionList2)) {
            return result;
        }
        Set<Integer> unionIdSet = new HashSet<>();
        for (UnionMain union : unionList) {
            unionIdSet.add(union.getId());
        }
        for (UnionMain union2 : unionList2) {
            if (unionIdSet.contains(union2.getId())) {
                result.add(union2);
            }
        }
        return result;
    }

    @Override
    public List<UnionMain> listReadByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMain> result = new ArrayList<>();
        List<UnionMember> memberList = this.unionMemberService.listReadByBusId(busId);
        if (ListUtil.isNotEmpty(memberList)) {
            result = listByMemberList(memberList);
        }
        return result;
    }

    /**
     * 根据商家id，获取所有具有写权限的盟员身份所在的联盟列表信息
     *
     * @param busId {not null} 商家id
     * @return
     * @throws Exception
     */
    @Override
    public List<UnionMain> listWriteByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMain> result = new ArrayList<>();
        List<UnionMember> memberList = this.unionMemberService.listWriteByBusId(busId);
        if (ListUtil.isNotEmpty(memberList)) {
            result = listByMemberList(memberList);
        }
        return result;
    }

    private List<UnionMain> listByMemberList(List<UnionMember> memberList) throws Exception {
        List<UnionMain> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(memberList)) {
            List<Integer> unionIdList = new ArrayList<>();
            for (UnionMember member : memberList) {
                unionIdList.add(member.getUnionId());
            }
            result = this.listByIds(unionIdList);
        }
        return result;
    }

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - save ********************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - remove ******************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - update ******************************************
     ******************************************************************************************************************/

    @Override
    @Transactional
    public void updateByMemberIdAndBusIdAndVO(Integer memberId, Integer busId, UnionMainVO vo) throws Exception {
        if (memberId == null || busId == null || vo == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        UnionMember unionOwner = this.unionMemberService.getByIdAndBusId(memberId, busId);
        if (unionOwner == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        Integer unionId = unionOwner.getUnionId();
        this.checkUnionValid(unionId);
        //(3)判断是否具有写权限
        if (!this.unionMemberService.hasWriteAuthority(unionOwner)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_WRITE_REJECT);
        }
        //(4)判断是否具有盟主权限
        if (!unionOwner.getIsUnionOwner().equals(MemberConstant.IS_UNION_OWNER_YES)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_NEED_OWNER);
        }
        //(5)联盟积分开启后不能关闭
        UnionMain union = this.getById(unionId);
        if (union == null) {
            throw new BusinessException("找不到联盟信息");
        }
        if (union.getIsIntegral().equals(MainConstant.IS_INTEGRAL_YES) && vo.getIsIntegral().equals(MainConstant.IS_INTEGRAL_NO)) {
            throw new BusinessException("积分开启后不可关闭");
        }
        //(6)联盟更新信息
        UnionMain updateUnion = new UnionMain();
        updateUnion.setId(unionId); //联盟id
        updateUnion.setName(vo.getUnionName()); //联盟名称
        String unionImg = vo.getUnionImg();
        if (StringUtil.isNotEmpty(unionImg) && unionImg.indexOf("/upload/") > -1) {
            updateUnion.setImg(unionImg.split("/upload/")[1]); //联盟图标
        }
        updateUnion.setJoinType(vo.getJoinType()); //加盟方式
        updateUnion.setIllustration(vo.getUnionIllustration()); //联盟说明
        updateUnion.setIsIntegral(vo.getIsIntegral()); //是否开启积分

        //(7)联盟收费实体-黑卡
        List<UnionMainCharge> blackChargeList = this.unionMainChargeService.listByUnionIdAndType(unionId, MainConstant.CHARGE_TYPE_BLACK);
        if (ListUtil.isEmpty(blackChargeList)) {
            throw new BusinessException("找不到联盟黑卡设置信息");
        }
        UnionMainChargeVO chargeVO = vo.getUnionMainChargeVO();
        UnionMainCharge updateBlackCharge = new UnionMainCharge();
        updateBlackCharge.setId(blackChargeList.get(0).getId());
        Integer blackIsAvailable = chargeVO.getBlackIsAvailable();
        updateBlackCharge.setIsAvailable(blackIsAvailable); //黑卡是否启用
        Integer blackIsCharge = chargeVO.getBlackIsCharge();
        updateBlackCharge.setIsCharge(blackIsCharge); //黑卡是否收费
        if (MainConstant.CHARGE_IS_AVAILABLE_YES == blackIsAvailable && MainConstant.CHARGE_IS_CHARGE_YES == blackIsCharge) {
            Double blackChargePrice = chargeVO.getBlackChargePrice();
            if (blackChargePrice < 0) {
                throw new BusinessException("黑卡价格不能小于0");
            }
            updateBlackCharge.setChargePrice(blackChargePrice); //黑卡收费价格
            Integer blackValidityDay = chargeVO.getBlackValidityDay();
            if (blackValidityDay < 0) {
                throw new BusinessException("黑卡有效期不能小于0");
            }
            updateBlackCharge.setValidityDay(blackValidityDay); //黑卡有效期
            updateBlackCharge.setIllustration(chargeVO.getBlackIllustration()); //黑卡说明
        } else {
            updateBlackCharge.setChargePrice(0D); //黑卡收费价格
            updateBlackCharge.setValidityDay(0); //黑卡有效期
            updateBlackCharge.setIllustration(""); //黑卡说明
        }

        //(8)联盟收费实体-红卡
        List<UnionMainCharge> redChargeList = this.unionMainChargeService.listByUnionIdAndType(unionId, MainConstant.CHARGE_TYPE_RED);
        if (ListUtil.isEmpty(redChargeList)) {
            throw new BusinessException("找不到联盟红卡设置信息");
        }
        UnionMainCharge updateRedCharge = new UnionMainCharge();
        updateRedCharge.setId(redChargeList.get(0).getId());
        Integer redIsAvailable = chargeVO.getRedIsAvailable();
        updateRedCharge.setIsAvailable(redIsAvailable); //红卡是否启用
        Integer redIsCharge = chargeVO.getRedIsCharge();
        updateRedCharge.setIsCharge(redIsCharge); //红卡是否收费
        if (MainConstant.CHARGE_IS_AVAILABLE_YES == redIsAvailable && MainConstant.CHARGE_IS_CHARGE_YES == redIsCharge) {
            Double redChargePrice = chargeVO.getRedChargePrice();
            if (redChargePrice < 0) {
                throw new BusinessException("红卡价格不能小于0");
            }
            updateRedCharge.setChargePrice(redChargePrice); //红卡收费价格
            Integer redValidityDay = chargeVO.getRedValidityDay();
            if (redValidityDay < 0) {
                throw new BusinessException("红卡有效期不能小于0");
            }
            updateRedCharge.setValidityDay(redValidityDay); //红卡有效期
            updateRedCharge.setIllustration(chargeVO.getBlackIllustration()); //红卡说明
        } else {
            updateRedCharge.setChargePrice(0D); //红卡收费价格
            updateRedCharge.setValidityDay(0); //红卡有效期
            updateRedCharge.setIllustration(""); //红卡说明
        }
        //(9)黑卡价格不能高于红卡价格
        if (MainConstant.CHARGE_IS_AVAILABLE_YES == blackIsAvailable && MainConstant.CHARGE_IS_CHARGE_YES == blackIsCharge
                && MainConstant.CHARGE_IS_AVAILABLE_YES == redIsAvailable && MainConstant.CHARGE_IS_CHARGE_YES == redIsCharge) {
            if (chargeVO.getBlackChargePrice() > chargeVO.getRedChargePrice()) {
                throw new BusinessException("黑卡价格不能高于红卡价格");
            }
        }

        //(10)事务处理
        this.update(updateUnion);
        this.unionMainChargeService.update(updateBlackCharge);
        this.unionMainChargeService.update(updateRedCharge);
        this.unionMainDictService.removeByUnionId(unionId);
        List<UnionMainDict> dictList = vo.getUnionMainDictList();
        if (ListUtil.isNotEmpty(dictList)) {
            this.unionMainDictService.saveBatch(dictList);
        }
    }

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - count *******************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - boolean *****************************************
     ******************************************************************************************************************/

    @Override
    public void checkUnionValid(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMain union = this.getById(unionId);
        checkUnionValid(union);
    }

    @Override
    public void checkUnionValid(UnionMain union) throws Exception {
        if (union == null) {
            throw new BusinessException("联盟不存在");
        }
        //(1)如果缓存中已存在，说明有效，直接返回
        String unionValidKey = RedisKeyUtil.getUnionValidKey(union.getId());
        if (this.redisCacheUtil.exists(unionValidKey)) {
            return;
        }
        //(2)检查联盟有效性
        if (union.getUnionValidity().compareTo(DateUtil.getCurrentDate()) < 0) {
            throw new BusinessException("联盟已过期");
        }
        //(3)检查盟主有效性
        UnionMember unionOwner = this.unionMemberService.getOwnerByUnionId(union.getId());
        if (unionOwner == null) {
            throw new BusinessException("找不到盟主信息");
        }
        // （4）重新存入缓存
        this.redisCacheUtil.set(unionValidKey, CommonConstant.COMMON_YES);
    }

    @Override
    public boolean isUnionValid(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMain union = this.getById(unionId);
        return isUnionValid(union);
    }

    @Override
    public boolean isUnionValid(UnionMain union) throws Exception {
        if (union == null) {
            throw new BusinessException("联盟不存在");
        }
        //(1)如果缓存中已存在，说明有效，直接返回
        String unionValidKey = RedisKeyUtil.getUnionValidKey(union.getId());
        if (this.redisCacheUtil.exists(unionValidKey)) {
            return true;
        }
        //(2)检查联盟有效性
        if (union.getUnionValidity().compareTo(DateUtil.getCurrentDate()) < 0) {
            return false;
        }
        //(3)检查盟主有效性
        UnionMember unionOwner = this.unionMemberService.getOwnerByUnionId(union.getId());
        if (unionOwner == null) {
            return false;
        }
        // （4）重新存入缓存
        this.redisCacheUtil.set(unionValidKey, "1");
        return true;
    }

    /*******************************************************************************************************************
     ****************************************** Object As a Service - get **********************************************
     ******************************************************************************************************************/

    @Override
    public UnionMain getById(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMain result;
        //(1)cache
        String unionIdKey = RedisKeyUtil.getUnionIdKey(unionId);
        if (this.redisCacheUtil.exists(unionIdKey)) {
            String tempStr = this.redisCacheUtil.get(unionIdKey);
            result = JSONArray.parseObject(tempStr, UnionMain.class);
            return result;
        }
        //(2)db
        EntityWrapper<UnionMain> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", unionId)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = this.selectOne(entityWrapper);
        setCache(result, unionId);
        return result;
    }

    /*******************************************************************************************************************
     ****************************************** Object As a Service - list *********************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Object As a Service - save *********************************************
     ******************************************************************************************************************/

    @Override
    @Transactional
    public void save(UnionMain newUnion) throws Exception {
        if (newUnion == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        this.insert(newUnion);
        this.removeCache(newUnion);
    }

    @Override
    @Transactional
    public void saveBatch(List<UnionMain> newUnionList) throws Exception {
        if (newUnionList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        this.insertBatch(newUnionList);
        this.removeCache(newUnionList);
    }

    /*******************************************************************************************************************
     ****************************************** Object As a Service - remove *******************************************
     ******************************************************************************************************************/

    @Override
    @Transactional
    public void removeById(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        UnionMain union = this.getById(unionId);
        removeCache(union);
        //(2)remove in db logically
        UnionMain removeUnion = new UnionMain();
        removeUnion.setId(unionId);
        removeUnion.setDelStatus(CommonConstant.DEL_STATUS_YES);
        this.updateById(removeUnion);
    }

    @Override
    @Transactional
    public void removeBatchById(List<Integer> unionIdList) throws Exception {
        if (unionIdList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        List<UnionMain> unionList = new ArrayList<>();
        for (Integer unionId : unionIdList) {
            UnionMain union = this.getById(unionId);
            unionList.add(union);
        }
        removeCache(unionList);
        //(2)remove in db logically
        List<UnionMain> removeUnionList = new ArrayList<>();
        for (Integer unionId : unionIdList) {
            UnionMain removeUnion = new UnionMain();
            removeUnion.setId(unionId);
            removeUnion.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionList.add(removeUnion);
        }
        this.updateBatchById(removeUnionList);
    }

    /*******************************************************************************************************************
     ****************************************** Object As a Service - update *******************************************
     ******************************************************************************************************************/

    @Override
    @Transactional
    public void update(UnionMain updateUnion) throws Exception {
        if (updateUnion == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        Integer unionId = updateUnion.getId();
        UnionMain union = this.getById(unionId);
        removeCache(union);
        //(2)update db
        this.updateById(updateUnion);
    }

    @Override
    @Transactional
    public void updateBatch(List<UnionMain> updateUnionList) throws Exception {
        if (updateUnionList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        List<Integer> unionIdList = new ArrayList<>();
        for (UnionMain updateUnion : updateUnionList) {
            unionIdList.add(updateUnion.getId());
        }
        List<UnionMain> unionList = new ArrayList<>();
        for (Integer unionId : unionIdList) {
            UnionMain union = this.getById(unionId);
            unionList.add(union);
        }
        removeCache(unionList);
        //(2)update db
        this.updateBatchById(updateUnionList);
    }

    /*******************************************************************************************************************
     ****************************************** Object As a Service - cache support ************************************
     ******************************************************************************************************************/

    private void setCache(UnionMain newUnion, Integer unionId) {
        if (unionId == null) {
            return; //do nothing,just in case
        }
        String unionIdKey = RedisKeyUtil.getUnionIdKey(unionId);
        this.redisCacheUtil.set(unionIdKey, newUnion);
    }

    private void setCache(List<UnionMain> newUnionList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            return; //do nothing,just in case
        }
        String foreignIdKey;
        switch (foreignIdType) {
            default:
                break;
        }
    }

    private void removeCache(UnionMain union) {
        if (union == null) {
            return;
        }
        Integer unionId = union.getId();
        String unionIdKey = RedisKeyUtil.getUnionIdKey(unionId);
        this.redisCacheUtil.remove(unionIdKey);
    }

    private void removeCache(List<UnionMain> unionList) {
        if (ListUtil.isEmpty(unionList)) {
            return;
        }
        List<Integer> unionIdList = new ArrayList<>();
        for (UnionMain union : unionList) {
            unionIdList.add(union.getId());
        }
        List<String> unionIdKeyList = RedisKeyUtil.getUnionIdKey(unionIdList);
        this.redisCacheUtil.remove(unionIdKeyList);
    }

    private List<String> getForeignIdKeyList(List<UnionMain> unionList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            default:
                break;
        }
        return result;
    }

}
