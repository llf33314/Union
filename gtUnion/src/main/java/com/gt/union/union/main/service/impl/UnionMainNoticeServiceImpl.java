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
import com.gt.union.union.main.dao.IUnionMainNoticeDao;
import com.gt.union.union.main.entity.UnionMainNotice;
import com.gt.union.union.main.service.IUnionMainNoticeService;
import com.gt.union.union.main.service.IUnionMainService;
import com.gt.union.union.main.util.UnionMainNoticeCacheUtil;
import com.gt.union.union.member.constant.MemberConstant;
import com.gt.union.union.member.entity.UnionMember;
import com.gt.union.union.member.service.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 联盟公告 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 15:18:52
 */
@Service
public class UnionMainNoticeServiceImpl implements IUnionMainNoticeService {
    @Autowired
    private IUnionMainNoticeDao unionMainNoticeDao;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private IUnionMemberService unionMemberService;

    //********************************************* Base On Business - get *********************************************

    //********************************************* Base On Business - list ********************************************

    @Override
    public UnionMainNotice getValidByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception {
        if (busId == null && unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        // （1）检查union有效性和member读权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getValidReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
        }

        return getValidByUnionId(unionId);
    }

    @Override
    public UnionMainNotice getValidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMainNotice> result = listValidByUnionId(unionId);

        return ListUtil.isNotEmpty(result) ? result.get(0) : null;
    }

    //********************************************* Base On Business - save ********************************************

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    @Override
    public void updateByBusIdAndUnionId(Integer busId, Integer unionId, String content) throws Exception {
        if (busId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        // （1）判断union有效性和member读权限、盟主权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getValidReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
        }
        if (member.getIsUnionOwner() != MemberConstant.IS_UNION_OWNER_YES) {
            throw new BusinessException(CommonConstant.UNION_OWNER_ERROR);
        }

        // （2）公告内容字数不能超过50字
        if (StringUtil.getStringLength(content) > 160) {
            throw new BusinessException("公告内容不能为空，且字数不能大于160");
        }

        // （3）判断原公告是否存在：
        //   （3-1）如果存在，则更新；
        //   （3-2）如果不存在，则新增
        UnionMainNotice notice = getValidByUnionId(unionId);
        if (notice != null) {
            UnionMainNotice updateNotice = new UnionMainNotice();
            updateNotice.setId(notice.getId());
            updateNotice.setModifyTime(DateUtil.getCurrentDate());
            updateNotice.setContent(content);
            update(updateNotice);
        } else {
            UnionMainNotice saveNotice = new UnionMainNotice();
            saveNotice.setDelStatus(CommonConstant.DEL_STATUS_NO);
            saveNotice.setCreateTime(DateUtil.getCurrentDate());
            saveNotice.setUnionId(unionId);
            saveNotice.setContent(content);
            save(saveNotice);
        }
    }

    //********************************************* Base On Business - other *******************************************

    //********************************************* Base On Business - filter ******************************************

    @Override
    public List<UnionMainNotice> filterByDelStatus(List<UnionMainNotice> unionMainNoticeList, Integer delStatus) throws Exception {
        if (delStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMainNotice> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionMainNoticeList)) {
            for (UnionMainNotice unionMainNotice : unionMainNoticeList) {
                if (delStatus.equals(unionMainNotice.getDelStatus())) {
                    result.add(unionMainNotice);
                }
            }
        }

        return result;
    }

    //********************************************* Object As a Service - get ******************************************

    @Override
    public UnionMainNotice getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMainNotice result;
        // (1)cache
        String idKey = UnionMainNoticeCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionMainNotice.class);
            return result;
        }
        // (2)db
        result = unionMainNoticeDao.selectById(id);
        setCache(result, id);
        return result;
    }

    @Override
    public UnionMainNotice getValidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMainNotice result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_NO == result.getDelStatus() ? result : null;
    }

    @Override
    public UnionMainNotice getInvalidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMainNotice result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_YES == result.getDelStatus() ? result : null;
    }

    //********************************************* Object As a Service - list *****************************************

    @Override
    public List<Integer> getIdList(List<UnionMainNotice> unionMainNoticeList) throws Exception {
        if (unionMainNoticeList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<Integer> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionMainNoticeList)) {
            for (UnionMainNotice unionMainNotice : unionMainNoticeList) {
                result.add(unionMainNotice.getId());
            }
        }

        return result;
    }

    @Override
    public List<UnionMainNotice> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainNotice> result;
        // (1)cache
        String unionIdKey = UnionMainNoticeCacheUtil.getUnionIdKey(unionId);
        if (redisCacheUtil.exists(unionIdKey)) {
            String tempStr = redisCacheUtil.get(unionIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainNotice.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainNotice> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId);
        result = unionMainNoticeDao.selectList(entityWrapper);
        setCache(result, unionId, UnionMainNoticeCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
    public List<UnionMainNotice> listValidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainNotice> result;
        // (1)cache
        String validUnionIdKey = UnionMainNoticeCacheUtil.getValidUnionIdKey(unionId);
        if (redisCacheUtil.exists(validUnionIdKey)) {
            String tempStr = redisCacheUtil.get(validUnionIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainNotice.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainNotice> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId);
        result = unionMainNoticeDao.selectList(entityWrapper);
        setValidCache(result, unionId, UnionMainNoticeCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
    public List<UnionMainNotice> listInvalidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainNotice> result;
        // (1)cache
        String invalidUnionIdKey = UnionMainNoticeCacheUtil.getInvalidUnionIdKey(unionId);
        if (redisCacheUtil.exists(invalidUnionIdKey)) {
            String tempStr = redisCacheUtil.get(invalidUnionIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainNotice.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainNotice> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("union_id", unionId);
        result = unionMainNoticeDao.selectList(entityWrapper);
        setInvalidCache(result, unionId, UnionMainNoticeCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
    public List<UnionMainNotice> listByIdList(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMainNotice> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(idList)) {
            for (Integer id : idList) {
                result.add(getById(id));
            }
        }

        return result;
    }

    @Override
    public Page pageSupport(Page page, EntityWrapper<UnionMainNotice> entityWrapper) throws Exception {
        if (page == null || entityWrapper == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionMainNoticeDao.selectPage(page, entityWrapper);
    }
    //********************************************* Object As a Service - save *****************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(UnionMainNotice newUnionMainNotice) throws Exception {
        if (newUnionMainNotice == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        unionMainNoticeDao.insert(newUnionMainNotice);
        removeCache(newUnionMainNotice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionMainNotice> newUnionMainNoticeList) throws Exception {
        if (newUnionMainNoticeList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        unionMainNoticeDao.insertBatch(newUnionMainNoticeList);
        removeCache(newUnionMainNoticeList);
    }

    //********************************************* Object As a Service - remove ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionMainNotice unionMainNotice = getById(id);
        removeCache(unionMainNotice);
        // (2)remove in db logically
        UnionMainNotice removeUnionMainNotice = new UnionMainNotice();
        removeUnionMainNotice.setId(id);
        removeUnionMainNotice.setDelStatus(CommonConstant.DEL_STATUS_YES);
        unionMainNoticeDao.updateById(removeUnionMainNotice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionMainNotice> unionMainNoticeList = listByIdList(idList);
        removeCache(unionMainNoticeList);
        // (2)remove in db logically
        List<UnionMainNotice> removeUnionMainNoticeList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMainNotice removeUnionMainNotice = new UnionMainNotice();
            removeUnionMainNotice.setId(id);
            removeUnionMainNotice.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionMainNoticeList.add(removeUnionMainNotice);
        }
        unionMainNoticeDao.updateBatchById(removeUnionMainNoticeList);
    }

    //********************************************* Object As a Service - update ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UnionMainNotice updateUnionMainNotice) throws Exception {
        if (updateUnionMainNotice == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionMainNotice.getId();
        UnionMainNotice unionMainNotice = getById(id);
        removeCache(unionMainNotice);
        // (2)update db
        unionMainNoticeDao.updateById(updateUnionMainNotice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionMainNotice> updateUnionMainNoticeList) throws Exception {
        if (updateUnionMainNoticeList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = getIdList(updateUnionMainNoticeList);
        List<UnionMainNotice> unionMainNoticeList = listByIdList(idList);
        removeCache(unionMainNoticeList);
        // (2)update db
        unionMainNoticeDao.updateBatchById(updateUnionMainNoticeList);
    }

    //********************************************* Object As a Service - cache support ********************************

    private void setCache(UnionMainNotice newUnionMainNotice, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionMainNoticeCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionMainNotice);
    }

    private void setCache(List<UnionMainNotice> newUnionMainNoticeList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionMainNoticeCacheUtil.TYPE_UNION_ID:
                foreignIdKey = UnionMainNoticeCacheUtil.getUnionIdKey(foreignId);
                break;

            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionMainNoticeList);
        }
    }

    private void setValidCache(List<UnionMainNotice> newUnionMainNoticeList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String validForeignIdKey = null;
        switch (foreignIdType) {
            case UnionMainNoticeCacheUtil.TYPE_UNION_ID:
                validForeignIdKey = UnionMainNoticeCacheUtil.getValidUnionIdKey(foreignId);
                break;

            default:
                break;
        }
        if (validForeignIdKey != null) {
            redisCacheUtil.set(validForeignIdKey, newUnionMainNoticeList);
        }
    }

    private void setInvalidCache(List<UnionMainNotice> newUnionMainNoticeList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String invalidForeignIdKey = null;
        switch (foreignIdType) {
            case UnionMainNoticeCacheUtil.TYPE_UNION_ID:
                invalidForeignIdKey = UnionMainNoticeCacheUtil.getInvalidUnionIdKey(foreignId);
                break;

            default:
                break;
        }
        if (invalidForeignIdKey != null) {
            redisCacheUtil.set(invalidForeignIdKey, newUnionMainNoticeList);
        }
    }

    private void removeCache(UnionMainNotice unionMainNotice) {
        if (unionMainNotice == null) {
            return;
        }
        Integer id = unionMainNotice.getId();
        String idKey = UnionMainNoticeCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer unionId = unionMainNotice.getUnionId();
        if (unionId != null) {
            String unionIdKey = UnionMainNoticeCacheUtil.getUnionIdKey(unionId);
            redisCacheUtil.remove(unionIdKey);

            String validUnionIdKey = UnionMainNoticeCacheUtil.getValidUnionIdKey(unionId);
            redisCacheUtil.remove(validUnionIdKey);

            String invalidUnionIdKey = UnionMainNoticeCacheUtil.getInvalidUnionIdKey(unionId);
            redisCacheUtil.remove(invalidUnionIdKey);
        }

    }

    private void removeCache(List<UnionMainNotice> unionMainNoticeList) {
        if (ListUtil.isEmpty(unionMainNoticeList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionMainNotice unionMainNotice : unionMainNoticeList) {
            idList.add(unionMainNotice.getId());
        }
        List<String> idKeyList = UnionMainNoticeCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

        List<String> unionIdKeyList = getForeignIdKeyList(unionMainNoticeList, UnionMainNoticeCacheUtil.TYPE_UNION_ID);
        if (ListUtil.isNotEmpty(unionIdKeyList)) {
            redisCacheUtil.remove(unionIdKeyList);
        }

    }

    private List<String> getForeignIdKeyList(List<UnionMainNotice> unionMainNoticeList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionMainNoticeCacheUtil.TYPE_UNION_ID:
                for (UnionMainNotice unionMainNotice : unionMainNoticeList) {
                    Integer unionId = unionMainNotice.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = UnionMainNoticeCacheUtil.getUnionIdKey(unionId);
                        result.add(unionIdKey);

                        String validUnionIdKey = UnionMainNoticeCacheUtil.getValidUnionIdKey(unionId);
                        result.add(validUnionIdKey);

                        String invalidUnionIdKey = UnionMainNoticeCacheUtil.getInvalidUnionIdKey(unionId);
                        result.add(invalidUnionIdKey);
                    }
                }
                break;

            default:
                break;
        }
        return result;
    }

}