package com.gt.union.card.main.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.card.main.dao.IUnionCardIntegralDao;
import com.gt.union.card.main.entity.UnionCardIntegral;
import com.gt.union.card.main.service.IUnionCardIntegralService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.BigDecimalUtil;
import com.gt.union.common.util.ListUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 联盟积分 服务实现类
 *
 * @author linweicong
 * @version 2017-09-01 11:34:16
 */
@Service
public class UnionCardIntegralServiceImpl implements IUnionCardIntegralService {
    @Autowired
    private IUnionCardIntegralDao unionCardIntegralDao;

    //********************************************* Base On Business - get *********************************************

    @Override
    public UnionCardIntegral getValidByUnionIdAndFanId(Integer unionId, Integer fanId) throws Exception {
        if (unionId == null || fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardIntegral> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .eq("fan_id", fanId);

        return unionCardIntegralDao.selectOne(entityWrapper);
    }

    //********************************************* Base On Business - list ********************************************

    @Override
    public List<UnionCardIntegral> listValidByUnionIdAndFanId(Integer unionId, Integer fanId) throws Exception {
        if (unionId == null || fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardIntegral> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .eq("fan_id", fanId);

        return unionCardIntegralDao.selectList(entityWrapper);
    }

    //********************************************* Base On Business - save ********************************************

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    //********************************************* Base On Business - other *******************************************

    @Override
    public Double sumValidIntegralByUnionIdAndFanId(Integer unionId, Integer fanId) throws Exception {
        if (unionId == null || fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        BigDecimal result = BigDecimal.ZERO;
        List<UnionCardIntegral> integralList = listValidByUnionIdAndFanId(unionId, fanId);
        if (ListUtil.isNotEmpty(integralList)) {
            for (UnionCardIntegral integral : integralList) {
                result = BigDecimalUtil.add(result, integral.getIntegral());
            }
        }

        return result.doubleValue();
    }

    @Override
    public Double sumValidIntegralByFanId(Integer fanId) throws Exception {
        if (fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        BigDecimal result = BigDecimal.ZERO;
        List<UnionCardIntegral> integralList = listValidByFanId(fanId);
        if (ListUtil.isNotEmpty(integralList)) {
            for (UnionCardIntegral integral : integralList) {
                result = BigDecimalUtil.add(result, integral.getIntegral());
            }
        }

        return result.doubleValue();
    }

    @Override
    public Double sumValidIntegralByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        BigDecimal result = BigDecimal.ZERO;
        List<UnionCardIntegral> integralList = listValidByUnionId(unionId);
        if (ListUtil.isNotEmpty(integralList)) {
            for (UnionCardIntegral integral : integralList) {
                result = BigDecimalUtil.add(result, integral.getIntegral());
            }
        }

        return result.doubleValue();
    }

    //********************************************* Base On Business - filter ******************************************

    @Override
    public List<UnionCardIntegral> filterByDelStatus(List<UnionCardIntegral> unionCardIntegralList, Integer delStatus) throws Exception {
        if (delStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardIntegral> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionCardIntegralList)) {
            for (UnionCardIntegral unionCardIntegral : unionCardIntegralList) {
                if (delStatus.equals(unionCardIntegral.getDelStatus())) {
                    result.add(unionCardIntegral);
                }
            }
        }

        return result;
    }

    @Override
    public List<UnionCardIntegral> filterByUnionId(List<UnionCardIntegral> integralList, Integer unionId) throws Exception {
        if (integralList == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardIntegral> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(integralList)) {
            for (UnionCardIntegral integral : integralList) {
                if (unionId.equals(integral.getUnionId())) {
                    result.add(integral);
                }
            }
        }

        return result;
    }

    //********************************************* Object As a Service - get ******************************************

    @Override
    public UnionCardIntegral getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionCardIntegralDao.selectById(id);
    }

    @Override
    public UnionCardIntegral getValidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardIntegral> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("id", id);

        return unionCardIntegralDao.selectOne(entityWrapper);
    }

    @Override
    public UnionCardIntegral getInvalidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardIntegral> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("id", id);

        return unionCardIntegralDao.selectOne(entityWrapper);
    }

    //********************************************* Object As a Service - list *****************************************

    @Override
    public List<Integer> getIdList(List<UnionCardIntegral> unionCardIntegralList) throws Exception {
        if (unionCardIntegralList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<Integer> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionCardIntegralList)) {
            for (UnionCardIntegral unionCardIntegral : unionCardIntegralList) {
                result.add(unionCardIntegral.getId());
            }
        }

        return result;
    }

    @Override
    public List<UnionCardIntegral> listByFanId(Integer fanId) throws Exception {
        if (fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardIntegral> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("fan_id", fanId);

        return unionCardIntegralDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardIntegral> listValidByFanId(Integer fanId) throws Exception {
        if (fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardIntegral> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("fan_id", fanId);

        return unionCardIntegralDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardIntegral> listInvalidByFanId(Integer fanId) throws Exception {
        if (fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardIntegral> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("fan_id", fanId);

        return unionCardIntegralDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardIntegral> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardIntegral> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId);

        return unionCardIntegralDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardIntegral> listValidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardIntegral> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId);

        return unionCardIntegralDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardIntegral> listInvalidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardIntegral> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("union_id", unionId);

        return unionCardIntegralDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardIntegral> listByIdList(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardIntegral> entityWrapper = new EntityWrapper<>();
        entityWrapper.in("id", idList);

        return unionCardIntegralDao.selectList(entityWrapper);
    }

    @Override
    public Page pageSupport(Page page, EntityWrapper<UnionCardIntegral> entityWrapper) throws Exception {
        if (page == null || entityWrapper == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionCardIntegralDao.selectPage(page, entityWrapper);
    }
    //********************************************* Object As a Service - save *****************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(UnionCardIntegral newUnionCardIntegral) throws Exception {
        if (newUnionCardIntegral == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionCardIntegralDao.insert(newUnionCardIntegral);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionCardIntegral> newUnionCardIntegralList) throws Exception {
        if (newUnionCardIntegralList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionCardIntegralDao.insertBatch(newUnionCardIntegralList);
    }

    //********************************************* Object As a Service - remove ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        UnionCardIntegral removeUnionCardIntegral = new UnionCardIntegral();
        removeUnionCardIntegral.setId(id);
        removeUnionCardIntegral.setDelStatus(CommonConstant.DEL_STATUS_YES);
        unionCardIntegralDao.updateById(removeUnionCardIntegral);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardIntegral> removeUnionCardIntegralList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardIntegral removeUnionCardIntegral = new UnionCardIntegral();
            removeUnionCardIntegral.setId(id);
            removeUnionCardIntegral.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionCardIntegralList.add(removeUnionCardIntegral);
        }
        unionCardIntegralDao.updateBatchById(removeUnionCardIntegralList);
    }

    //********************************************* Object As a Service - update ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UnionCardIntegral updateUnionCardIntegral) throws Exception {
        if (updateUnionCardIntegral == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionCardIntegralDao.updateById(updateUnionCardIntegral);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionCardIntegral> updateUnionCardIntegralList) throws Exception {
        if (updateUnionCardIntegralList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionCardIntegralDao.updateBatchById(updateUnionCardIntegralList);
    }

}