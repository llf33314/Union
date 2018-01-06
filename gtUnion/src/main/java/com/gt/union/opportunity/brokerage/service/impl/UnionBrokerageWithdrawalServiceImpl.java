package com.gt.union.opportunity.brokerage.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.BigDecimalUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.opportunity.brokerage.dao.IUnionBrokerageWithdrawalDao;
import com.gt.union.opportunity.brokerage.entity.UnionBrokerageWithdrawal;
import com.gt.union.opportunity.brokerage.service.IUnionBrokerageIncomeService;
import com.gt.union.opportunity.brokerage.service.IUnionBrokerageWithdrawalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 佣金提现 服务实现类
 *
 * @author linweicong
 * @version 2017-11-24 09:21:28
 */
@Service
public class UnionBrokerageWithdrawalServiceImpl implements IUnionBrokerageWithdrawalService {
    @Autowired
    private IUnionBrokerageWithdrawalDao unionBrokerageWithdrawalDao;

    @Autowired
    private IUnionBrokerageIncomeService unionBrokerageIncomeService;

    //********************************************* Base On Business - get *********************************************

    @Override
    public Double getValidAvailableMoneyByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）收入
        Double incomeSum = unionBrokerageIncomeService.sumValidMoneyByBusId(busId);
        // （2）已提现
        Double withdrawalSum = sumValidMoneyByBusId(busId);

        return BigDecimalUtil.toDouble(BigDecimalUtil.subtract(incomeSum, withdrawalSum));
    }


    //********************************************* Base On Business - list ********************************************


    //********************************************* Base On Business - save ********************************************

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    //********************************************* Base On Business - other *******************************************

    @Override
    public Double sumMoneyByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokerageWithdrawal> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("bus_id", busId);

        entityWrapper.setSqlSelect("IfNull(SUM(money),0) moneySum");
        Map<String, Object> resultMap = unionBrokerageWithdrawalDao.selectMap(entityWrapper);

        return Double.valueOf(resultMap.get("moneySum").toString());
    }

    @Override
    public Double sumValidMoneyByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokerageWithdrawal> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId);

        entityWrapper.setSqlSelect("IfNull(SUM(money),0) moneySum");
        Map<String, Object> resultMap = unionBrokerageWithdrawalDao.selectMap(entityWrapper);

        return Double.valueOf(resultMap.get("moneySum").toString());
    }

    //********************************************* Base On Business - filter ******************************************

    @Override
    public List<UnionBrokerageWithdrawal> filterByDelStatus(List<UnionBrokerageWithdrawal> unionBrokerageWithdrawalList, Integer delStatus) throws Exception {
        if (delStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionBrokerageWithdrawal> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionBrokerageWithdrawalList)) {
            for (UnionBrokerageWithdrawal unionBrokerageWithdrawal : unionBrokerageWithdrawalList) {
                if (delStatus.equals(unionBrokerageWithdrawal.getDelStatus())) {
                    result.add(unionBrokerageWithdrawal);
                }
            }
        }

        return result;
    }

    //********************************************* Object As a Service - get ******************************************

    @Override
    public UnionBrokerageWithdrawal getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionBrokerageWithdrawalDao.selectById(id);
    }

    @Override
    public UnionBrokerageWithdrawal getValidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokerageWithdrawal> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("id", id);

        return unionBrokerageWithdrawalDao.selectOne(entityWrapper);
    }

    @Override
    public UnionBrokerageWithdrawal getInvalidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokerageWithdrawal> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("id", id);

        return unionBrokerageWithdrawalDao.selectOne(entityWrapper);
    }

    //********************************************* Object As a Service - list *****************************************

    @Override
    public List<Integer> getIdList(List<UnionBrokerageWithdrawal> unionBrokerageWithdrawalList) throws Exception {
        if (unionBrokerageWithdrawalList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<Integer> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionBrokerageWithdrawalList)) {
            for (UnionBrokerageWithdrawal unionBrokerageWithdrawal : unionBrokerageWithdrawalList) {
                result.add(unionBrokerageWithdrawal.getId());
            }
        }

        return result;
    }

    @Override
    public List<UnionBrokerageWithdrawal> listByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokerageWithdrawal> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("bus_id", busId);

        return unionBrokerageWithdrawalDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokerageWithdrawal> listValidByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokerageWithdrawal> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId);

        return unionBrokerageWithdrawalDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokerageWithdrawal> listInvalidByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokerageWithdrawal> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("bus_id", busId);

        return unionBrokerageWithdrawalDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokerageWithdrawal> listByVerifierId(Integer verifierId) throws Exception {
        if (verifierId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokerageWithdrawal> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("verifier_id", verifierId);

        return unionBrokerageWithdrawalDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokerageWithdrawal> listValidByVerifierId(Integer verifierId) throws Exception {
        if (verifierId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokerageWithdrawal> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("verifier_id", verifierId);

        return unionBrokerageWithdrawalDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokerageWithdrawal> listInvalidByVerifierId(Integer verifierId) throws Exception {
        if (verifierId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokerageWithdrawal> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("verifier_id", verifierId);

        return unionBrokerageWithdrawalDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionBrokerageWithdrawal> listByIdList(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokerageWithdrawal> entityWrapper = new EntityWrapper<>();
        entityWrapper.in("id", idList);

        return unionBrokerageWithdrawalDao.selectList(entityWrapper);
    }

    @Override
    public Page pageSupport(Page page, EntityWrapper<UnionBrokerageWithdrawal> entityWrapper) throws Exception {
        if (page == null || entityWrapper == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionBrokerageWithdrawalDao.selectPage(page, entityWrapper);
    }
    //********************************************* Object As a Service - save *****************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(UnionBrokerageWithdrawal newUnionBrokerageWithdrawal) throws Exception {
        if (newUnionBrokerageWithdrawal == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionBrokerageWithdrawalDao.insert(newUnionBrokerageWithdrawal);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionBrokerageWithdrawal> newUnionBrokerageWithdrawalList) throws Exception {
        if (newUnionBrokerageWithdrawalList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionBrokerageWithdrawalDao.insertBatch(newUnionBrokerageWithdrawalList);
    }

    //********************************************* Object As a Service - remove ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        UnionBrokerageWithdrawal removeUnionBrokerageWithdrawal = new UnionBrokerageWithdrawal();
        removeUnionBrokerageWithdrawal.setId(id);
        removeUnionBrokerageWithdrawal.setDelStatus(CommonConstant.DEL_STATUS_YES);
        unionBrokerageWithdrawalDao.updateById(removeUnionBrokerageWithdrawal);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionBrokerageWithdrawal> removeUnionBrokerageWithdrawalList = new ArrayList<>();
        for (Integer id : idList) {
            UnionBrokerageWithdrawal removeUnionBrokerageWithdrawal = new UnionBrokerageWithdrawal();
            removeUnionBrokerageWithdrawal.setId(id);
            removeUnionBrokerageWithdrawal.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionBrokerageWithdrawalList.add(removeUnionBrokerageWithdrawal);
        }
        unionBrokerageWithdrawalDao.updateBatchById(removeUnionBrokerageWithdrawalList);
    }

    //********************************************* Object As a Service - update ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UnionBrokerageWithdrawal updateUnionBrokerageWithdrawal) throws Exception {
        if (updateUnionBrokerageWithdrawal == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionBrokerageWithdrawalDao.updateById(updateUnionBrokerageWithdrawal);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionBrokerageWithdrawal> updateUnionBrokerageWithdrawalList) throws Exception {
        if (updateUnionBrokerageWithdrawalList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionBrokerageWithdrawalDao.updateBatchById(updateUnionBrokerageWithdrawalList);
    }

}