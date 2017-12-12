package com.gt.union.union.main.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.DateUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.union.main.entity.UnionMainNotice;
import com.gt.union.union.main.mapper.UnionMainNoticeMapper;
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
public class UnionMainNoticeServiceImpl extends ServiceImpl<UnionMainNoticeMapper, UnionMainNotice> implements IUnionMainNoticeService {
    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private IUnionMemberService unionMemberService;

    //***************************************** Domain Driven Design - get *********************************************

    @Override
    public UnionMainNotice getByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception {
        if (busId == null && unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        // （1）检查union有效性和member读权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_READ_REJECT);
        }

        return getByUnionId(unionId);
    }

    @Override
    public UnionMainNotice getByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);   
        }

        List<UnionMainNotice> result = listByUnionId(unionId);

        return ListUtil.isNotEmpty(result) ? result.get(0) : null;
    }

    //***************************************** Domain Driven Design - list ********************************************

    //***************************************** Domain Driven Design - save ********************************************

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    @Override
    public void updateContentByBusIdAndUnionId(Integer busId, Integer unionId, String content) throws Exception {
        if (busId == null || unionId == null || content == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        // （1）判断union有效性和member写权限、盟主权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getWriteByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_WRITE_REJECT);
        }
        if (member.getIsUnionOwner() != MemberConstant.IS_UNION_OWNER_YES) {
            throw new BusinessException(CommonConstant.UNION_NEED_OWNER);
        }

        // （2）要求公告内容不能为空，且字数不能超过50字
        if (StringUtil.isEmpty(content) || StringUtil.getStringLength(content) > 50) {
            throw new BusinessException("公告内容不能为空，且字数不能大于50");
        }

        // （3）如果原公告不存在，则新增；否则，更新
        UnionMainNotice notice = getByUnionId(unionId);
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

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

    //***************************************** Object As a Service - get **********************************************

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
        EntityWrapper<UnionMainNotice> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

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
        entityWrapper.eq("union_id", unionId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, unionId, UnionMainNoticeCacheUtil.TYPE_UNION_ID);
        return result;
    }

    //***************************************** Object As a Service - save *********************************************

    @Transactional(rollbackFor = Exception.class)
    public void save(UnionMainNotice newUnionMainNotice) throws Exception {
        if (newUnionMainNotice == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionMainNotice);
        removeCache(newUnionMainNotice);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionMainNotice> newUnionMainNoticeList) throws Exception {
        if (newUnionMainNoticeList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionMainNoticeList);
        removeCache(newUnionMainNoticeList);
    }

    //***************************************** Object As a Service - remove *******************************************

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
        updateById(removeUnionMainNotice);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionMainNotice> unionMainNoticeList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMainNotice unionMainNotice = getById(id);
            unionMainNoticeList.add(unionMainNotice);
        }
        removeCache(unionMainNoticeList);
        // (2)remove in db logically
        List<UnionMainNotice> removeUnionMainNoticeList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMainNotice removeUnionMainNotice = new UnionMainNotice();
            removeUnionMainNotice.setId(id);
            removeUnionMainNotice.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionMainNoticeList.add(removeUnionMainNotice);
        }
        updateBatchById(removeUnionMainNoticeList);
    }

    //***************************************** Object As a Service - update *******************************************

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
        updateById(updateUnionMainNotice);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionMainNotice> updateUnionMainNoticeList) throws Exception {
        if (updateUnionMainNoticeList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionMainNotice updateUnionMainNotice : updateUnionMainNoticeList) {
            idList.add(updateUnionMainNotice.getId());
        }
        List<UnionMainNotice> unionMainNoticeList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMainNotice unionMainNotice = getById(id);
            unionMainNoticeList.add(unionMainNotice);
        }
        removeCache(unionMainNoticeList);
        // (2)update db
        updateBatchById(updateUnionMainNoticeList);
    }

    //***************************************** Object As a Service - cache support ************************************

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
                    }
                }
                break;
            default:
                break;
        }
        return result;
    }

}