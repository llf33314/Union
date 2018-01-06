package com.gt.union.card.project.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.api.client.erp.ErpService;
import com.gt.union.api.client.shop.ShopService;
import com.gt.union.card.activity.constant.ActivityConstant;
import com.gt.union.card.activity.entity.UnionCardActivity;
import com.gt.union.card.activity.service.IUnionCardActivityService;
import com.gt.union.card.consume.service.IUnionConsumeProjectService;
import com.gt.union.card.project.constant.ProjectConstant;
import com.gt.union.card.project.dao.IUnionCardProjectItemDao;
import com.gt.union.card.project.entity.UnionCardProject;
import com.gt.union.card.project.entity.UnionCardProjectFlow;
import com.gt.union.card.project.entity.UnionCardProjectItem;
import com.gt.union.card.project.service.IUnionCardProjectFlowService;
import com.gt.union.card.project.service.IUnionCardProjectItemService;
import com.gt.union.card.project.service.IUnionCardProjectService;
import com.gt.union.card.project.vo.CardProjectItemConsumeVO;
import com.gt.union.card.project.vo.CardProjectVO;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.DateUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.union.main.service.IUnionMainService;
import com.gt.union.union.member.entity.UnionMember;
import com.gt.union.union.member.service.IUnionMemberService;
import com.gt.util.entity.result.shop.WsWxShopInfoExtend;
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
public class UnionCardProjectItemServiceImpl implements IUnionCardProjectItemService {
    @Autowired
    private IUnionCardProjectItemDao unionCardProjectItemDao;

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

    @Autowired
    private IUnionCardProjectFlowService unionCardProjectFlowService;

    @Autowired
    private ErpService erpService;

    @Autowired
    private ShopService shopService;

    //********************************************* Base On Business - get *********************************************

    @Override
    public UnionCardProjectItem getValidByIdAndProjectId(Integer itemId, Integer projectId) throws Exception {
        if (itemId == null || projectId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardProjectItem> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("id", itemId)
                .eq("project_id", projectId);

        return unionCardProjectItemDao.selectOne(entityWrapper);
    }

    //********************************************* Base On Business - list ********************************************

    @Override
    public List<UnionCardProjectItem> listValidByProjectIdAndType(Integer projectId, Integer type) throws Exception {
        if (projectId == null || type == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardProjectItem> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("project_id", projectId)
                .eq("type", type);

        return unionCardProjectItemDao.selectList(entityWrapper);
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
        UnionMember member = unionMemberService.getValidReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
        }
        // （2）	判断activityId有效性
        UnionCardActivity activity = unionCardActivityService.getValidByIdAndUnionId(activityId, unionId);
        if (activity == null) {
            throw new BusinessException("找不到活动信息");
        }
        // （3）	获取activity关联的非ERP文本项目优惠列表
        // （4）	过滤掉可使用数量为0的项目优惠
        List<CardProjectItemConsumeVO> result = new ArrayList<>();
        List<UnionCardProject> projectList = unionCardProjectService.listValidByUnionIdAndMemberIdAndActivityIdAndStatus(unionId, member.getId(), activityId, ProjectConstant.STATUS_ACCEPT);
        if (ListUtil.isNotEmpty(projectList)) {
            for (UnionCardProject project : projectList) {
                List<UnionCardProjectItem> textItemList = listValidByProjectIdAndType(project.getId(), ProjectConstant.TYPE_TEXT);
                if (ListUtil.isNotEmpty(textItemList)) {
                    for (UnionCardProjectItem textItem : textItemList) {
                        Integer consumeItemCount = unionConsumeProjectService.countValidByProjectIdAndProjectItemId(project.getId(), textItem.getId());
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

    //********************************************* Base On Business - save ********************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveProjectItemVOByBusIdAndUnionIdAndActivityId(Integer busId, Integer unionId, Integer activityId, CardProjectVO vo) throws Exception {
        if (busId == null || unionId == null || activityId == null || vo == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性和member写权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getValidReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
        }
        // （2）	判断activityId有效性
        UnionCardActivity activity = unionCardActivityService.getValidByIdAndUnionId(activityId, unionId);
        if (activity == null) {
            throw new BusinessException("找不到活动信息");
        }
        // （3）	要求活动在报名中状态
        Integer activityStatus = unionCardActivityService.getStatus(activity);
        if (ActivityConstant.STATUS_APPLYING != activityStatus) {
            throw new BusinessException("活动卡不在报名中状态，无法操作");
        }
        // （4）	判断是否已有活动项目
        UnionCardProject updateProject = null;
        UnionCardProject saveProject = null;
        List<Integer> removeItemIdList = null;
        UnionCardProject project = unionCardProjectService.getValidByUnionIdAndMemberIdAndActivityId(unionId, member.getId(), activityId);
        Date currentDate = DateUtil.getCurrentDate();
        if (project != null) {
            if (ProjectConstant.STATUS_COMMITTED == project.getStatus()) {
                throw new BusinessException("活动项目已提交");
            }
            if (ProjectConstant.STATUS_ACCEPT == project.getStatus()) {
                throw new BusinessException("活动项目已审核通过");
            }
            updateProject = new UnionCardProject();
            updateProject.setId(project.getId());
            updateProject.setModifyTime(currentDate);
            updateProject.setStatus(ProjectConstant.STATUS_NOT_COMMIT);

            List<UnionCardProjectItem> removeItemList = listValidByProjectId(project.getId());
            removeItemIdList = getIdList(removeItemList);
        } else {
            saveProject = new UnionCardProject();
            saveProject.setDelStatus(CommonConstant.COMMON_NO);
            saveProject.setCreateTime(currentDate);
            saveProject.setActivityId(activityId);
            saveProject.setMemberId(member.getId());
            saveProject.setUnionId(unionId);
            saveProject.setStatus(ProjectConstant.STATUS_NOT_COMMIT);
        }

        List<UnionCardProjectItem> saveItemList = listItemByVO(vo, busId);

        // （5）事务操作
        if (project == null) {
            unionCardProjectService.save(saveProject);

            if (ListUtil.isNotEmpty(saveItemList)) {
                for (UnionCardProjectItem saveItem : saveItemList) {
                    saveItem.setProjectId(saveProject.getId());
                }
                saveBatch(saveItemList);
            }
        } else {
            unionCardProjectService.update(updateProject);

            if (ListUtil.isNotEmpty(removeItemIdList)) {
                removeBatchById(removeItemIdList);
            }

            if (ListUtil.isNotEmpty(saveItemList)) {
                for (UnionCardProjectItem saveItem : saveItemList) {
                    saveItem.setProjectId(project.getId());
                }
                saveBatch(saveItemList);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void commitProjectItemVOByBusIdAndUnionIdAndActivityId(Integer busId, Integer unionId, Integer activityId, CardProjectVO vo) throws Exception {
        if (busId == null || unionId == null || activityId == null || vo == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性和member写权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getValidReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
        }
        // （2）	判断activityId有效性
        UnionCardActivity activity = unionCardActivityService.getValidByIdAndUnionId(activityId, unionId);
        if (activity == null) {
            throw new BusinessException("找不到活动卡信息");
        }
        // （3）	要求活动在报名中状态
        Integer activityStatus = unionCardActivityService.getStatus(activity);
        if (ActivityConstant.STATUS_APPLYING != activityStatus) {
            throw new BusinessException("活动卡不在报名中状态，无法操作");
        }
        // （4）	判断是否已有活动项目
        UnionCardProject updateProject = null;
        UnionCardProject saveProject = null;
        UnionCardProjectFlow saveFlow = null;
        List<Integer> removeItemIdList = null;
        UnionCardProject project = unionCardProjectService.getValidByUnionIdAndMemberIdAndActivityId(unionId, member.getId(), activityId);
        Date currentDate = DateUtil.getCurrentDate();
        if (project != null) {
            if (ProjectConstant.STATUS_COMMITTED == project.getStatus()) {
                throw new BusinessException("活动项目已提交");
            }
            if (ProjectConstant.STATUS_ACCEPT == project.getStatus()) {
                throw new BusinessException("活动项目已审核通过");
            }
            updateProject = new UnionCardProject();
            updateProject.setId(project.getId());
            updateProject.setModifyTime(currentDate);

            List<UnionCardProjectItem> removeItemList = listByProjectId(project.getId());
            removeItemIdList = getIdList(removeItemList);
        } else {
            saveProject = new UnionCardProject();
            saveProject.setDelStatus(CommonConstant.COMMON_NO);
            saveProject.setCreateTime(currentDate);
            saveProject.setActivityId(activityId);
            saveProject.setMemberId(member.getId());
            saveProject.setUnionId(unionId);
        }
        // （5）判断活动是否设置了需要项目审核
        if (updateProject != null) {
            updateProject.setStatus(ActivityConstant.IS_PROJECT_CHECK_YES == activity.getIsProjectCheck()
                    ? ProjectConstant.STATUS_COMMITTED : ProjectConstant.STATUS_ACCEPT);
        }
        if (saveProject != null) {
            saveProject.setStatus(ActivityConstant.IS_PROJECT_CHECK_YES == activity.getIsProjectCheck()
                    ? ProjectConstant.STATUS_COMMITTED : ProjectConstant.STATUS_ACCEPT);
        }
        if (ActivityConstant.IS_PROJECT_CHECK_YES != activity.getIsProjectCheck()) {
            saveFlow = new UnionCardProjectFlow();
            saveFlow.setDelStatus(CommonConstant.DEL_STATUS_NO);
            saveFlow.setCreateTime(currentDate);
            saveFlow.setIllustration("无需审核，自动通过");
        }

        List<UnionCardProjectItem> saveItemList = listItemByVO(vo, busId);
        if (ListUtil.isEmpty(saveItemList)) {
            throw new BusinessException("请填写项目后再提交");
        }

        // （6）事务操作
        if (project != null) {
            unionCardProjectService.update(updateProject);

            if (ListUtil.isNotEmpty(removeItemIdList)) {
                removeBatchById(removeItemIdList);
            }

            if (ListUtil.isNotEmpty(saveItemList)) {
                for (UnionCardProjectItem saveItem : saveItemList) {
                    saveItem.setProjectId(project.getId());
                }
                saveBatch(saveItemList);
            }

            if (saveFlow != null) {
                saveFlow.setProjectId(updateProject.getId());
                unionCardProjectFlowService.save(saveFlow);
            }
        } else {
            unionCardProjectService.save(saveProject);

            if (ListUtil.isNotEmpty(saveItemList)) {
                for (UnionCardProjectItem saveItem : saveItemList) {
                    saveItem.setProjectId(saveProject.getId());
                }
                saveBatch(saveItemList);
            }
        }
    }

    private List<UnionCardProjectItem> listItemByVO(CardProjectVO vo, Integer busId) throws Exception {
        List<UnionCardProjectItem> result = new ArrayList<>();

        if (erpService.userHasErpAuthority(busId)) {
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
                saveItem.setType(ProjectConstant.TYPE_TEXT);

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
                saveItem.setType(ProjectConstant.TYPE_ERP_TEXT);

                Integer erpType = erpText.getErpType();
                if (erpType == null) {
                    throw new BusinessException("ERP类型不能为空");
                }
                saveItem.setErpType(erpType);

                Integer shopId = erpText.getShopId();
                if (shopId == null) {
                    throw new BusinessException("门店id不能为空");
                }
                WsWxShopInfoExtend shop = shopService.getById(shopId);
                if (shop == null) {
                    throw new BusinessException("找不到门店信息");
                }
                saveItem.setShopId(shopId);
                saveItem.setShopName(shop.getBusinessName());

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

    private List<UnionCardProjectItem> listErpGoodsByVO(CardProjectVO vo) throws Exception {
        List<UnionCardProjectItem> result = new ArrayList<>();
        Date currentDate = DateUtil.getCurrentDate();

        List<UnionCardProjectItem> erpGoodsList = vo.getErpGoodsList();
        if (ListUtil.isNotEmpty(erpGoodsList)) {
            for (UnionCardProjectItem erpGoods : erpGoodsList) {
                UnionCardProjectItem saveItem = new UnionCardProjectItem();
                saveItem.setDelStatus(CommonConstant.COMMON_NO);
                saveItem.setCreateTime(currentDate);
                saveItem.setType(ProjectConstant.TYPE_ERP_GOODS);

                Integer erpGoodsId = erpGoods.getErpGoodsId();
                if (erpGoodsId == null) {
                    throw new BusinessException("ERP商品项目id不能为空");
                }
                saveItem.setErpGoodsId(erpGoodsId);

                String name = erpGoods.getName();
                if (StringUtil.isEmpty(name)) {
                    throw new BusinessException("名称不能为空");
                }
                saveItem.setName(name);

                String spec = erpGoods.getSpec();
                if (StringUtil.isEmpty(spec)) {
                    throw new BusinessException("商品规格不能为空");
                }
                saveItem.setSpec(spec);

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

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    //********************************************* Base On Business - other *******************************************

    @Override
    public Integer countValidByProjectId(Integer projectId) throws Exception {
        if (projectId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardProjectItem> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("project_id", projectId);

        return unionCardProjectItemDao.selectCount(entityWrapper);
    }

    @Override
    public Integer countValidCommittedByUnionIdAndActivityId(Integer unionId, Integer activityId) throws Exception {
        if (unionId == null || activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardProject> projectList = unionCardProjectService.listValidByUnionIdAndActivityIdAndStatus(unionId, activityId, ProjectConstant.STATUS_ACCEPT);
        List<Integer> projectIdList = unionCardProjectService.getIdList(projectList);

        Integer result = 0;
        if (ListUtil.isNotEmpty(projectIdList)) {
            EntityWrapper<UnionCardProjectItem> entityWrapper = new EntityWrapper<>();
            entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                    .in("project_id", projectIdList);

            result = unionCardProjectItemDao.selectCount(entityWrapper);
        }

        return result;
    }

    @Override
    public boolean existValidByUnionIdAndMemberIdAndActivityIdAndProjectStatusAndItemType(Integer unionId, Integer memberId, Integer activityId, Integer projectStatus, Integer itemType) throws Exception {
        if (unionId == null || memberId == null || activityId == null || projectStatus == null || itemType == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardProjectItem> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("type", itemType)
                .exists("SELECT p.id FROM t_union_card_project p"
                        + " WHERE p.del_status=" + CommonConstant.DEL_STATUS_NO
                        + " AND p.id=t_union_card_project_item.project_id"
                        + " AND p.union_id=" + unionId
                        + " AND p.member_id=" + memberId
                        + " AND p.activity_id=" + activityId
                        + " AND p.status=" + projectStatus);

        return unionCardProjectItemDao.selectOne(entityWrapper) != null;
    }

    @Override
    public boolean existValidByUnionIdAndMemberIdAndActivityIdListAndProjectStatusAndItemType(Integer unionId, Integer memberId, List<Integer> activityIdList, Integer projectStatus, Integer itemType) throws Exception {
        if (unionId == null || memberId == null || activityIdList == null || projectStatus == null || itemType == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardProjectItem> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("type", itemType)
                .exists("SELECT p.id FROM t_union_card_project p"
                        + " WHERE p.del_status=" + CommonConstant.DEL_STATUS_NO
                        + " AND p.id=t_union_card_project_item.project_id"
                        + " AND p.union_id=" + unionId
                        + " AND p.member_id=" + memberId
                        + " AND p.activity_id in (" + ListUtil.toString(activityIdList) + ")"
                        + " AND p.status=" + projectStatus);

        return unionCardProjectItemDao.selectOne(entityWrapper) != null;
    }

    //********************************************* Base On Business - filter ******************************************

    @Override
    public List<UnionCardProjectItem> filterByDelStatus(List<UnionCardProjectItem> unionCardProjectItemList, Integer delStatus) throws Exception {
        if (delStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardProjectItem> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionCardProjectItemList)) {
            for (UnionCardProjectItem unionCardProjectItem : unionCardProjectItemList) {
                if (delStatus.equals(unionCardProjectItem.getDelStatus())) {
                    result.add(unionCardProjectItem);
                }
            }
        }

        return result;
    }

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

    //********************************************* Object As a Service - get ******************************************

    @Override
    public UnionCardProjectItem getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionCardProjectItemDao.selectById(id);
    }

    @Override
    public UnionCardProjectItem getValidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardProjectItem> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("id", id);

        return unionCardProjectItemDao.selectOne(entityWrapper);
    }

    @Override
    public UnionCardProjectItem getInvalidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardProjectItem> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("id", id);

        return unionCardProjectItemDao.selectOne(entityWrapper);
    }

    //********************************************* Object As a Service - list *****************************************

    @Override
    public List<Integer> getIdList(List<UnionCardProjectItem> unionCardProjectItemList) throws Exception {
        if (unionCardProjectItemList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<Integer> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionCardProjectItemList)) {
            for (UnionCardProjectItem unionCardProjectItem : unionCardProjectItemList) {
                result.add(unionCardProjectItem.getId());
            }
        }

        return result;
    }

    @Override
    public List<UnionCardProjectItem> listByProjectId(Integer projectId) throws Exception {
        if (projectId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardProjectItem> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("project_id", projectId);

        return unionCardProjectItemDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardProjectItem> listValidByProjectId(Integer projectId) throws Exception {
        if (projectId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardProjectItem> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("project_id", projectId);

        return unionCardProjectItemDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardProjectItem> listInvalidByProjectId(Integer projectId) throws Exception {
        if (projectId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardProjectItem> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("project_id", projectId);

        return unionCardProjectItemDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardProjectItem> listByIdList(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardProjectItem> entityWrapper = new EntityWrapper<>();
        entityWrapper.in("id", idList);

        return unionCardProjectItemDao.selectList(entityWrapper);
    }

    @Override
    public Page pageSupport(Page page, EntityWrapper<UnionCardProjectItem> entityWrapper) throws Exception {
        if (page == null || entityWrapper == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionCardProjectItemDao.selectPage(page, entityWrapper);
    }
    //********************************************* Object As a Service - save *****************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(UnionCardProjectItem newUnionCardProjectItem) throws Exception {
        if (newUnionCardProjectItem == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionCardProjectItemDao.insert(newUnionCardProjectItem);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionCardProjectItem> newUnionCardProjectItemList) throws Exception {
        if (newUnionCardProjectItemList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionCardProjectItemDao.insertBatch(newUnionCardProjectItemList);
    }

    //********************************************* Object As a Service - remove ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        UnionCardProjectItem removeUnionCardProjectItem = new UnionCardProjectItem();
        removeUnionCardProjectItem.setId(id);
        removeUnionCardProjectItem.setDelStatus(CommonConstant.DEL_STATUS_YES);
        unionCardProjectItemDao.updateById(removeUnionCardProjectItem);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardProjectItem> removeUnionCardProjectItemList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardProjectItem removeUnionCardProjectItem = new UnionCardProjectItem();
            removeUnionCardProjectItem.setId(id);
            removeUnionCardProjectItem.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionCardProjectItemList.add(removeUnionCardProjectItem);
        }
        unionCardProjectItemDao.updateBatchById(removeUnionCardProjectItemList);
    }

    //********************************************* Object As a Service - update ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UnionCardProjectItem updateUnionCardProjectItem) throws Exception {
        if (updateUnionCardProjectItem == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionCardProjectItemDao.updateById(updateUnionCardProjectItem);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionCardProjectItem> updateUnionCardProjectItemList) throws Exception {
        if (updateUnionCardProjectItemList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionCardProjectItemDao.updateBatchById(updateUnionCardProjectItemList);
    }

}