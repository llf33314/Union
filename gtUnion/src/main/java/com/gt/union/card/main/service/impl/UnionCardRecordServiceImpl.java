package com.gt.union.card.main.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.card.main.dao.IUnionCardRecordDao;
import com.gt.union.card.main.entity.UnionCardRecord;
import com.gt.union.card.main.service.IUnionCardRecordService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.ListUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 联盟卡购买记录 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 17:39:04
 */
@Service
public class UnionCardRecordServiceImpl implements IUnionCardRecordService {
    @Autowired
    private IUnionCardRecordDao unionCardRecordDao;

    //********************************************* Base On Business - get *********************************************

    //********************************************* Base On Business - list ********************************************

    @Override
    public List<UnionCardRecord> listValidByOrderNo(String orderNo) throws Exception {
        if (orderNo == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.COMMON_NO)
                .eq("sys_order_no", orderNo);

        return unionCardRecordDao.selectList(entityWrapper);
    }

    //********************************************* Base On Business - save ********************************************

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    //********************************************* Base On Business - other *******************************************

    //********************************************* Base On Business - filter ******************************************

    @Override
    public List<UnionCardRecord> filterByDelStatus(List<UnionCardRecord> unionCardRecordList, Integer delStatus) throws Exception {
        if (delStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardRecord> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionCardRecordList)) {
            for (UnionCardRecord unionCardRecord : unionCardRecordList) {
                if (delStatus.equals(unionCardRecord.getDelStatus())) {
                    result.add(unionCardRecord);
                }
            }
        }

        return result;
    }

    //********************************************* Object As a Service - get ******************************************

    @Override
    public UnionCardRecord getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionCardRecordDao.selectById(id);
    }

    @Override
    public UnionCardRecord getValidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("id", id);

        return unionCardRecordDao.selectOne(entityWrapper);
    }

    @Override
    public UnionCardRecord getInvalidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("id", id);

        return unionCardRecordDao.selectOne(entityWrapper);
    }

    @Override
    public UnionCardRecord getValidByOrderNo(String orderNo) throws Exception {
        if (orderNo == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("sys_order_no", orderNo);

        return unionCardRecordDao.selectOne(entityWrapper);
    }

    //********************************************* Object As a Service - list *****************************************

    @Override
    public List<Integer> getIdList(List<UnionCardRecord> unionCardRecordList) throws Exception {
        if (unionCardRecordList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<Integer> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionCardRecordList)) {
            for (UnionCardRecord unionCardRecord : unionCardRecordList) {
                result.add(unionCardRecord.getId());
            }
        }

        return result;
    }

    @Override
    public List<UnionCardRecord> listByActivityId(Integer activityId) throws Exception {
        if (activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("activity_id", activityId);

        return unionCardRecordDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardRecord> listValidByActivityId(Integer activityId) throws Exception {
        if (activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("activity_id", activityId);

        return unionCardRecordDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardRecord> listInvalidByActivityId(Integer activityId) throws Exception {
        if (activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("activity_id", activityId);

        return unionCardRecordDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardRecord> listByCardId(Integer cardId) throws Exception {
        if (cardId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("card_id", cardId);

        return unionCardRecordDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardRecord> listValidByCardId(Integer cardId) throws Exception {
        if (cardId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("card_id", cardId);

        return unionCardRecordDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardRecord> listInvalidByCardId(Integer cardId) throws Exception {
        if (cardId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("card_id", cardId);

        return unionCardRecordDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardRecord> listByFanId(Integer fanId) throws Exception {
        if (fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("fan_id", fanId);

        return unionCardRecordDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardRecord> listValidByFanId(Integer fanId) throws Exception {
        if (fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("fan_id", fanId);

        return unionCardRecordDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardRecord> listInvalidByFanId(Integer fanId) throws Exception {
        if (fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("fan_id", fanId);

        return unionCardRecordDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardRecord> listByMemberId(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("member_id", memberId);

        return unionCardRecordDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardRecord> listValidByMemberId(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("member_id", memberId);

        return unionCardRecordDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardRecord> listInvalidByMemberId(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("member_id", memberId);

        return unionCardRecordDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardRecord> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId);

        return unionCardRecordDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardRecord> listValidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId);

        return unionCardRecordDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardRecord> listInvalidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("union_id", unionId);

        return unionCardRecordDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardRecord> listByIdList(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.in("id", idList).eq(ListUtil.isEmpty(idList), "id", null);

        return unionCardRecordDao.selectList(entityWrapper);
    }

    @Override
    public Page pageSupport(Page page, EntityWrapper<UnionCardRecord> entityWrapper) throws Exception {
        if (page == null || entityWrapper == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionCardRecordDao.selectPage(page, entityWrapper);
    }
    //********************************************* Object As a Service - save *****************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(UnionCardRecord newUnionCardRecord) throws Exception {
        if (newUnionCardRecord == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionCardRecordDao.insert(newUnionCardRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionCardRecord> newUnionCardRecordList) throws Exception {
        if (newUnionCardRecordList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionCardRecordDao.insertBatch(newUnionCardRecordList);
    }

    //********************************************* Object As a Service - remove ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        UnionCardRecord removeUnionCardRecord = new UnionCardRecord();
        removeUnionCardRecord.setId(id);
        removeUnionCardRecord.setDelStatus(CommonConstant.DEL_STATUS_YES);
        unionCardRecordDao.updateById(removeUnionCardRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardRecord> removeUnionCardRecordList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardRecord removeUnionCardRecord = new UnionCardRecord();
            removeUnionCardRecord.setId(id);
            removeUnionCardRecord.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionCardRecordList.add(removeUnionCardRecord);
        }
        unionCardRecordDao.updateBatchById(removeUnionCardRecordList);
    }

    //********************************************* Object As a Service - update ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UnionCardRecord updateUnionCardRecord) throws Exception {
        if (updateUnionCardRecord == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionCardRecordDao.updateById(updateUnionCardRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionCardRecord> updateUnionCardRecordList) throws Exception {
        if (updateUnionCardRecordList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionCardRecordDao.updateBatchById(updateUnionCardRecordList);
    }

}