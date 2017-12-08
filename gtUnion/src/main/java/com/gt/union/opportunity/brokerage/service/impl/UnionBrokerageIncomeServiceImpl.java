package com.gt.union.opportunity.brokerage.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.opportunity.brokerage.entity.UnionBrokerageIncome;
import com.gt.union.opportunity.brokerage.mapper.UnionBrokerageIncomeMapper;
import com.gt.union.opportunity.brokerage.service.IUnionBrokerageIncomeService;
import com.gt.union.opportunity.brokerage.util.UnionBrokerageIncomeCacheUtil;
import com.gt.union.opportunity.brokerage.vo.BrokerageOpportunityVO;
import com.gt.union.opportunity.main.constant.OpportunityConstant;
import com.gt.union.opportunity.main.service.IUnionOpportunityService;
import com.gt.union.opportunity.main.vo.OpportunityVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 佣金收入 服务实现类
 *
 * @author linweicong
 * @version 2017-11-24 09:21:28
 */
@Service
public class UnionBrokerageIncomeServiceImpl extends ServiceImpl<UnionBrokerageIncomeMapper, UnionBrokerageIncome> implements IUnionBrokerageIncomeService {
    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IUnionOpportunityService unionOpportunityService;

    //***************************************** Domain Driven Design - get *********************************************

    @Override
    public UnionBrokerageIncome getByMemberIdAndUnionIdAndOpportunityId(Integer memberId, Integer unionId, Integer opportunityId) throws Exception {
        if (memberId == null || unionId == null || opportunityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionBrokerageIncome> result = listByMemberId(memberId);
        result = filterByUnionId(result, unionId);
        result = filterByOpportunityId(result, opportunityId);

        return ListUtil.isNotEmpty(result) ? result.get(0) : null;
    }

    //***************************************** Domain Driven Design - list ********************************************

    @Override
    public List<BrokerageOpportunityVO> listBrokerageOpportunityVOByBusId(Integer busId, Integer optUnionId, Integer optToMemberId, Integer optIsClose, String optClientName, String optClientPhone) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）获取已被接受的商机推荐
        List<OpportunityVO> opportunityVOList = unionOpportunityService.listFromMeByBusId(busId, optUnionId,
                String.valueOf(OpportunityConstant.OPPORTUNITY_ACCEPT_STATUS_CONFIRMED), optClientName, optClientPhone);
        // （2）	按已结算状态顺序(未>已)，时间倒序排序
        List<BrokerageOpportunityVO> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(opportunityVOList)) {
            for (OpportunityVO opportunityVO : opportunityVOList) {
                BrokerageOpportunityVO vo = new BrokerageOpportunityVO();
                vo.setOpportunity(opportunityVO.getOpportunity());
                vo.setFromMember(opportunityVO.getFromMember());
                vo.setToMember(opportunityVO.getToMember());
                vo.setUnion(opportunityVO.getUnion());

                boolean existIncome = existByMemberIdAndUnionIdAndOpportunityId(vo.getFromMember().getId(), vo.getFromMember().getUnionId(), vo.getOpportunity().getId());
                if (optIsClose != null) {
                    boolean isOK = (CommonConstant.COMMON_YES == optIsClose && existIncome) || (CommonConstant.COMMON_NO == optIsClose && !existIncome);
                    if (!isOK) {
                        continue;
                    }
                }
                vo.setIsClose(existIncome ? CommonConstant.COMMON_YES : CommonConstant.COMMON_NO);
                result.add(vo);
            }
        }
        Collections.sort(result, new Comparator<BrokerageOpportunityVO>() {
            @Override
            public int compare(BrokerageOpportunityVO o1, BrokerageOpportunityVO o2) {
                if (o1.getIsClose().compareTo(o2.getIsClose()) < 0) {
                    return 1;
                } else if (o1.getIsClose().compareTo(o2.getIsClose()) > 0) {
                    return -1;
                }
                return o1.getOpportunity().getCreateTime().compareTo(o2.getOpportunity().getCreateTime());
            }
        });

        return result;
    }


    //***************************************** Domain Driven Design - save ********************************************

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

    @Override
    public boolean existByMemberIdAndUnionIdAndOpportunityId(Integer memberId, Integer unionId, Integer opportunityId) throws Exception {
        if (memberId == null || unionId == null || opportunityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return getByMemberIdAndUnionIdAndOpportunityId(memberId, unionId, opportunityId) != null;
    }

    //***************************************** Domain Driven Design - filter ******************************************

    @Override
    public List<UnionBrokerageIncome> filterByUnionId(List<UnionBrokerageIncome> incomeList, Integer unionId) throws Exception {
        if (incomeList == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionBrokerageIncome> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(incomeList)) {
            for (UnionBrokerageIncome income : incomeList) {
                if (unionId.equals(income.getUnionId())) {
                    result.add(income);
                }
            }
        }

        return result;
    }

    @Override
    public List<UnionBrokerageIncome> filterByOpportunityId(List<UnionBrokerageIncome> incomeList, Integer opportunityId) throws Exception {
        if (incomeList == null || opportunityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionBrokerageIncome> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(incomeList)) {
            for (UnionBrokerageIncome income : incomeList) {
                if (opportunityId.equals(income.getOpportunityId())) {
                    result.add(income);
                }
            }
        }

        return result;
    }

    //***************************************** Object As a Service - get **********************************************

    public UnionBrokerageIncome getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionBrokerageIncome result;
        // (1)cache
        String idKey = UnionBrokerageIncomeCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionBrokerageIncome.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

    public List<UnionBrokerageIncome> listByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokerageIncome> result;
        // (1)cache
        String busIdKey = UnionBrokerageIncomeCacheUtil.getBusIdKey(busId);
        if (redisCacheUtil.exists(busIdKey)) {
            String tempStr = redisCacheUtil.get(busIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokerageIncome.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("bus_id", busId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, busId, UnionBrokerageIncomeCacheUtil.TYPE_BUS_ID);
        return result;
    }

    public List<UnionBrokerageIncome> listByMemberId(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokerageIncome> result;
        // (1)cache
        String memberIdKey = UnionBrokerageIncomeCacheUtil.getMemberIdKey(memberId);
        if (redisCacheUtil.exists(memberIdKey)) {
            String tempStr = redisCacheUtil.get(memberIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokerageIncome.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("member_id", memberId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, memberId, UnionBrokerageIncomeCacheUtil.TYPE_MEMBER_ID);
        return result;
    }

    public List<UnionBrokerageIncome> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokerageIncome> result;
        // (1)cache
        String unionIdKey = UnionBrokerageIncomeCacheUtil.getUnionIdKey(unionId);
        if (redisCacheUtil.exists(unionIdKey)) {
            String tempStr = redisCacheUtil.get(unionIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokerageIncome.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, unionId, UnionBrokerageIncomeCacheUtil.TYPE_UNION_ID);
        return result;
    }

    public List<UnionBrokerageIncome> listByCardId(Integer cardId) throws Exception {
        if (cardId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokerageIncome> result;
        // (1)cache
        String cardIdKey = UnionBrokerageIncomeCacheUtil.getCardIdKey(cardId);
        if (redisCacheUtil.exists(cardIdKey)) {
            String tempStr = redisCacheUtil.get(cardIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokerageIncome.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("card_id", cardId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, cardId, UnionBrokerageIncomeCacheUtil.TYPE_CARD_ID);
        return result;
    }

    public List<UnionBrokerageIncome> listByOpportunityId(Integer opportunityId) throws Exception {
        if (opportunityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokerageIncome> result;
        // (1)cache
        String opportunityIdKey = UnionBrokerageIncomeCacheUtil.getOpportunityIdKey(opportunityId);
        if (redisCacheUtil.exists(opportunityIdKey)) {
            String tempStr = redisCacheUtil.get(opportunityIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokerageIncome.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("opportunity_id", opportunityId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, opportunityId, UnionBrokerageIncomeCacheUtil.TYPE_OPPORTUNITY_ID);
        return result;
    }

    //***************************************** Object As a Service - save *********************************************

    @Transactional(rollbackFor = Exception.class)
    public void save(UnionBrokerageIncome newUnionBrokerageIncome) throws Exception {
        if (newUnionBrokerageIncome == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionBrokerageIncome);
        removeCache(newUnionBrokerageIncome);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionBrokerageIncome> newUnionBrokerageIncomeList) throws Exception {
        if (newUnionBrokerageIncomeList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionBrokerageIncomeList);
        removeCache(newUnionBrokerageIncomeList);
    }

    //***************************************** Object As a Service - remove *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionBrokerageIncome unionBrokerageIncome = getById(id);
        removeCache(unionBrokerageIncome);
        // (2)remove in db logically
        UnionBrokerageIncome removeUnionBrokerageIncome = new UnionBrokerageIncome();
        removeUnionBrokerageIncome.setId(id);
        removeUnionBrokerageIncome.setDelStatus(CommonConstant.DEL_STATUS_YES);
        updateById(removeUnionBrokerageIncome);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionBrokerageIncome> unionBrokerageIncomeList = new ArrayList<>();
        for (Integer id : idList) {
            UnionBrokerageIncome unionBrokerageIncome = getById(id);
            unionBrokerageIncomeList.add(unionBrokerageIncome);
        }
        removeCache(unionBrokerageIncomeList);
        // (2)remove in db logically
        List<UnionBrokerageIncome> removeUnionBrokerageIncomeList = new ArrayList<>();
        for (Integer id : idList) {
            UnionBrokerageIncome removeUnionBrokerageIncome = new UnionBrokerageIncome();
            removeUnionBrokerageIncome.setId(id);
            removeUnionBrokerageIncome.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionBrokerageIncomeList.add(removeUnionBrokerageIncome);
        }
        updateBatchById(removeUnionBrokerageIncomeList);
    }

    //***************************************** Object As a Service - update *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void update(UnionBrokerageIncome updateUnionBrokerageIncome) throws Exception {
        if (updateUnionBrokerageIncome == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionBrokerageIncome.getId();
        UnionBrokerageIncome unionBrokerageIncome = getById(id);
        removeCache(unionBrokerageIncome);
        // (2)update db
        updateById(updateUnionBrokerageIncome);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionBrokerageIncome> updateUnionBrokerageIncomeList) throws Exception {
        if (updateUnionBrokerageIncomeList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionBrokerageIncome updateUnionBrokerageIncome : updateUnionBrokerageIncomeList) {
            idList.add(updateUnionBrokerageIncome.getId());
        }
        List<UnionBrokerageIncome> unionBrokerageIncomeList = new ArrayList<>();
        for (Integer id : idList) {
            UnionBrokerageIncome unionBrokerageIncome = getById(id);
            unionBrokerageIncomeList.add(unionBrokerageIncome);
        }
        removeCache(unionBrokerageIncomeList);
        // (2)update db
        updateBatchById(updateUnionBrokerageIncomeList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionBrokerageIncome newUnionBrokerageIncome, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionBrokerageIncomeCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionBrokerageIncome);
    }

    private void setCache(List<UnionBrokerageIncome> newUnionBrokerageIncomeList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionBrokerageIncomeCacheUtil.TYPE_BUS_ID:
                foreignIdKey = UnionBrokerageIncomeCacheUtil.getBusIdKey(foreignId);
                break;
            case UnionBrokerageIncomeCacheUtil.TYPE_MEMBER_ID:
                foreignIdKey = UnionBrokerageIncomeCacheUtil.getMemberIdKey(foreignId);
                break;
            case UnionBrokerageIncomeCacheUtil.TYPE_UNION_ID:
                foreignIdKey = UnionBrokerageIncomeCacheUtil.getUnionIdKey(foreignId);
                break;
            case UnionBrokerageIncomeCacheUtil.TYPE_CARD_ID:
                foreignIdKey = UnionBrokerageIncomeCacheUtil.getCardIdKey(foreignId);
                break;
            case UnionBrokerageIncomeCacheUtil.TYPE_OPPORTUNITY_ID:
                foreignIdKey = UnionBrokerageIncomeCacheUtil.getOpportunityIdKey(foreignId);
                break;
            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionBrokerageIncomeList);
        }
    }

    private void removeCache(UnionBrokerageIncome unionBrokerageIncome) {
        if (unionBrokerageIncome == null) {
            return;
        }
        Integer id = unionBrokerageIncome.getId();
        String idKey = UnionBrokerageIncomeCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer busId = unionBrokerageIncome.getBusId();
        if (busId != null) {
            String busIdKey = UnionBrokerageIncomeCacheUtil.getBusIdKey(busId);
            redisCacheUtil.remove(busIdKey);
        }

        Integer memberId = unionBrokerageIncome.getMemberId();
        if (memberId != null) {
            String memberIdKey = UnionBrokerageIncomeCacheUtil.getMemberIdKey(memberId);
            redisCacheUtil.remove(memberIdKey);
        }

        Integer unionId = unionBrokerageIncome.getUnionId();
        if (unionId != null) {
            String unionIdKey = UnionBrokerageIncomeCacheUtil.getUnionIdKey(unionId);
            redisCacheUtil.remove(unionIdKey);
        }

        Integer cardId = unionBrokerageIncome.getCardId();
        if (cardId != null) {
            String cardIdKey = UnionBrokerageIncomeCacheUtil.getCardIdKey(cardId);
            redisCacheUtil.remove(cardIdKey);
        }

        Integer opportunityId = unionBrokerageIncome.getOpportunityId();
        if (opportunityId != null) {
            String opportunityIdKey = UnionBrokerageIncomeCacheUtil.getOpportunityIdKey(opportunityId);
            redisCacheUtil.remove(opportunityIdKey);
        }
    }

    private void removeCache(List<UnionBrokerageIncome> unionBrokerageIncomeList) {
        if (ListUtil.isEmpty(unionBrokerageIncomeList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionBrokerageIncome unionBrokerageIncome : unionBrokerageIncomeList) {
            idList.add(unionBrokerageIncome.getId());
        }
        List<String> idKeyList = UnionBrokerageIncomeCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

        List<String> busIdKeyList = getForeignIdKeyList(unionBrokerageIncomeList, UnionBrokerageIncomeCacheUtil.TYPE_BUS_ID);
        if (ListUtil.isNotEmpty(busIdKeyList)) {
            redisCacheUtil.remove(busIdKeyList);
        }

        List<String> memberIdKeyList = getForeignIdKeyList(unionBrokerageIncomeList, UnionBrokerageIncomeCacheUtil.TYPE_MEMBER_ID);
        if (ListUtil.isNotEmpty(memberIdKeyList)) {
            redisCacheUtil.remove(memberIdKeyList);
        }

        List<String> unionIdKeyList = getForeignIdKeyList(unionBrokerageIncomeList, UnionBrokerageIncomeCacheUtil.TYPE_UNION_ID);
        if (ListUtil.isNotEmpty(unionIdKeyList)) {
            redisCacheUtil.remove(unionIdKeyList);
        }

        List<String> cardIdKeyList = getForeignIdKeyList(unionBrokerageIncomeList, UnionBrokerageIncomeCacheUtil.TYPE_CARD_ID);
        if (ListUtil.isNotEmpty(cardIdKeyList)) {
            redisCacheUtil.remove(cardIdKeyList);
        }

        List<String> opportunityIdKeyList = getForeignIdKeyList(unionBrokerageIncomeList, UnionBrokerageIncomeCacheUtil.TYPE_OPPORTUNITY_ID);
        if (ListUtil.isNotEmpty(opportunityIdKeyList)) {
            redisCacheUtil.remove(opportunityIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionBrokerageIncome> unionBrokerageIncomeList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionBrokerageIncomeCacheUtil.TYPE_BUS_ID:
                for (UnionBrokerageIncome unionBrokerageIncome : unionBrokerageIncomeList) {
                    Integer busId = unionBrokerageIncome.getBusId();
                    if (busId != null) {
                        String busIdKey = UnionBrokerageIncomeCacheUtil.getBusIdKey(busId);
                        result.add(busIdKey);
                    }
                }
                break;
            case UnionBrokerageIncomeCacheUtil.TYPE_MEMBER_ID:
                for (UnionBrokerageIncome unionBrokerageIncome : unionBrokerageIncomeList) {
                    Integer memberId = unionBrokerageIncome.getMemberId();
                    if (memberId != null) {
                        String memberIdKey = UnionBrokerageIncomeCacheUtil.getMemberIdKey(memberId);
                        result.add(memberIdKey);
                    }
                }
                break;
            case UnionBrokerageIncomeCacheUtil.TYPE_UNION_ID:
                for (UnionBrokerageIncome unionBrokerageIncome : unionBrokerageIncomeList) {
                    Integer unionId = unionBrokerageIncome.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = UnionBrokerageIncomeCacheUtil.getUnionIdKey(unionId);
                        result.add(unionIdKey);
                    }
                }
                break;
            case UnionBrokerageIncomeCacheUtil.TYPE_CARD_ID:
                for (UnionBrokerageIncome unionBrokerageIncome : unionBrokerageIncomeList) {
                    Integer cardId = unionBrokerageIncome.getCardId();
                    if (cardId != null) {
                        String cardIdKey = UnionBrokerageIncomeCacheUtil.getCardIdKey(cardId);
                        result.add(cardIdKey);
                    }
                }
                break;
            case UnionBrokerageIncomeCacheUtil.TYPE_OPPORTUNITY_ID:
                for (UnionBrokerageIncome unionBrokerageIncome : unionBrokerageIncomeList) {
                    Integer opportunityId = unionBrokerageIncome.getOpportunityId();
                    if (opportunityId != null) {
                        String opportunityIdKey = UnionBrokerageIncomeCacheUtil.getOpportunityIdKey(opportunityId);
                        result.add(opportunityIdKey);
                    }
                }
                break;
            default:
                break;
        }
        return result;
    }

}