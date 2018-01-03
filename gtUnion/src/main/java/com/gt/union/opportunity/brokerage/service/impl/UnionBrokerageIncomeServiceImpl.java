package com.gt.union.opportunity.brokerage.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.opportunity.brokerage.dao.IUnionBrokerageIncomeDao;
import com.gt.union.opportunity.brokerage.entity.UnionBrokerageIncome;
import com.gt.union.opportunity.brokerage.service.IUnionBrokerageIncomeService;
import com.gt.union.opportunity.main.constant.OpportunityConstant;
import com.gt.union.opportunity.main.entity.UnionOpportunity;
import com.gt.union.opportunity.main.service.IUnionOpportunityService;
import com.gt.union.union.member.entity.UnionMember;
import com.gt.union.union.member.service.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
                .eq("accept_status", OpportunityConstant.ACCEPT_STATUS_CONFIRMED)
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

        entityWrapper.setSqlSelect("IfNull(SUM(money),0) moneySum");

        Map<String, Object> resultMap = unionBrokerageIncomeDao.selectMap(entityWrapper);
        return Double.valueOf(resultMap.get("moneySum").toString());
    }

    @Override
    public Double sumValidMoneyByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId);

        entityWrapper.setSqlSelect("IfNull(SUM(money),0) moneySum");

        Map<String, Object> resultMap = unionBrokerageIncomeDao.selectMap(entityWrapper);
        return Double.valueOf(resultMap.get("moneySum").toString());
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

        entityWrapper.setSqlSelect("IfNull(SUM(money),0) moneySum");

        Map<String, Object> resultMap = unionBrokerageIncomeDao.selectMap(entityWrapper);
        return Double.valueOf(resultMap.get("moneySum").toString());
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

        entityWrapper.setSqlSelect("IfNull(SUM(money),0) moneySum");

        Map<String, Object> resultMap = unionBrokerageIncomeDao.selectMap(entityWrapper);
        return Double.valueOf(resultMap.get("moneySum").toString());
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

        entityWrapper.setSqlSelect("IfNull(SUM(money),0) moneySum");

        Map<String, Object> resultMap = unionBrokerageIncomeDao.selectMap(entityWrapper);
        return Double.valueOf(resultMap.get("moneySum").toString());
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

        return unionBrokerageIncomeDao.selectCount(entityWrapper) > 0;
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

        return unionBrokerageIncomeDao.selectById(id);
    }

    @Override
    public UnionBrokerageIncome getValidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("id", id);

        return unionBrokerageIncomeDao.selectOne(entityWrapper);
    }

    @Override
    public UnionBrokerageIncome getInvalidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("id", id);

        return unionBrokerageIncomeDao.selectOne(entityWrapper);
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

        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("bus_id", busId);

        return unionBrokerageIncomeDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokerageIncome> listValidByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId);

        return unionBrokerageIncomeDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokerageIncome> listInvalidByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("bus_id", busId);

        return unionBrokerageIncomeDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokerageIncome> listByMemberId(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("member_id", memberId);

        return unionBrokerageIncomeDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokerageIncome> listValidByMemberId(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("member_id", memberId);

        return unionBrokerageIncomeDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokerageIncome> listInvalidByMemberId(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("member_id", memberId);

        return unionBrokerageIncomeDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokerageIncome> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId);

        return unionBrokerageIncomeDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokerageIncome> listValidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId);

        return unionBrokerageIncomeDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokerageIncome> listInvalidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("union_id", unionId);

        return unionBrokerageIncomeDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokerageIncome> listByCardId(Integer cardId) throws Exception {
        if (cardId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("card_id", cardId);

        return unionBrokerageIncomeDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokerageIncome> listValidByCardId(Integer cardId) throws Exception {
        if (cardId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("card_id", cardId);

        return unionBrokerageIncomeDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokerageIncome> listInvalidByCardId(Integer cardId) throws Exception {
        if (cardId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("card_id", cardId);

        return unionBrokerageIncomeDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokerageIncome> listByOpportunityId(Integer opportunityId) throws Exception {
        if (opportunityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("opportunity_id", opportunityId);

        return unionBrokerageIncomeDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokerageIncome> listValidByOpportunityId(Integer opportunityId) throws Exception {
        if (opportunityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("opportunity_id", opportunityId);

        return unionBrokerageIncomeDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokerageIncome> listInvalidByOpportunityId(Integer opportunityId) throws Exception {
        if (opportunityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("opportunity_id", opportunityId);

        return unionBrokerageIncomeDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokerageIncome> listByIdList(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokerageIncome> entityWrapper = new EntityWrapper<>();
        entityWrapper.in("id", idList);

        return unionBrokerageIncomeDao.selectList(entityWrapper);
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
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionBrokerageIncome> newUnionBrokerageIncomeList) throws Exception {
        if (newUnionBrokerageIncomeList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionBrokerageIncomeDao.insertBatch(newUnionBrokerageIncomeList);
    }

    //********************************************* Object As a Service - remove ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

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

        unionBrokerageIncomeDao.updateById(updateUnionBrokerageIncome);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionBrokerageIncome> updateUnionBrokerageIncomeList) throws Exception {
        if (updateUnionBrokerageIncomeList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionBrokerageIncomeDao.updateBatchById(updateUnionBrokerageIncomeList);
    }

}