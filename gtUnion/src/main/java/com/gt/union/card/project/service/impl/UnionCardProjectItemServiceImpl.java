package com.gt.union.card.project.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.card.activity.entity.UnionCardActivity;
import com.gt.union.card.activity.service.IUnionCardActivityService;
import com.gt.union.card.constant.CardConstant;
import com.gt.union.card.consume.service.IUnionConsumeProjectService;
import com.gt.union.card.project.entity.UnionCardProject;
import com.gt.union.card.project.entity.UnionCardProjectItem;
import com.gt.union.card.project.mapper.UnionCardProjectItemMapper;
import com.gt.union.card.project.service.IUnionCardProjectItemService;
import com.gt.union.card.project.service.IUnionCardProjectService;
import com.gt.union.card.project.util.UnionCardProjectItemCacheUtil;
import com.gt.union.card.project.vo.CardProjectItemConsumeVO;
import com.gt.union.card.project.vo.CardProjectVO;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.DateUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.union.main.service.IUnionMainService;
import com.gt.union.union.member.entity.UnionMember;
import com.gt.union.union.member.service.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 项目优惠 服务实现类
 *
 * @author linweicong
 * @version 2017-11-27 09:55:47
 */
@Service
public class UnionCardProjectItemServiceImpl extends ServiceImpl<UnionCardProjectItemMapper, UnionCardProjectItem> implements IUnionCardProjectItemService {
    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionCardActivityService unionCardActivityService;

    @Autowired
    private IUnionCardProjectService unionCardProjectService;

    @Autowired
    private IUnionConsumeProjectService unionConsumeProjectService;

    //***************************************** Domain Driven Design - get *********************************************

    //***************************************** Domain Driven Design - list ********************************************

    @Override
    public List<UnionCardProjectItem> listItemByProjectId(Integer projectId) throws Exception {
        if (projectId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardProjectItem> result = new ArrayList<>();
        List<UnionCardProjectItem> itemList = listByProjectId(projectId);
        for (UnionCardProjectItem item : itemList) {
            if (CardConstant.ITEM_TYPE_ERP_TEXT == item.getType()) {
                // TODO 这里少了个接口
            } else if (CardConstant.ITEM_TYPE_ERP_GOODS == item.getType()) {
                // TODO 这里少了个接口
            }
            result.add(item);
        }

        return result;
    }

    @Override
    public List<UnionCardProjectItem> listItemByProjectIdAndType(Integer projectId, Integer type) throws Exception {
        if (projectId == null || type == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardProjectItem> result = listItemByProjectId(projectId);
        result = filterByType(result, type);

        return result;
    }

    @Override
    public List<CardProjectItemConsumeVO> listCardProjectItemConsumeVOByBusIdAndUnionIdAndActivityId(Integer busId, Integer unionId, Integer activityId) throws Exception {
        if (busId == null || unionId == null || activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性和member读权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_READ_REJECT);
        }
        // （2）	判断activityId有效性
        UnionCardActivity activity = unionCardActivityService.getByIdAndUnionId(activityId, unionId);
        if (activity == null) {
            throw new BusinessException("找不到活动卡信息");
        }
        // （3）	获取activity关联的非ERP文本项目优惠列表
        // （4）	过滤掉可使用数量为0的项目优惠
        List<CardProjectItemConsumeVO> result = new ArrayList<>();
        List<UnionCardProject> projectList = unionCardProjectService.listByActivityIdAndUnionIdAndStatus(activityId, unionId, CardConstant.PROJECT_STATUS_ACCEPT);
        if (ListUtil.isNotEmpty(projectList)) {
            for (UnionCardProject project : projectList) {
                List<UnionCardProjectItem> textItemList = listItemByProjectIdAndType(project.getId(), CardConstant.ITEM_TYPE_TEXT);

                if (ListUtil.isNotEmpty(textItemList)) {
                    for (UnionCardProjectItem textItem : textItemList) {
                        Integer consumeItemCount = unionConsumeProjectService.countByProjectIdAndProjectItemId(project.getId(), textItem.getId());
                        Integer textItemNumber = textItem.getNumber() != null ? textItem.getNumber() : 0;
                        Integer surplusItemCount = textItemNumber - consumeItemCount;

                        if (surplusItemCount > 0) {
                            CardProjectItemConsumeVO vo = new CardProjectItemConsumeVO();
                            vo.setItem(textItem);
                            vo.setAvailableCount(surplusItemCount);
                            result.add(vo);
                        }
                    }
                }
            }
        }
        // （5）	按时间顺序排序
        Collections.sort(result, new Comparator<CardProjectItemConsumeVO>() {
            @Override
            public int compare(CardProjectItemConsumeVO o1, CardProjectItemConsumeVO o2) {
                return o2.getItem().getCreateTime().compareTo(o1.getItem().getCreateTime());
            }
        });

        return result;
    }


    //***************************************** Domain Driven Design - save ********************************************

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveProjectItemVOByBusIdAndActivityIdAndUnionId(Integer busId, Integer activityId, Integer unionId, CardProjectVO vo) throws Exception {
        if (busId == null || activityId == null || unionId == null || vo == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性和member写权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getWriteByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_WRITE_REJECT);
        }
        // （2）	判断activityId有效性
        UnionCardActivity activity = unionCardActivityService.getByIdAndUnionId(activityId, unionId);
        if (activity == null) {
            throw new BusinessException("找不到活动卡信息");
        }
        // （3）	要求活动在报名中状态
        Integer activityStatus = unionCardActivityService.getStatus(activity);
        if (CardConstant.ACTIVITY_STATUS_APPLYING != activityStatus) {
            throw new BusinessException("活动卡不在报名中状态，无法操作");
        }
        // （4）	判断是否已有活动项目
        // 如果有，则要求在未提交或不通过状态，且删除所有已有的项目优惠，保存新项目优惠
        // 如果没有，则新增活动项目，未提交状态，且保存新项目优惠
        UnionCardProject updateProject = null;
        UnionCardProject saveProject = null;
        List<Integer> removeItemIdList = null;
        UnionCardProject project = unionCardProjectService.getByActivityIdAndMemberIdAndUnionId(activityId, member.getId(), unionId);
        Date currentDate = DateUtil.getCurrentDate();
        if (project != null) {
            if (CardConstant.PROJECT_STATUS_COMMITTED == project.getStatus()) {
                throw new BusinessException("活动项目已提交");
            }
            if (CardConstant.PROJECT_STATUS_ACCEPT == project.getStatus()) {
                throw new BusinessException("活动项目已审核通过");
            }
            updateProject = new UnionCardProject();
            updateProject.setId(project.getId());
            updateProject.setModifyTime(currentDate);
            updateProject.setStatus(CardConstant.PROJECT_STATUS_NOT_COMMIT);

            List<UnionCardProjectItem> removeItemList = listByProjectId(project.getId());
            removeItemIdList = new ArrayList<>();
            if (ListUtil.isNotEmpty(removeItemList)) {
                for (UnionCardProjectItem item : removeItemList) {
                    removeItemIdList.add(item.getId());
                }
            }
        } else {
            saveProject = new UnionCardProject();
            saveProject.setDelStatus(CommonConstant.COMMON_NO);
            saveProject.setCreateTime(currentDate);
            saveProject.setActivityId(activityId);
            saveProject.setMemberId(member.getId());
            saveProject.setUnionId(unionId);
            saveProject.setStatus(CardConstant.PROJECT_STATUS_NOT_COMMIT);
        }

        List<UnionCardProjectItem> saveItemList = listItemByVO(vo);

        // 事务操作
        if (project == null) {
            unionCardProjectService.save(saveProject);

            for (UnionCardProjectItem saveItem : saveItemList) {
                saveItem.setProjectId(saveProject.getId());
            }
            saveBatch(saveItemList);
        } else {
            for (UnionCardProjectItem saveItem : saveItemList) {
                saveItem.setProjectId(project.getId());
            }

            unionCardProjectService.update(updateProject);
            removeBatchById(removeItemIdList);
            saveBatch(saveItemList);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void commitProjectItemVOByBusIdAndActivityIdAndUnionId(Integer busId, Integer activityId, Integer unionId, CardProjectVO vo) throws Exception {
        if (busId == null || activityId == null || unionId == null || vo == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性和member写权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getWriteByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_WRITE_REJECT);
        }
        // （2）	判断activityId有效性
        UnionCardActivity activity = unionCardActivityService.getByIdAndUnionId(activityId, unionId);
        if (activity == null) {
            throw new BusinessException("找不到活动卡信息");
        }
        // （3）	要求活动在报名中状态
        Integer activityStatus = unionCardActivityService.getStatus(activity);
        if (CardConstant.ACTIVITY_STATUS_APPLYING != activityStatus) {
            throw new BusinessException("活动卡不在报名中状态，无法操作");
        }
        // （4）	判断是否已有活动项目
        // 如果有，则要求在未提交或不通过状态，且删除所有已有的项目优惠，保存新项目优惠
        // 如果没有，则新增活动项目，未提交状态，且保存新项目优惠
        UnionCardProject updateProject = null;
        UnionCardProject saveProject = null;
        List<Integer> removeItemIdList = null;
        UnionCardProject project = unionCardProjectService.getByActivityIdAndMemberIdAndUnionId(activityId, member.getId(), unionId);
        Date currentDate = DateUtil.getCurrentDate();
        if (project != null) {
            if (CardConstant.PROJECT_STATUS_COMMITTED == project.getStatus()) {
                throw new BusinessException("活动项目已提交");
            }
            if (CardConstant.PROJECT_STATUS_ACCEPT == project.getStatus()) {
                throw new BusinessException("活动项目已审核通过");
            }
            updateProject = new UnionCardProject();
            updateProject.setId(project.getId());
            updateProject.setModifyTime(currentDate);
            updateProject.setStatus(CardConstant.ACTIVITY_IS_PROJECT_CHECK_YES == activity.getIsProjectCheck()
                    ? CardConstant.PROJECT_STATUS_COMMITTED : CardConstant.PROJECT_STATUS_ACCEPT);

            List<UnionCardProjectItem> removeItemList = listByProjectId(project.getId());
            removeItemIdList = new ArrayList<>();
            if (ListUtil.isNotEmpty(removeItemList)) {
                for (UnionCardProjectItem item : removeItemList) {
                    removeItemIdList.add(item.getId());
                }
            }
        } else {
            saveProject = new UnionCardProject();
            saveProject.setDelStatus(CommonConstant.COMMON_NO);
            saveProject.setCreateTime(currentDate);
            saveProject.setActivityId(activityId);
            saveProject.setMemberId(member.getId());
            saveProject.setUnionId(unionId);
            saveProject.setStatus(CardConstant.ACTIVITY_IS_PROJECT_CHECK_YES == activity.getIsProjectCheck()
                    ? CardConstant.PROJECT_STATUS_COMMITTED : CardConstant.PROJECT_STATUS_ACCEPT);
        }

        List<UnionCardProjectItem> saveItemList = listItemByVO(vo);

        // 事务操作
        if (project != null) {
            for (UnionCardProjectItem saveItem : saveItemList) {
                saveItem.setProjectId(project.getId());
            }

            unionCardProjectService.update(updateProject);
            removeBatchById(removeItemIdList);
            saveBatch(saveItemList);
        } else {
            unionCardProjectService.save(saveProject);

            for (UnionCardProjectItem saveItem : saveItemList) {
                saveItem.setProjectId(saveProject.getId());
            }
            saveBatch(saveItemList);
        }
    }

    private List<UnionCardProjectItem> listItemByVO(CardProjectVO vo) throws Exception {
        List<UnionCardProjectItem> result = new ArrayList<>();

        // TODO 这里少个接口
        Integer isErp = CommonConstant.COMMON_YES;
        if (CommonConstant.COMMON_YES == isErp) {
            result.addAll(listErpTextByVO(vo));
            result.addAll(listErpGoodsByVO(vo));
        } else {
            result.addAll(listTextByVO(vo));
        }

        return result;
    }

    private List<UnionCardProjectItem> listTextByVO(CardProjectVO vo) throws Exception {
        List<UnionCardProjectItem> result = new ArrayList<>();
        Date currentDate = DateUtil.getCurrentDate();

        List<UnionCardProjectItem> textList = vo.getNonErpTextList();
        if (ListUtil.isNotEmpty(textList)) {
            for (UnionCardProjectItem text : textList) {
                UnionCardProjectItem saveItem = new UnionCardProjectItem();
                saveItem.setDelStatus(CommonConstant.COMMON_NO);
                saveItem.setCreateTime(currentDate);
                saveItem.setType(CardConstant.ITEM_TYPE_TEXT);

                String name = text.getName();
                if (StringUtil.isEmpty(name)) {
                    throw new BusinessException("名称不能为空");
                }
                saveItem.setName(name);

                Integer number = text.getNumber();
                if (number == null || number <= 0) {
                    throw new BusinessException("数量不能为空，且必须大于0");
                }

                saveItem.setNumber(number);
                result.add(saveItem);
            }
        }

        return result;
    }

    private List<UnionCardProjectItem> listErpTextByVO(CardProjectVO vo) throws Exception {
        List<UnionCardProjectItem> result = new ArrayList<>();
        Date currentDate = DateUtil.getCurrentDate();

        List<UnionCardProjectItem> erpTextList = vo.getErpTextList();
        if (ListUtil.isNotEmpty(erpTextList)) {
            for (UnionCardProjectItem erpText : erpTextList) {
                UnionCardProjectItem saveItem = new UnionCardProjectItem();
                saveItem.setDelStatus(CommonConstant.COMMON_NO);
                saveItem.setCreateTime(currentDate);
                saveItem.setType(CardConstant.ITEM_TYPE_ERP_TEXT);

                Integer erpType = erpText.getErpType();
                if (erpType == null) {
                    throw new BusinessException("ERP类型不能为空");
                }
                saveItem.setErpType(erpType);

                Integer shopId = erpText.getShopId();
                if (shopId == null) {
                    throw new BusinessException("门店id不能为空");
                }
                saveItem.setShopId(shopId);

                Integer erpTextId = erpText.getErpTextId();
                if (erpTextId == null) {
                    throw new BusinessException("ERP文本项目id不能为空");
                }
                saveItem.setErpTextId(erpTextId);

                String name = erpText.getName();
                if (StringUtil.isEmpty(name)) {
                    throw new BusinessException("名称不能为空");
                }
                saveItem.setName(name);

                Integer number = erpText.getNumber();
                if (number == null || number <= 0) {
                    throw new BusinessException("数量不能为空，且必须大于0");
                }

                saveItem.setNumber(number);
                result.add(saveItem);
            }
        }

        return result;
    }

    List<UnionCardProjectItem> listErpGoodsByVO(CardProjectVO vo) throws Exception {
        List<UnionCardProjectItem> result = new ArrayList<>();
        Date currentDate = DateUtil.getCurrentDate();

        List<UnionCardProjectItem> erpGoodsList = vo.getErpGoodsList();
        if (ListUtil.isNotEmpty(erpGoodsList)) {
            for (UnionCardProjectItem erpGoods : erpGoodsList) {
                UnionCardProjectItem saveItem = new UnionCardProjectItem();
                saveItem.setDelStatus(CommonConstant.COMMON_NO);
                saveItem.setCreateTime(currentDate);
                saveItem.setType(CardConstant.ITEM_TYPE_ERP_GOODS);

                Integer erpType = erpGoods.getErpType();
                if (erpType == null) {
                    throw new BusinessException("ERP类型不能为空");
                }
                saveItem.setErpType(erpType);

                Integer shopId = erpGoods.getShopId();
                if (shopId == null) {
                    throw new BusinessException("门店id不能为空");
                }
                saveItem.setShopId(shopId);

                Integer erpGoodsId = erpGoods.getErpGoodsId();
                if (erpGoodsId == null) {
                    throw new BusinessException("ERP商品项目id不能为空");
                }
                saveItem.setErpTextId(erpGoodsId);

                String name = erpGoods.getName();
                if (StringUtil.isEmpty(name)) {
                    throw new BusinessException("名称不能为空");
                }
                saveItem.setName(name);

                String size = erpGoods.getSize();
                if (StringUtil.isEmpty(size)) {
                    throw new BusinessException("商品规格不能为空");
                }
                saveItem.setSize(size);

                Integer number = erpGoods.getNumber();
                if (number == null || number <= 0) {
                    throw new BusinessException("数量不能为空，且必须大于0");
                }

                saveItem.setNumber(number);
                result.add(saveItem);
            }
        }

        return result;
    }

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

    //***************************************** Domain Driven Design - filter ******************************************

    @Override
    public List<UnionCardProjectItem> filterByType(List<UnionCardProjectItem> itemList, Integer type) throws Exception {
        if (itemList == null || type == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardProjectItem> result = new ArrayList<>();
        for (UnionCardProjectItem item : itemList) {
            if (type.equals(item.getType())) {
                result.add(item);
            }
        }

        return result;
    }

    //***************************************** Object As a Service - get **********************************************

    @Override
    public UnionCardProjectItem getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionCardProjectItem result;
        // (1)cache
        String idKey = UnionCardProjectItemCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionCardProjectItem.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardProjectItem> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

    public List<UnionCardProjectItem> listByProjectId(Integer projectId) throws Exception {
        if (projectId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardProjectItem> result;
        // cache
        String projectIdKey = UnionCardProjectItemCacheUtil.getProjectIdKey(projectId);
        if (redisCacheUtil.exists(projectIdKey)) {
            String tempStr = redisCacheUtil.get(projectIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardProjectItem.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardProjectItem> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("project_id", projectId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, projectId, UnionCardProjectItemCacheUtil.TYPE_PROJECT_ID);
        return result;
    }

    //***************************************** Object As a Service - save *********************************************

    @Transactional(rollbackFor = Exception.class)
    public void save(UnionCardProjectItem newUnionCardProjectItem) throws Exception {
        if (newUnionCardProjectItem == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionCardProjectItem);
        removeCache(newUnionCardProjectItem);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionCardProjectItem> newUnionCardProjectItemList) throws Exception {
        if (newUnionCardProjectItemList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionCardProjectItemList);
        removeCache(newUnionCardProjectItemList);
    }

    //***************************************** Object As a Service - remove *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionCardProjectItem unionCardProjectItem = getById(id);
        removeCache(unionCardProjectItem);
        // (2)remove in db logically
        UnionCardProjectItem removeUnionCardProjectItem = new UnionCardProjectItem();
        removeUnionCardProjectItem.setId(id);
        removeUnionCardProjectItem.setDelStatus(CommonConstant.DEL_STATUS_YES);
        updateById(removeUnionCardProjectItem);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionCardProjectItem> unionCardProjectItemList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardProjectItem unionCardProjectItem = getById(id);
            unionCardProjectItemList.add(unionCardProjectItem);
        }
        removeCache(unionCardProjectItemList);
        // (2)remove in db logically
        List<UnionCardProjectItem> removeUnionCardProjectItemList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardProjectItem removeUnionCardProjectItem = new UnionCardProjectItem();
            removeUnionCardProjectItem.setId(id);
            removeUnionCardProjectItem.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionCardProjectItemList.add(removeUnionCardProjectItem);
        }
        updateBatchById(removeUnionCardProjectItemList);
    }

    //***************************************** Object As a Service - update *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void update(UnionCardProjectItem updateUnionCardProjectItem) throws Exception {
        if (updateUnionCardProjectItem == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionCardProjectItem.getId();
        UnionCardProjectItem unionCardProjectItem = getById(id);
        removeCache(unionCardProjectItem);
        // (2)update db
        updateById(updateUnionCardProjectItem);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionCardProjectItem> updateUnionCardProjectItemList) throws Exception {
        if (updateUnionCardProjectItemList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionCardProjectItem updateUnionCardProjectItem : updateUnionCardProjectItemList) {
            idList.add(updateUnionCardProjectItem.getId());
        }
        List<UnionCardProjectItem> unionCardProjectItemList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardProjectItem unionCardProjectItem = getById(id);
            unionCardProjectItemList.add(unionCardProjectItem);
        }
        removeCache(unionCardProjectItemList);
        // (2)update db
        updateBatchById(updateUnionCardProjectItemList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionCardProjectItem newUnionCardProjectItem, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionCardProjectItemCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionCardProjectItem);
    }

    private void setCache(List<UnionCardProjectItem> newUnionCardProjectItemList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionCardProjectItemCacheUtil.TYPE_PROJECT_ID:
                foreignIdKey = UnionCardProjectItemCacheUtil.getProjectIdKey(foreignId);
                break;
            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionCardProjectItemList);
        }
    }

    private void removeCache(UnionCardProjectItem unionCardProjectItem) {
        if (unionCardProjectItem == null) {
            return;
        }
        Integer id = unionCardProjectItem.getId();
        String idKey = UnionCardProjectItemCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer projectId = unionCardProjectItem.getProjectId();
        if (projectId != null) {
            String projectIdKey = UnionCardProjectItemCacheUtil.getProjectIdKey(projectId);
            redisCacheUtil.remove(projectIdKey);
        }
    }

    private void removeCache(List<UnionCardProjectItem> unionCardProjectItemList) {
        if (ListUtil.isEmpty(unionCardProjectItemList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionCardProjectItem unionCardProjectItem : unionCardProjectItemList) {
            idList.add(unionCardProjectItem.getId());
        }
        List<String> idKeyList = UnionCardProjectItemCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

        List<String> projectIdKeyList = getForeignIdKeyList(unionCardProjectItemList, UnionCardProjectItemCacheUtil.TYPE_PROJECT_ID);
        if (ListUtil.isNotEmpty(projectIdKeyList)) {
            redisCacheUtil.remove(projectIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionCardProjectItem> unionCardProjectItemList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionCardProjectItemCacheUtil.TYPE_PROJECT_ID:
                for (UnionCardProjectItem unionCardProjectItem : unionCardProjectItemList) {
                    Integer projectId = unionCardProjectItem.getProjectId();
                    if (projectId != null) {
                        String projectIdKey = UnionCardProjectItemCacheUtil.getProjectIdKey(projectId);
                        result.add(projectIdKey);
                    }
                }
                break;
            default:
                break;
        }
        return result;
    }

}