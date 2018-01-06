package com.gt.union.union.main.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.DateUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.union.main.constant.UnionConstant;
import com.gt.union.union.main.dao.IUnionMainDao;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.main.entity.UnionMainDict;
import com.gt.union.union.main.service.IUnionMainDictService;
import com.gt.union.union.main.service.IUnionMainService;
import com.gt.union.union.main.util.UnionMainCacheUtil;
import com.gt.union.union.main.vo.UnionVO;
import com.gt.union.union.member.constant.MemberConstant;
import com.gt.union.union.member.entity.UnionMember;
import com.gt.union.union.member.service.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 联盟 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 15:26:16
 */
@Service
public class UnionMainServiceImpl implements IUnionMainService {
    @Autowired
    private IUnionMainDao unionMainDao;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IUnionMainDictService unionMainDictService;

    @Autowired
    private IUnionMemberService unionMemberService;

    //********************************************* Base On Business - get *********************************************

    @Override
    public UnionVO getUnionVOByBusIdAndId(Integer busId, Integer unionId) throws Exception {
        if (busId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性和member读权限
        UnionMain union = getValidById(unionId);
        if (!isUnionValid(union)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getValidReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
        }

        UnionVO result = new UnionVO();
        result.setUnion(union);

        List<UnionMainDict> itemList = unionMainDictService.listValidByUnionId(unionId);
        result.setItemList(itemList);

        return result;
    }

    @Override
    public List<UnionMain> listValidJoinByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）获取我创建和已加入的联盟id列表
        List<UnionMember> memberList = unionMemberService.listValidByBusId(busId);
        List<Integer> unionIdList = unionMemberService.getUnionIdList(memberList);
        // （2）缓存穿透：获取所有有效的，但不包括我创建和加入的联盟列表信息
        EntityWrapper<UnionMain> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.COMMON_NO)
                .gt("validity", DateUtil.getCurrentDate())
                .notIn("id", unionIdList)
                .orderBy("create_time", true);

        return unionMainDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionMain> listReadByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMember> readMemberList = unionMemberService.listReadByBusId(busId);
        List<Integer> unionIdList = unionMemberService.getUnionIdList(readMemberList);
        unionIdList = ListUtil.unique(unionIdList);

        return listByIdList(unionIdList);
    }


    @Override
    public List<UnionMain> listValidReadByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMain> result = listReadByBusId(busId);
        result = filterByDelStatus(result, CommonConstant.DEL_STATUS_NO);

        return result;
    }

    //********************************************* Base On Business - list ********************************************

    //********************************************* Base On Business - save ********************************************

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUnionVOByBusIdAndId(Integer busId, Integer unionId, UnionVO vo) throws Exception {
        if (busId == null || unionId == null || vo == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性和member读权限、盟主权限
        if (!isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getValidWriteByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
        }
        if (MemberConstant.IS_UNION_OWNER_YES != member.getIsUnionOwner()) {
            throw new BusinessException(CommonConstant.UNION_OWNER_ERROR);
        }
        // （2）	校验表单
        UnionMain updateUnion = new UnionMain();
        updateUnion.setId(unionId);
        updateUnion.setModifyTime(DateUtil.getCurrentDate());
        UnionMain voUnion = vo.getUnion();
        if (voUnion == null) {
            throw new BusinessException("请填写联盟设置信息");
        }
        // （2-1）联盟名称
        String voUnionName = voUnion.getName();
        if (StringUtil.isEmpty(voUnionName)) {
            throw new BusinessException("联盟名称不能为空");
        }
        if (StringUtil.getStringLength(voUnionName) > 10) {
            throw new BusinessException("联盟名称字数不能大于10");
        }
        updateUnion.setName(voUnionName);
        // （2-2）联盟图标
        String voUnionImg = voUnion.getImg();
        if (StringUtil.isEmpty(voUnionImg)) {
            throw new BusinessException("联盟图标不能为空");
        }
        updateUnion.setImg(voUnionImg);
        // （2-3）联盟说明
        String voUnionIllustration = voUnion.getIllustration();
        if (StringUtil.isEmpty(voUnionIllustration)) {
            throw new BusinessException("联盟说明不能为空");
        }
        if (StringUtil.getStringLength(voUnionIllustration) > 30) {
            throw new BusinessException("联盟说明字数不能大于30");
        }
        updateUnion.setIllustration(voUnionIllustration);
        // （2-4）加盟方式
        Integer voUnionJoinType = voUnion.getJoinType();
        if (voUnionJoinType == null) {
            throw new BusinessException("加盟方式不能为空");
        }
        if (UnionConstant.JOIN_TYPE_RECOMMEND != voUnionJoinType && UnionConstant.JOIN_TYPE_APPLY_RECOMMEND != voUnionJoinType) {
            throw new BusinessException("加盟方式参数值有误");
        }
        updateUnion.setJoinType(voUnionJoinType);
        // （2-5）是否开启积分
        UnionMain union = getById(unionId);
        if (UnionConstant.IS_INTEGRAL_YES != union.getIsIntegral()) {
            Integer voUnionIsIntegral = voUnion.getIsIntegral();
            if (UnionConstant.IS_INTEGRAL_YES == voUnionIsIntegral) {
                updateUnion.setIsIntegral(UnionConstant.IS_INTEGRAL_YES);
            }
        }
        // （2-6）入盟申请必填信息
        List<UnionMainDict> dictList = unionMainDictService.listValidByUnionId(unionId);
        List<Integer> removeDictIdList = unionMainDictService.getIdList(dictList);

        List<UnionMainDict> saveDictList = vo.getItemList();
        for (UnionMainDict dict : saveDictList) {
            if (!unionId.equals(dict.getUnionId())) {
                throw new BusinessException("入盟申请必填信息不能绑定到其他联盟中");
            }
        }

        // 事务操作
        update(updateUnion);
        if (ListUtil.isNotEmpty(removeDictIdList)) {
            unionMainDictService.removeBatchById(removeDictIdList);
        }
        if (ListUtil.isNotEmpty(saveDictList)) {
            Date currentDate = DateUtil.getCurrentDate();
            for (UnionMainDict saveDict : saveDictList) {
                saveDict.setDelStatus(CommonConstant.DEL_STATUS_NO);
                saveDict.setCreateTime(currentDate);
            }
            unionMainDictService.saveBatch(saveDictList);
        }
    }

    //********************************************* Base On Business - other *******************************************

    @Override
    public Integer countSurplusByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMain union = getValidById(unionId);
        if (!isUnionValid(union)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        List<UnionMember> memberList = unionMemberService.listValidReadByUnionId(unionId);

        Integer memberLimit = union.getMemberLimit() != null ? union.getMemberLimit() : 0;
        Integer memberCount = ListUtil.isNotEmpty(memberList) ? memberList.size() : 0;

        return memberLimit - memberCount;
    }

    @Override
    public boolean isUnionValid(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        UnionMain union = getValidById(unionId);

        return isUnionValid(union);
    }

    @Override
    public boolean isUnionValid(UnionMain union) throws Exception {
        if (union == null) {
            throw new ParamException(CommonConstant.UNION_INVALID);
        }

        return union.getValidity().compareTo(DateUtil.getCurrentDate()) >= 0;
    }

    //********************************************* Base On Business - filter ******************************************

    @Override
    public List<UnionMain> filterByDelStatus(List<UnionMain> unionMainList, Integer delStatus) throws Exception {
        if (delStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMain> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionMainList)) {
            for (UnionMain unionMain : unionMainList) {
                if (delStatus.equals(unionMain.getDelStatus())) {
                    result.add(unionMain);
                }
            }
        }

        return result;
    }

    //********************************************* Object As a Service - get ******************************************

    @Override
    public UnionMain getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMain result;
        // (1)cache
        String idKey = UnionMainCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionMain.class);
            return result;
        }
        // (2)db
        result = unionMainDao.selectById(id);
        setCache(result, id);
        return result;
    }

    @Override
    public UnionMain getValidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMain result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_NO == result.getDelStatus() ? result : null;
    }

    @Override
    public UnionMain getInvalidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMain result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_YES == result.getDelStatus() ? result : null;
    }

    //********************************************* Object As a Service - list *****************************************

    @Override
    public List<Integer> getIdList(List<UnionMain> unionMainList) throws Exception {
        if (unionMainList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<Integer> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionMainList)) {
            for (UnionMain unionMain : unionMainList) {
                result.add(unionMain.getId());
            }
        }

        return result;
    }

    @Override
    public List<UnionMain> listByIdList(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMain> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(idList)) {
            for (Integer id : idList) {
                result.add(getById(id));
            }
        }

        return result;
    }

    @Override
    public List<UnionMain> listSupport(EntityWrapper<UnionMain> entityWrapper) throws Exception {
        if (entityWrapper == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionMainDao.selectList(entityWrapper);
    }

    @Override
    public Page pageSupport(Page page, EntityWrapper<UnionMain> entityWrapper) throws Exception {
        if (page == null || entityWrapper == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionMainDao.selectPage(page, entityWrapper);
    }
    //********************************************* Object As a Service - save *****************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(UnionMain newUnionMain) throws Exception {
        if (newUnionMain == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        unionMainDao.insert(newUnionMain);
        removeCache(newUnionMain);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionMain> newUnionMainList) throws Exception {
        if (newUnionMainList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        unionMainDao.insertBatch(newUnionMainList);
        removeCache(newUnionMainList);
    }

    //********************************************* Object As a Service - remove ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionMain unionMain = getById(id);
        removeCache(unionMain);
        // (2)remove in db logically
        UnionMain removeUnionMain = new UnionMain();
        removeUnionMain.setId(id);
        removeUnionMain.setDelStatus(CommonConstant.DEL_STATUS_YES);
        unionMainDao.updateById(removeUnionMain);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionMain> unionMainList = listByIdList(idList);
        removeCache(unionMainList);
        // (2)remove in db logically
        List<UnionMain> removeUnionMainList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMain removeUnionMain = new UnionMain();
            removeUnionMain.setId(id);
            removeUnionMain.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionMainList.add(removeUnionMain);
        }
        unionMainDao.updateBatchById(removeUnionMainList);
    }

    //********************************************* Object As a Service - update ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UnionMain updateUnionMain) throws Exception {
        if (updateUnionMain == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionMain.getId();
        UnionMain unionMain = getById(id);
        removeCache(unionMain);
        // (2)update db
        unionMainDao.updateById(updateUnionMain);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionMain> updateUnionMainList) throws Exception {
        if (updateUnionMainList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = getIdList(updateUnionMainList);
        List<UnionMain> unionMainList = listByIdList(idList);
        removeCache(unionMainList);
        // (2)update db
        unionMainDao.updateBatchById(updateUnionMainList);
    }

    //********************************************* Object As a Service - cache support ********************************

    private void setCache(UnionMain newUnionMain, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionMainCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionMain);
    }

    private void setCache(List<UnionMain> newUnionMainList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {

            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionMainList);
        }
    }

    private void setValidCache(List<UnionMain> newUnionMainList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String validForeignIdKey = null;
        switch (foreignIdType) {

            default:
                break;
        }
        if (validForeignIdKey != null) {
            redisCacheUtil.set(validForeignIdKey, newUnionMainList);
        }
    }

    private void setInvalidCache(List<UnionMain> newUnionMainList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String invalidForeignIdKey = null;
        switch (foreignIdType) {

            default:
                break;
        }
        if (invalidForeignIdKey != null) {
            redisCacheUtil.set(invalidForeignIdKey, newUnionMainList);
        }
    }

    private void removeCache(UnionMain unionMain) {
        if (unionMain == null) {
            return;
        }
        Integer id = unionMain.getId();
        String idKey = UnionMainCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

    }

    private void removeCache(List<UnionMain> unionMainList) {
        if (ListUtil.isEmpty(unionMainList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionMain unionMain : unionMainList) {
            idList.add(unionMain.getId());
        }
        List<String> idKeyList = UnionMainCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

    }

    private List<String> getForeignIdKeyList(List<UnionMain> unionMainList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {

            default:
                break;
        }
        return result;
    }

}