package com.gt.union.card.sharing.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.card.main.entity.UnionCardFan;
import com.gt.union.card.main.service.IUnionCardFanService;
import com.gt.union.card.sharing.dao.IUnionCardSharingRecordDao;
import com.gt.union.card.sharing.entity.UnionCardSharingRecord;
import com.gt.union.card.sharing.service.IUnionCardSharingRecordService;
import com.gt.union.card.sharing.vo.CardSharingRecordVO;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.union.main.service.IUnionMainService;
import com.gt.union.union.member.entity.UnionMember;
import com.gt.union.union.member.service.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 联盟卡售卡分成记录 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 17:39:04
 */
@Service
public class UnionCardSharingRecordServiceImpl implements IUnionCardSharingRecordService {
    @Autowired
    private IUnionCardSharingRecordDao unionCardSharingRecordDao;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionCardFanService unionCardFanService;

    //********************************************* Base On Business - get *********************************************

    //********************************************* Base On Business - list ********************************************

    @Override
    public List<UnionCardSharingRecord> listValidByUnionIdAndSharingMemberId(Integer unionId, Integer sharingMemberId) throws Exception {
        if (unionId == null || sharingMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardSharingRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .eq("sharing_member_id", sharingMemberId);

        return unionCardSharingRecordDao.selectList(entityWrapper);
    }

    @Override
    public List<CardSharingRecordVO> listCardSharingRecordVOByBusIdAndUnionId(
            Integer busId, Integer unionId, String optCardNumber, Date optBeginTime, Date optEndTime) throws Exception {
        if (busId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // 判断union有效性
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        // 判断member读权限
        UnionMember member = unionMemberService.getValidReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
        }
        // 按时间倒序排序
        List<CardSharingRecordVO> result = new ArrayList<>();
        List<UnionCardSharingRecord> recordList = listValidByUnionIdAndSharingMemberId(unionId, member.getId());
        if (ListUtil.isNotEmpty(recordList)) {
            for (UnionCardSharingRecord record : recordList) {
                if (optBeginTime != null && optBeginTime.compareTo(record.getCreateTime()) > 0) {
                    continue;
                }
                if (optEndTime != null && optEndTime.compareTo(record.getCreateTime()) < 0) {
                    continue;
                }
                Integer fanId = record.getFanId();
                UnionCardFan fan = unionCardFanService.getById(fanId);
                if (StringUtil.isNotEmpty(optCardNumber) && fan != null && !fan.getNumber().contains(optCardNumber)) {
                    continue;
                }
                CardSharingRecordVO vo = new CardSharingRecordVO();
                vo.setFan(fan);
                vo.setSharingRecord(record);
                UnionMember fromMember = unionMemberService.getById(record.getFromMemberId());
                vo.setMember(fromMember);
                result.add(vo);
            }
        }
        Collections.sort(result, new Comparator<CardSharingRecordVO>() {
            @Override
            public int compare(CardSharingRecordVO o1, CardSharingRecordVO o2) {
                return o2.getSharingRecord().getCreateTime().compareTo(o1.getSharingRecord().getCreateTime());
            }
        });

        return result;
    }

    //********************************************* Base On Business - save ********************************************

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    //********************************************* Base On Business - other *******************************************

    @Override
    public Double sumValidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardSharingRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId);

        entityWrapper.setSqlSelect("IfNull(SUM(sharing_money),0) sharingMoneySum");
        Map<String, Object> resultMap = unionCardSharingRecordDao.selectMap(entityWrapper);

        return Double.valueOf(resultMap.get("sharingMoneySum").toString());
    }

    //********************************************* Base On Business - filter ******************************************

    @Override
    public List<UnionCardSharingRecord> filterByDelStatus(List<UnionCardSharingRecord> unionCardSharingRecordList, Integer delStatus) throws Exception {
        if (delStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardSharingRecord> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionCardSharingRecordList)) {
            for (UnionCardSharingRecord unionCardSharingRecord : unionCardSharingRecordList) {
                if (delStatus.equals(unionCardSharingRecord.getDelStatus())) {
                    result.add(unionCardSharingRecord);
                }
            }
        }

        return result;
    }

    @Override
    public List<UnionCardSharingRecord> filterByUnionId(List<UnionCardSharingRecord> recordList, Integer unionId) throws Exception {
        if (recordList == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardSharingRecord> result = new ArrayList<>();
        for (UnionCardSharingRecord record : recordList) {
            if (unionId.equals(record.getUnionId())) {
                result.add(record);
            }
        }

        return result;
    }

    //********************************************* Object As a Service - get ******************************************

    @Override
    public UnionCardSharingRecord getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionCardSharingRecordDao.selectById(id);
    }

    @Override
    public UnionCardSharingRecord getValidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardSharingRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("id", id);

        return unionCardSharingRecordDao.selectOne(entityWrapper);
    }

    @Override
    public UnionCardSharingRecord getInvalidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardSharingRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("id", id);

        return unionCardSharingRecordDao.selectOne(entityWrapper);
    }

    //********************************************* Object As a Service - list *****************************************

    @Override
    public List<Integer> getIdList(List<UnionCardSharingRecord> unionCardSharingRecordList) throws Exception {
        if (unionCardSharingRecordList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<Integer> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionCardSharingRecordList)) {
            for (UnionCardSharingRecord unionCardSharingRecord : unionCardSharingRecordList) {
                result.add(unionCardSharingRecord.getId());
            }
        }

        return result;
    }

    @Override
    public List<UnionCardSharingRecord> listBySharingMemberId(Integer sharingMemberId) throws Exception {
        if (sharingMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardSharingRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("sharing_member_id", sharingMemberId);

        return unionCardSharingRecordDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardSharingRecord> listValidBySharingMemberId(Integer sharingMemberId) throws Exception {
        if (sharingMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardSharingRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("sharing_member_id", sharingMemberId);

        return unionCardSharingRecordDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardSharingRecord> listInvalidBySharingMemberId(Integer sharingMemberId) throws Exception {
        if (sharingMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardSharingRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("sharing_member_id", sharingMemberId);

        return unionCardSharingRecordDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardSharingRecord> listByFromMemberId(Integer fromMemberId) throws Exception {
        if (fromMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardSharingRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("from_member_id", fromMemberId);

        return unionCardSharingRecordDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardSharingRecord> listValidByFromMemberId(Integer fromMemberId) throws Exception {
        if (fromMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardSharingRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("from_member_id", fromMemberId);

        return unionCardSharingRecordDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardSharingRecord> listInvalidByFromMemberId(Integer fromMemberId) throws Exception {
        if (fromMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardSharingRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("from_member_id", fromMemberId);

        return unionCardSharingRecordDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardSharingRecord> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardSharingRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId);

        return unionCardSharingRecordDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardSharingRecord> listValidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardSharingRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId);

        return unionCardSharingRecordDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardSharingRecord> listInvalidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardSharingRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("union_id", unionId);

        return unionCardSharingRecordDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardSharingRecord> listByActivityId(Integer activityId) throws Exception {
        if (activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardSharingRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("activity_id", activityId);

        return unionCardSharingRecordDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardSharingRecord> listValidByActivityId(Integer activityId) throws Exception {
        if (activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardSharingRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("activity_id", activityId);

        return unionCardSharingRecordDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardSharingRecord> listInvalidByActivityId(Integer activityId) throws Exception {
        if (activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardSharingRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("activity_id", activityId);

        return unionCardSharingRecordDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardSharingRecord> listByCardId(Integer cardId) throws Exception {
        if (cardId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardSharingRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("card_id", cardId);

        return unionCardSharingRecordDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardSharingRecord> listValidByCardId(Integer cardId) throws Exception {
        if (cardId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardSharingRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("card_id", cardId);

        return unionCardSharingRecordDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardSharingRecord> listInvalidByCardId(Integer cardId) throws Exception {
        if (cardId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardSharingRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("card_id", cardId);

        return unionCardSharingRecordDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardSharingRecord> listByFanId(Integer fanId) throws Exception {
        if (fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardSharingRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("fan_id", fanId);

        return unionCardSharingRecordDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardSharingRecord> listValidByFanId(Integer fanId) throws Exception {
        if (fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardSharingRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("fan_id", fanId);

        return unionCardSharingRecordDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardSharingRecord> listInvalidByFanId(Integer fanId) throws Exception {
        if (fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardSharingRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("fan_id", fanId);

        return unionCardSharingRecordDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardSharingRecord> listByIdList(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardSharingRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.in("id", idList).eq(ListUtil.isEmpty(idList), "id", null);

        return unionCardSharingRecordDao.selectList(entityWrapper);
    }

    @Override
    public Page pageSupport(Page page, EntityWrapper<UnionCardSharingRecord> entityWrapper) throws Exception {
        if (page == null || entityWrapper == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionCardSharingRecordDao.selectPage(page, entityWrapper);
    }
    //********************************************* Object As a Service - save *****************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(UnionCardSharingRecord newUnionCardSharingRecord) throws Exception {
        if (newUnionCardSharingRecord == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionCardSharingRecordDao.insert(newUnionCardSharingRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionCardSharingRecord> newUnionCardSharingRecordList) throws Exception {
        if (newUnionCardSharingRecordList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionCardSharingRecordDao.insertBatch(newUnionCardSharingRecordList);
    }

    //********************************************* Object As a Service - remove ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        UnionCardSharingRecord removeUnionCardSharingRecord = new UnionCardSharingRecord();
        removeUnionCardSharingRecord.setId(id);
        removeUnionCardSharingRecord.setDelStatus(CommonConstant.DEL_STATUS_YES);
        unionCardSharingRecordDao.updateById(removeUnionCardSharingRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardSharingRecord> removeUnionCardSharingRecordList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardSharingRecord removeUnionCardSharingRecord = new UnionCardSharingRecord();
            removeUnionCardSharingRecord.setId(id);
            removeUnionCardSharingRecord.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionCardSharingRecordList.add(removeUnionCardSharingRecord);
        }
        unionCardSharingRecordDao.updateBatchById(removeUnionCardSharingRecordList);
    }

    //********************************************* Object As a Service - update ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UnionCardSharingRecord updateUnionCardSharingRecord) throws Exception {
        if (updateUnionCardSharingRecord == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionCardSharingRecordDao.updateById(updateUnionCardSharingRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionCardSharingRecord> updateUnionCardSharingRecordList) throws Exception {
        if (updateUnionCardSharingRecordList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionCardSharingRecordDao.updateBatchById(updateUnionCardSharingRecordList);
    }

}