package com.gt.union.opportunity.brokerage.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.BigDecimalUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.opportunity.brokerage.dao.IUnionBrokerageIncomeDao;
import com.gt.union.opportunity.brokerage.entity.UnionBrokerageIncome;
import com.gt.union.opportunity.brokerage.service.IUnionBrokerageIncomeService;
import com.gt.union.opportunity.brokerage.util.UnionBrokerageIncomeCacheUtil;
import com.gt.union.opportunity.main.entity.UnionOpportunity;
import com.gt.union.opportunity.main.service.IUnionOpportunityService;
import com.gt.union.union.member.entity.UnionMember;
import com.gt.union.union.member.service.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 佣金收入 服务实现类
 *
 * @author linweicong
 * @version 2017-11-24 09:21:28
 */
@Service
public class UnionBrokerageIncomeServiceImpl implements IUnionBrokerageIncomeService {
    @Autowired
    private IUnionBrokerageIncomeDao unionBrokerageIncomeDao;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IUnionOpportunityService unionOpportunityService;

    @Autowired
    private IUnionMemberService unionMemberService;

    //********************************************* Base On Business - get *********************************************

    @Override
    public UnionBrokerageIncome getValidByUnionIdAndMemberIdAndOpportunityId(Integer unionId, Integer memberId, Integer opportunityId) throws Exception {
        if (unionId == null || memberId == null || opportunityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .eq("member_id", memberId)
                .eq("opportunity_id", opportunityId);

        return unionBrokerageIncomeDao.selectOne(entityWrapper);
    }

    //********************************************* Base On Business - list ********************************************

    @Override
    public List<UnionBrokerageIncome> listValidByBusIdAndType(Integer busId, Integer type) throws Exception {
        if (busId == null || type == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId)
                .eq("type", type);

        return unionBrokerageIncomeDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokerageIncome> listValidByBusIdAndTypeAndMemberIdList(Integer busId, Integer type, List<Integer> memberIdList) throws Exception {
        if (busId == null || type == null || memberIdList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId)
                .eq("type", type)
                .in("member_id", memberIdList);

        return unionBrokerageIncomeDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokerageIncome> listValidByBusIdAndUnionIdAndType(Integer busId, Integer unionId, Integer type) throws Exception {
        if (busId == null || unionId == null || type == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId)
                .eq("union_id", unionId)
                .eq("type", type);

        return unionBrokerageIncomeDao.selectList(entityWrapper);
    }

    @Override
    public Page pageBrokerageOpportunityVOByBusId(
            Page page, Integer busId, Integer optUnionId, Integer optToMemberId,
            Integer optIsClose, String optClientName, String optClientPhone) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	获取商家所有有效的member
        List<UnionMember> memberList = unionMemberService.listByBusId(busId);
        List<Integer> fromMemberIdList = unionMemberService.getIdList(memberList);
        // （2）分页查询结果
        EntityWrapper<UnionOpportunity> opportunityEntityWrapper = new EntityWrapper<>();
        opportunityEntityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .in("from_member_id", fromMemberIdList)
                .eq(optUnionId != null, "union_id", optUnionId)
                .eq(optToMemberId != null, "to_member_id", optToMemberId)
                .eq(optIsClose != null, "is_close", optIsClose)
                .like(StringUtil.isNotEmpty(optClientName), "client_name", optClientName)
                .like(StringUtil.isNotEmpty(optClientPhone), "client_phone", optClientPhone)
                .orderBy("is_close ASC, create_time", true);

        Page result = unionOpportunityService.pageSupport(page, opportunityEntityWrapper);
        List<UnionOpportunity> resultListDate = result.getRecords();
        result.setRecords(unionOpportunityService.listBrokerageOpportunityVO(resultListDate));

        return result;
    }


    //********************************************* Base On Business - save ********************************************

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    //********************************************* Base On Business - other *******************************************

    @Override
    public Double sumMoneyByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("bus_id", busId);
        List<UnionBrokerageIncome> incomeList = unionBrokerageIncomeDao.selectList(entityWrapper);

        BigDecimal result = BigDecimal.ZERO;
        if (ListUtil.isNotEmpty(incomeList)) {
            for (UnionBrokerageIncome income : incomeList) {
                result = BigDecimalUtil.add(result, income.getMoney());
            }
        }

        return result.doubleValue();
    }

    @Override
    public Double sumValidMoneyByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId);
        List<UnionBrokerageIncome> incomeList = unionBrokerageIncomeDao.selectList(entityWrapper);

        BigDecimal result = BigDecimal.ZERO;
        if (ListUtil.isNotEmpty(incomeList)) {
            for (UnionBrokerageIncome income : incomeList) {
                result = BigDecimalUtil.add(result, income.getMoney());
            }
        }

        return result.doubleValue();
    }

    @Override
    public Double sumValidMoneyByBusIdAndType(Integer busId, Integer type) throws Exception {
        if (busId == null || type == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId)
                .eq("type", type);
        List<UnionBrokerageIncome> incomeList = unionBrokerageIncomeDao.selectList(entityWrapper);

        BigDecimal result = BigDecimal.ZERO;
        if (ListUtil.isNotEmpty(incomeList)) {
            for (UnionBrokerageIncome income : incomeList) {
                result = BigDecimalUtil.add(result, income.getMoney());
            }
        }

        return result.doubleValue();
    }

    @Override
    public Double sumValidMoneyByBusIdAndTypeAndMemberIdList(Integer busId, Integer type, List<Integer> memberIdList) throws Exception {
        if (busId == null || type == null || memberIdList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId)
                .eq("type", type)
                .in("member_id", memberIdList);
        List<UnionBrokerageIncome> incomeList = unionBrokerageIncomeDao.selectList(entityWrapper);

        BigDecimal result = BigDecimal.ZERO;
        if (ListUtil.isNotEmpty(incomeList)) {
            for (UnionBrokerageIncome income : incomeList) {
                result = BigDecimalUtil.add(result, income.getMoney());
            }
        }

        return result.doubleValue();
    }

    @Override
    public Double sumValidMoneyByBusIdAndUnionIdAndType(Integer busId, Integer unionId, Integer type) throws Exception {
        if (busId == null || unionId == null || type == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId)
                .eq("type", type)
                .eq("union_id", unionId);
        List<UnionBrokerageIncome> incomeList = unionBrokerageIncomeDao.selectList(entityWrapper);

        BigDecimal result = BigDecimal.ZERO;
        if (ListUtil.isNotEmpty(incomeList)) {
            for (UnionBrokerageIncome income : incomeList) {
                result = BigDecimalUtil.add(result, income.getMoney());
            }
        }

        return result.doubleValue();
    }

    @Override
    public boolean existValidByUnionIdAndMemberIdAndOpportunityId(Integer unionId, Integer memberId, Integer opportunityId) throws Exception {
        if (unionId == null || memberId == null || opportunityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .eq("member_id", memberId)
                .eq("opportunity_id", opportunityId);

        return unionBrokerageIncomeDao.selectOne(entityWrapper) != null;
    }

    //********************************************* Base On Business - filter ******************************************

    @Override
    public List<UnionBrokerageIncome> filterByDelStatus(List<UnionBrokerageIncome> unionBrokerageIncomeList, Integer delStatus) throws Exception {
        if (delStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionBrokerageIncome> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionBrokerageIncomeList)) {
            for (UnionBrokerageIncome unionBrokerageIncome : unionBrokerageIncomeList) {
                if (delStatus.equals(unionBrokerageIncome.getDelStatus())) {
                    result.add(unionBrokerageIncome);
                }
            }
        }

        return result;
    }

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

    @Override
    public List<UnionBrokerageIncome> filterByType(List<UnionBrokerageIncome> incomeList, Integer type) throws Exception {
        if (incomeList == null || type == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionBrokerageIncome> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(incomeList)) {
            for (UnionBrokerageIncome income : incomeList) {
                if (type.equals(income.getType())) {
                    result.add(income);
                }
            }
        }

        return result;
    }

    @Override
    public List<UnionBrokerageIncome> filterByMemberIdList(List<UnionBrokerageIncome> incomeList, List<Integer> memberIdList) throws Exception {
        if (incomeList == null || memberIdList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionBrokerageIncome> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(memberIdList)) {
            for (UnionBrokerageIncome income : incomeList) {
                if (memberIdList.contains(income.getMemberId())) {
                    result.add(income);
                }
            }
        }

        return result;
    }

    //********************************************* Object As a Service - get ******************************************

    @Override
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
        result = unionBrokerageIncomeDao.selectById(id);
        setCache(result, id);
        return result;
    }

    @Override
    public UnionBrokerageIncome getValidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionBrokerageIncome result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_NO == result.getDelStatus() ? result : null;
    }

    @Override
    public UnionBrokerageIncome getInvalidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionBrokerageIncome result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_YES == result.getDelStatus() ? result : null;
    }

    //********************************************* Object As a Service - list *****************************************

    @Override
    public List<Integer> getIdList(List<UnionBrokerageIncome> unionBrokerageIncomeList) throws Exception {
        if (unionBrokerageIncomeList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<Integer> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionBrokerageIncomeList)) {
            for (UnionBrokerageIncome unionBrokerageIncome : unionBrokerageIncomeList) {
                result.add(unionBrokerageIncome.getId());
            }
        }

        return result;
    }

    @Override
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
        entityWrapper.eq("bus_id", busId);
        result = unionBrokerageIncomeDao.selectList(entityWrapper);
        setCache(result, busId, UnionBrokerageIncomeCacheUtil.TYPE_BUS_ID);
        return result;
    }

    @Override
    public List<UnionBrokerageIncome> listValidByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokerageIncome> result;
        // (1)cache
        String validBusIdKey = UnionBrokerageIncomeCacheUtil.getValidBusIdKey(busId);
        if (redisCacheUtil.exists(validBusIdKey)) {
            String tempStr = redisCacheUtil.get(validBusIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokerageIncome.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId);
        result = unionBrokerageIncomeDao.selectList(entityWrapper);
        setValidCache(result, busId, UnionBrokerageIncomeCacheUtil.TYPE_BUS_ID);
        return result;
    }

    @Override
    public List<UnionBrokerageIncome> listInvalidByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokerageIncome> result;
        // (1)cache
        String invalidBusIdKey = UnionBrokerageIncomeCacheUtil.getInvalidBusIdKey(busId);
        if (redisCacheUtil.exists(invalidBusIdKey)) {
            String tempStr = redisCacheUtil.get(invalidBusIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokerageIncome.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("bus_id", busId);
        result = unionBrokerageIncomeDao.selectList(entityWrapper);
        setInvalidCache(result, busId, UnionBrokerageIncomeCacheUtil.TYPE_BUS_ID);
        return result;
    }

    @Override
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
        entityWrapper.eq("member_id", memberId);
        result = unionBrokerageIncomeDao.selectList(entityWrapper);
        setCache(result, memberId, UnionBrokerageIncomeCacheUtil.TYPE_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionBrokerageIncome> listValidByMemberId(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokerageIncome> result;
        // (1)cache
        String validMemberIdKey = UnionBrokerageIncomeCacheUtil.getValidMemberIdKey(memberId);
        if (redisCacheUtil.exists(validMemberIdKey)) {
            String tempStr = redisCacheUtil.get(validMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokerageIncome.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("member_id", memberId);
        result = unionBrokerageIncomeDao.selectList(entityWrapper);
        setValidCache(result, memberId, UnionBrokerageIncomeCacheUtil.TYPE_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionBrokerageIncome> listInvalidByMemberId(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokerageIncome> result;
        // (1)cache
        String invalidMemberIdKey = UnionBrokerageIncomeCacheUtil.getInvalidMemberIdKey(memberId);
        if (redisCacheUtil.exists(invalidMemberIdKey)) {
            String tempStr = redisCacheUtil.get(invalidMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokerageIncome.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("member_id", memberId);
        result = unionBrokerageIncomeDao.selectList(entityWrapper);
        setInvalidCache(result, memberId, UnionBrokerageIncomeCacheUtil.TYPE_MEMBER_ID);
        return result;
    }

    @Override
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
        entityWrapper.eq("union_id", unionId);
        result = unionBrokerageIncomeDao.selectList(entityWrapper);
        setCache(result, unionId, UnionBrokerageIncomeCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
    public List<UnionBrokerageIncome> listValidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokerageIncome> result;
        // (1)cache
        String validUnionIdKey = UnionBrokerageIncomeCacheUtil.getValidUnionIdKey(unionId);
        if (redisCacheUtil.exists(validUnionIdKey)) {
            String tempStr = redisCacheUtil.get(validUnionIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokerageIncome.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId);
        result = unionBrokerageIncomeDao.selectList(entityWrapper);
        setValidCache(result, unionId, UnionBrokerageIncomeCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
    public List<UnionBrokerageIncome> listInvalidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokerageIncome> result;
        // (1)cache
        String invalidUnionIdKey = UnionBrokerageIncomeCacheUtil.getInvalidUnionIdKey(unionId);
        if (redisCacheUtil.exists(invalidUnionIdKey)) {
            String tempStr = redisCacheUtil.get(invalidUnionIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokerageIncome.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("union_id", unionId);
        result = unionBrokerageIncomeDao.selectList(entityWrapper);
        setInvalidCache(result, unionId, UnionBrokerageIncomeCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
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
        entityWrapper.eq("card_id", cardId);
        result = unionBrokerageIncomeDao.selectList(entityWrapper);
        setCache(result, cardId, UnionBrokerageIncomeCacheUtil.TYPE_CARD_ID);
        return result;
    }

    @Override
    public List<UnionBrokerageIncome> listValidByCardId(Integer cardId) throws Exception {
        if (cardId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokerageIncome> result;
        // (1)cache
        String validCardIdKey = UnionBrokerageIncomeCacheUtil.getValidCardIdKey(cardId);
        if (redisCacheUtil.exists(validCardIdKey)) {
            String tempStr = redisCacheUtil.get(validCardIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokerageIncome.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("card_id", cardId);
        result = unionBrokerageIncomeDao.selectList(entityWrapper);
        setValidCache(result, cardId, UnionBrokerageIncomeCacheUtil.TYPE_CARD_ID);
        return result;
    }

    @Override
    public List<UnionBrokerageIncome> listInvalidByCardId(Integer cardId) throws Exception {
        if (cardId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokerageIncome> result;
        // (1)cache
        String invalidCardIdKey = UnionBrokerageIncomeCacheUtil.getInvalidCardIdKey(cardId);
        if (redisCacheUtil.exists(invalidCardIdKey)) {
            String tempStr = redisCacheUtil.get(invalidCardIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokerageIncome.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("card_id", cardId);
        result = unionBrokerageIncomeDao.selectList(entityWrapper);
        setInvalidCache(result, cardId, UnionBrokerageIncomeCacheUtil.TYPE_CARD_ID);
        return result;
    }

    @Override
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
        entityWrapper.eq("opportunity_id", opportunityId);
        result = unionBrokerageIncomeDao.selectList(entityWrapper);
        setCache(result, opportunityId, UnionBrokerageIncomeCacheUtil.TYPE_OPPORTUNITY_ID);
        return result;
    }

    @Override
    public List<UnionBrokerageIncome> listValidByOpportunityId(Integer opportunityId) throws Exception {
        if (opportunityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokerageIncome> result;
        // (1)cache
        String validOpportunityIdKey = UnionBrokerageIncomeCacheUtil.getValidOpportunityIdKey(opportunityId);
        if (redisCacheUtil.exists(validOpportunityIdKey)) {
            String tempStr = redisCacheUtil.get(validOpportunityIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokerageIncome.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("opportunity_id", opportunityId);
        result = unionBrokerageIncomeDao.selectList(entityWrapper);
        setValidCache(result, opportunityId, UnionBrokerageIncomeCacheUtil.TYPE_OPPORTUNITY_ID);
        return result;
    }

    @Override
    public List<UnionBrokerageIncome> listInvalidByOpportunityId(Integer opportunityId) throws Exception {
        if (opportunityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokerageIncome> result;
        // (1)cache
        String invalidOpportunityIdKey = UnionBrokerageIncomeCacheUtil.getInvalidOpportunityIdKey(opportunityId);
        if (redisCacheUtil.exists(invalidOpportunityIdKey)) {
            String tempStr = redisCacheUtil.get(invalidOpportunityIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokerageIncome.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("opportunity_id", opportunityId);
        result = unionBrokerageIncomeDao.selectList(entityWrapper);
        setInvalidCache(result, opportunityId, UnionBrokerageIncomeCacheUtil.TYPE_OPPORTUNITY_ID);
        return result;
    }

    @Override
    public List<UnionBrokerageIncome> listByIdList(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionBrokerageIncome> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(idList)) {
            for (Integer id : idList) {
                result.add(getById(id));
            }
        }

        return result;
    }

    @Override
    public Page pageSupport(Page page, EntityWrapper<UnionBrokerageIncome> entityWrapper) throws Exception {
        if (page == null || entityWrapper == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionBrokerageIncomeDao.selectPage(page, entityWrapper);
    }
    //********************************************* Object As a Service - save *****************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(UnionBrokerageIncome newUnionBrokerageIncome) throws Exception {
        if (newUnionBrokerageIncome == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        unionBrokerageIncomeDao.insert(newUnionBrokerageIncome);
        removeCache(newUnionBrokerageIncome);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionBrokerageIncome> newUnionBrokerageIncomeList) throws Exception {
        if (newUnionBrokerageIncomeList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        unionBrokerageIncomeDao.insertBatch(newUnionBrokerageIncomeList);
        removeCache(newUnionBrokerageIncomeList);
    }

    //********************************************* Object As a Service - remove ***************************************

    @Override
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
        unionBrokerageIncomeDao.updateById(removeUnionBrokerageIncome);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionBrokerageIncome> unionBrokerageIncomeList = listByIdList(idList);
        removeCache(unionBrokerageIncomeList);
        // (2)remove in db logically
        List<UnionBrokerageIncome> removeUnionBrokerageIncomeList = new ArrayList<>();
        for (Integer id : idList) {
            UnionBrokerageIncome removeUnionBrokerageIncome = new UnionBrokerageIncome();
            removeUnionBrokerageIncome.setId(id);
            removeUnionBrokerageIncome.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionBrokerageIncomeList.add(removeUnionBrokerageIncome);
        }
        unionBrokerageIncomeDao.updateBatchById(removeUnionBrokerageIncomeList);
    }

    //********************************************* Object As a Service - update ***************************************

    @Override
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
        unionBrokerageIncomeDao.updateById(updateUnionBrokerageIncome);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionBrokerageIncome> updateUnionBrokerageIncomeList) throws Exception {
        if (updateUnionBrokerageIncomeList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = getIdList(updateUnionBrokerageIncomeList);
        List<UnionBrokerageIncome> unionBrokerageIncomeList = listByIdList(idList);
        removeCache(unionBrokerageIncomeList);
        // (2)update db
        unionBrokerageIncomeDao.updateBatchById(updateUnionBrokerageIncomeList);
    }

    //********************************************* Object As a Service - cache support ********************************

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

    private void setValidCache(List<UnionBrokerageIncome> newUnionBrokerageIncomeList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String validForeignIdKey = null;
        switch (foreignIdType) {
            case UnionBrokerageIncomeCacheUtil.TYPE_BUS_ID:
                validForeignIdKey = UnionBrokerageIncomeCacheUtil.getValidBusIdKey(foreignId);
                break;
            case UnionBrokerageIncomeCacheUtil.TYPE_MEMBER_ID:
                validForeignIdKey = UnionBrokerageIncomeCacheUtil.getValidMemberIdKey(foreignId);
                break;
            case UnionBrokerageIncomeCacheUtil.TYPE_UNION_ID:
                validForeignIdKey = UnionBrokerageIncomeCacheUtil.getValidUnionIdKey(foreignId);
                break;
            case UnionBrokerageIncomeCacheUtil.TYPE_CARD_ID:
                validForeignIdKey = UnionBrokerageIncomeCacheUtil.getValidCardIdKey(foreignId);
                break;
            case UnionBrokerageIncomeCacheUtil.TYPE_OPPORTUNITY_ID:
                validForeignIdKey = UnionBrokerageIncomeCacheUtil.getValidOpportunityIdKey(foreignId);
                break;

            default:
                break;
        }
        if (validForeignIdKey != null) {
            redisCacheUtil.set(validForeignIdKey, newUnionBrokerageIncomeList);
        }
    }

    private void setInvalidCache(List<UnionBrokerageIncome> newUnionBrokerageIncomeList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String invalidForeignIdKey = null;
        switch (foreignIdType) {
            case UnionBrokerageIncomeCacheUtil.TYPE_BUS_ID:
                invalidForeignIdKey = UnionBrokerageIncomeCacheUtil.getInvalidBusIdKey(foreignId);
                break;
            case UnionBrokerageIncomeCacheUtil.TYPE_MEMBER_ID:
                invalidForeignIdKey = UnionBrokerageIncomeCacheUtil.getInvalidMemberIdKey(foreignId);
                break;
            case UnionBrokerageIncomeCacheUtil.TYPE_UNION_ID:
                invalidForeignIdKey = UnionBrokerageIncomeCacheUtil.getInvalidUnionIdKey(foreignId);
                break;
            case UnionBrokerageIncomeCacheUtil.TYPE_CARD_ID:
                invalidForeignIdKey = UnionBrokerageIncomeCacheUtil.getInvalidCardIdKey(foreignId);
                break;
            case UnionBrokerageIncomeCacheUtil.TYPE_OPPORTUNITY_ID:
                invalidForeignIdKey = UnionBrokerageIncomeCacheUtil.getInvalidOpportunityIdKey(foreignId);
                break;

            default:
                break;
        }
        if (invalidForeignIdKey != null) {
            redisCacheUtil.set(invalidForeignIdKey, newUnionBrokerageIncomeList);
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

            String validBusIdKey = UnionBrokerageIncomeCacheUtil.getValidBusIdKey(busId);
            redisCacheUtil.remove(validBusIdKey);

            String invalidBusIdKey = UnionBrokerageIncomeCacheUtil.getInvalidBusIdKey(busId);
            redisCacheUtil.remove(invalidBusIdKey);
        }

        Integer memberId = unionBrokerageIncome.getMemberId();
        if (memberId != null) {
            String memberIdKey = UnionBrokerageIncomeCacheUtil.getMemberIdKey(memberId);
            redisCacheUtil.remove(memberIdKey);

            String validMemberIdKey = UnionBrokerageIncomeCacheUtil.getValidMemberIdKey(memberId);
            redisCacheUtil.remove(validMemberIdKey);

            String invalidMemberIdKey = UnionBrokerageIncomeCacheUtil.getInvalidMemberIdKey(memberId);
            redisCacheUtil.remove(invalidMemberIdKey);
        }

        Integer unionId = unionBrokerageIncome.getUnionId();
        if (unionId != null) {
            String unionIdKey = UnionBrokerageIncomeCacheUtil.getUnionIdKey(unionId);
            redisCacheUtil.remove(unionIdKey);

            String validUnionIdKey = UnionBrokerageIncomeCacheUtil.getValidUnionIdKey(unionId);
            redisCacheUtil.remove(validUnionIdKey);

            String invalidUnionIdKey = UnionBrokerageIncomeCacheUtil.getInvalidUnionIdKey(unionId);
            redisCacheUtil.remove(invalidUnionIdKey);
        }

        Integer cardId = unionBrokerageIncome.getCardId();
        if (cardId != null) {
            String cardIdKey = UnionBrokerageIncomeCacheUtil.getCardIdKey(cardId);
            redisCacheUtil.remove(cardIdKey);

            String validCardIdKey = UnionBrokerageIncomeCacheUtil.getValidCardIdKey(cardId);
            redisCacheUtil.remove(validCardIdKey);

            String invalidCardIdKey = UnionBrokerageIncomeCacheUtil.getInvalidCardIdKey(cardId);
            redisCacheUtil.remove(invalidCardIdKey);
        }

        Integer opportunityId = unionBrokerageIncome.getOpportunityId();
        if (opportunityId != null) {
            String opportunityIdKey = UnionBrokerageIncomeCacheUtil.getOpportunityIdKey(opportunityId);
            redisCacheUtil.remove(opportunityIdKey);

            String validOpportunityIdKey = UnionBrokerageIncomeCacheUtil.getValidOpportunityIdKey(opportunityId);
            redisCacheUtil.remove(validOpportunityIdKey);

            String invalidOpportunityIdKey = UnionBrokerageIncomeCacheUtil.getInvalidOpportunityIdKey(opportunityId);
            redisCacheUtil.remove(invalidOpportunityIdKey);
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

                        String validBusIdKey = UnionBrokerageIncomeCacheUtil.getValidBusIdKey(busId);
                        result.add(validBusIdKey);

                        String invalidBusIdKey = UnionBrokerageIncomeCacheUtil.getInvalidBusIdKey(busId);
                        result.add(invalidBusIdKey);
                    }
                }
                break;
            case UnionBrokerageIncomeCacheUtil.TYPE_MEMBER_ID:
                for (UnionBrokerageIncome unionBrokerageIncome : unionBrokerageIncomeList) {
                    Integer memberId = unionBrokerageIncome.getMemberId();
                    if (memberId != null) {
                        String memberIdKey = UnionBrokerageIncomeCacheUtil.getMemberIdKey(memberId);
                        result.add(memberIdKey);

                        String validMemberIdKey = UnionBrokerageIncomeCacheUtil.getValidMemberIdKey(memberId);
                        result.add(validMemberIdKey);

                        String invalidMemberIdKey = UnionBrokerageIncomeCacheUtil.getInvalidMemberIdKey(memberId);
                        result.add(invalidMemberIdKey);
                    }
                }
                break;
            case UnionBrokerageIncomeCacheUtil.TYPE_UNION_ID:
                for (UnionBrokerageIncome unionBrokerageIncome : unionBrokerageIncomeList) {
                    Integer unionId = unionBrokerageIncome.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = UnionBrokerageIncomeCacheUtil.getUnionIdKey(unionId);
                        result.add(unionIdKey);

                        String validUnionIdKey = UnionBrokerageIncomeCacheUtil.getValidUnionIdKey(unionId);
                        result.add(validUnionIdKey);

                        String invalidUnionIdKey = UnionBrokerageIncomeCacheUtil.getInvalidUnionIdKey(unionId);
                        result.add(invalidUnionIdKey);
                    }
                }
                break;
            case UnionBrokerageIncomeCacheUtil.TYPE_CARD_ID:
                for (UnionBrokerageIncome unionBrokerageIncome : unionBrokerageIncomeList) {
                    Integer cardId = unionBrokerageIncome.getCardId();
                    if (cardId != null) {
                        String cardIdKey = UnionBrokerageIncomeCacheUtil.getCardIdKey(cardId);
                        result.add(cardIdKey);

                        String validCardIdKey = UnionBrokerageIncomeCacheUtil.getValidCardIdKey(cardId);
                        result.add(validCardIdKey);

                        String invalidCardIdKey = UnionBrokerageIncomeCacheUtil.getInvalidCardIdKey(cardId);
                        result.add(invalidCardIdKey);
                    }
                }
                break;
            case UnionBrokerageIncomeCacheUtil.TYPE_OPPORTUNITY_ID:
                for (UnionBrokerageIncome unionBrokerageIncome : unionBrokerageIncomeList) {
                    Integer opportunityId = unionBrokerageIncome.getOpportunityId();
                    if (opportunityId != null) {
                        String opportunityIdKey = UnionBrokerageIncomeCacheUtil.getOpportunityIdKey(opportunityId);
                        result.add(opportunityIdKey);

                        String validOpportunityIdKey = UnionBrokerageIncomeCacheUtil.getValidOpportunityIdKey(opportunityId);
                        result.add(validOpportunityIdKey);

                        String invalidOpportunityIdKey = UnionBrokerageIncomeCacheUtil.getInvalidOpportunityIdKey(opportunityId);
                        result.add(invalidOpportunityIdKey);
                    }
                }
                break;

            default:
                break;
        }
        return result;
    }

}