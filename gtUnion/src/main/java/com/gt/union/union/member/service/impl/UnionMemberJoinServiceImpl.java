package com.gt.union.union.member.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.union.api.client.user.IBusUserService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.DateUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.union.main.constant.UnionConstant;
import com.gt.union.union.main.service.IUnionMainDictService;
import com.gt.union.union.main.service.IUnionMainService;
import com.gt.union.union.member.constant.MemberConstant;
import com.gt.union.union.member.dao.IUnionMemberJoinDao;
import com.gt.union.union.member.entity.UnionMember;
import com.gt.union.union.member.entity.UnionMemberJoin;
import com.gt.union.union.member.service.IUnionMemberJoinService;
import com.gt.union.union.member.service.IUnionMemberService;
import com.gt.union.union.member.util.UnionMemberJoinCacheUtil;
import com.gt.union.union.member.vo.MemberJoinCreateVO;
import com.gt.union.union.member.vo.MemberJoinVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 入盟申请 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 11:45:12
 */
@Service
public class UnionMemberJoinServiceImpl implements IUnionMemberJoinService {
    @Autowired
    private IUnionMemberJoinDao unionMemberJoinDao;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionMainDictService unionMainDictService;

    @Autowired
    private IBusUserService busUserService;

    //********************************************* Base On Business - get *********************************************

    //********************************************* Base On Business - list ********************************************


    //********************************************* Base On Business - save ********************************************

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    //********************************************* Base On Business - other *******************************************

    //********************************************* Base On Business - filter ******************************************

    @Override
    public List<UnionMemberJoin> filterByDelStatus(List<UnionMemberJoin> unionMemberJoinList, Integer delStatus) throws Exception {
        if (delStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMemberJoin> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionMemberJoinList)) {
            for (UnionMemberJoin unionMemberJoin : unionMemberJoinList) {
                if (delStatus.equals(unionMemberJoin.getDelStatus())) {
                    result.add(unionMemberJoin);
                }
            }
        }

        return result;
    }

    //********************************************* Object As a Service - get ******************************************

    @Override
    public UnionMemberJoin getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMemberJoin result;
        // (1)cache
        String idKey = UnionMemberJoinCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionMemberJoin.class);
            return result;
        }
        // (2)db
        result = unionMemberJoinDao.selectById(id);
        setCache(result, id);
        return result;
    }

    @Override
    public UnionMemberJoin getValidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMemberJoin result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_NO == result.getDelStatus() ? result : null;
    }

    @Override
    public UnionMemberJoin getInvalidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMemberJoin result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_YES == result.getDelStatus() ? result : null;
    }

    //********************************************* Object As a Service - list *****************************************

    @Override
    public List<Integer> getIdList(List<UnionMemberJoin> unionMemberJoinList) throws Exception {
        if (unionMemberJoinList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<Integer> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionMemberJoinList)) {
            for (UnionMemberJoin unionMemberJoin : unionMemberJoinList) {
                result.add(unionMemberJoin.getId());
            }
        }

        return result;
    }

    @Override
    public List<UnionMemberJoin> listByApplyMemberId(Integer applyMemberId) throws Exception {
        if (applyMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMemberJoin> result;
        // (1)cache
        String applyMemberIdKey = UnionMemberJoinCacheUtil.getApplyMemberIdKey(applyMemberId);
        if (redisCacheUtil.exists(applyMemberIdKey)) {
            String tempStr = redisCacheUtil.get(applyMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionMemberJoin.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMemberJoin> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("apply_member_id", applyMemberId);
        result = unionMemberJoinDao.selectList(entityWrapper);
        setCache(result, applyMemberId, UnionMemberJoinCacheUtil.TYPE_APPLY_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionMemberJoin> listValidByApplyMemberId(Integer applyMemberId) throws Exception {
        if (applyMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMemberJoin> result;
        // (1)cache
        String validApplyMemberIdKey = UnionMemberJoinCacheUtil.getValidApplyMemberIdKey(applyMemberId);
        if (redisCacheUtil.exists(validApplyMemberIdKey)) {
            String tempStr = redisCacheUtil.get(validApplyMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionMemberJoin.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMemberJoin> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("apply_member_id", applyMemberId);
        result = unionMemberJoinDao.selectList(entityWrapper);
        setValidCache(result, applyMemberId, UnionMemberJoinCacheUtil.TYPE_APPLY_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionMemberJoin> listInvalidByApplyMemberId(Integer applyMemberId) throws Exception {
        if (applyMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMemberJoin> result;
        // (1)cache
        String invalidApplyMemberIdKey = UnionMemberJoinCacheUtil.getInvalidApplyMemberIdKey(applyMemberId);
        if (redisCacheUtil.exists(invalidApplyMemberIdKey)) {
            String tempStr = redisCacheUtil.get(invalidApplyMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionMemberJoin.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMemberJoin> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("apply_member_id", applyMemberId);
        result = unionMemberJoinDao.selectList(entityWrapper);
        setInvalidCache(result, applyMemberId, UnionMemberJoinCacheUtil.TYPE_APPLY_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionMemberJoin> listByRecommendMemberId(Integer recommendMemberId) throws Exception {
        if (recommendMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMemberJoin> result;
        // (1)cache
        String recommendMemberIdKey = UnionMemberJoinCacheUtil.getRecommendMemberIdKey(recommendMemberId);
        if (redisCacheUtil.exists(recommendMemberIdKey)) {
            String tempStr = redisCacheUtil.get(recommendMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionMemberJoin.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMemberJoin> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("recommend_member_id", recommendMemberId);
        result = unionMemberJoinDao.selectList(entityWrapper);
        setCache(result, recommendMemberId, UnionMemberJoinCacheUtil.TYPE_RECOMMEND_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionMemberJoin> listValidByRecommendMemberId(Integer recommendMemberId) throws Exception {
        if (recommendMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMemberJoin> result;
        // (1)cache
        String validRecommendMemberIdKey = UnionMemberJoinCacheUtil.getValidRecommendMemberIdKey(recommendMemberId);
        if (redisCacheUtil.exists(validRecommendMemberIdKey)) {
            String tempStr = redisCacheUtil.get(validRecommendMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionMemberJoin.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMemberJoin> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("recommend_member_id", recommendMemberId);
        result = unionMemberJoinDao.selectList(entityWrapper);
        setValidCache(result, recommendMemberId, UnionMemberJoinCacheUtil.TYPE_RECOMMEND_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionMemberJoin> listInvalidByRecommendMemberId(Integer recommendMemberId) throws Exception {
        if (recommendMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMemberJoin> result;
        // (1)cache
        String invalidRecommendMemberIdKey = UnionMemberJoinCacheUtil.getInvalidRecommendMemberIdKey(recommendMemberId);
        if (redisCacheUtil.exists(invalidRecommendMemberIdKey)) {
            String tempStr = redisCacheUtil.get(invalidRecommendMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionMemberJoin.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMemberJoin> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("recommend_member_id", recommendMemberId);
        result = unionMemberJoinDao.selectList(entityWrapper);
        setInvalidCache(result, recommendMemberId, UnionMemberJoinCacheUtil.TYPE_RECOMMEND_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionMemberJoin> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMemberJoin> result;
        // (1)cache
        String unionIdKey = UnionMemberJoinCacheUtil.getUnionIdKey(unionId);
        if (redisCacheUtil.exists(unionIdKey)) {
            String tempStr = redisCacheUtil.get(unionIdKey);
            result = JSONArray.parseArray(tempStr, UnionMemberJoin.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMemberJoin> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId);
        result = unionMemberJoinDao.selectList(entityWrapper);
        setCache(result, unionId, UnionMemberJoinCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
    public List<UnionMemberJoin> listValidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMemberJoin> result;
        // (1)cache
        String validUnionIdKey = UnionMemberJoinCacheUtil.getValidUnionIdKey(unionId);
        if (redisCacheUtil.exists(validUnionIdKey)) {
            String tempStr = redisCacheUtil.get(validUnionIdKey);
            result = JSONArray.parseArray(tempStr, UnionMemberJoin.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMemberJoin> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId);
        result = unionMemberJoinDao.selectList(entityWrapper);
        setValidCache(result, unionId, UnionMemberJoinCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
    public List<UnionMemberJoin> listInvalidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMemberJoin> result;
        // (1)cache
        String invalidUnionIdKey = UnionMemberJoinCacheUtil.getInvalidUnionIdKey(unionId);
        if (redisCacheUtil.exists(invalidUnionIdKey)) {
            String tempStr = redisCacheUtil.get(invalidUnionIdKey);
            result = JSONArray.parseArray(tempStr, UnionMemberJoin.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMemberJoin> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("union_id", unionId);
        result = unionMemberJoinDao.selectList(entityWrapper);
        setInvalidCache(result, unionId, UnionMemberJoinCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
    public List<UnionMemberJoin> listByIdList(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMemberJoin> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(idList)) {
            for (Integer id : idList) {
                result.add(getById(id));
            }
        }

        return result;
    }

    @Override
    public Page<UnionMemberJoin> pageSupport(Page page, EntityWrapper<UnionMemberJoin> entityWrapper) throws Exception {
        if (page == null || entityWrapper == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionMemberJoinDao.selectPage(page, entityWrapper);
    }
    //********************************************* Object As a Service - save *****************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(UnionMemberJoin newUnionMemberJoin) throws Exception {
        if (newUnionMemberJoin == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        unionMemberJoinDao.insert(newUnionMemberJoin);
        removeCache(newUnionMemberJoin);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionMemberJoin> newUnionMemberJoinList) throws Exception {
        if (newUnionMemberJoinList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        unionMemberJoinDao.insertBatch(newUnionMemberJoinList);
        removeCache(newUnionMemberJoinList);
    }

    //********************************************* Object As a Service - remove ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionMemberJoin unionMemberJoin = getById(id);
        removeCache(unionMemberJoin);
        // (2)remove in db logically
        UnionMemberJoin removeUnionMemberJoin = new UnionMemberJoin();
        removeUnionMemberJoin.setId(id);
        removeUnionMemberJoin.setDelStatus(CommonConstant.DEL_STATUS_YES);
        unionMemberJoinDao.updateById(removeUnionMemberJoin);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionMemberJoin> unionMemberJoinList = listByIdList(idList);
        removeCache(unionMemberJoinList);
        // (2)remove in db logically
        List<UnionMemberJoin> removeUnionMemberJoinList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMemberJoin removeUnionMemberJoin = new UnionMemberJoin();
            removeUnionMemberJoin.setId(id);
            removeUnionMemberJoin.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionMemberJoinList.add(removeUnionMemberJoin);
        }
        unionMemberJoinDao.updateBatchById(removeUnionMemberJoinList);
    }

    //********************************************* Object As a Service - update ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UnionMemberJoin updateUnionMemberJoin) throws Exception {
        if (updateUnionMemberJoin == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionMemberJoin.getId();
        UnionMemberJoin unionMemberJoin = getById(id);
        removeCache(unionMemberJoin);
        // (2)update db
        unionMemberJoinDao.updateById(updateUnionMemberJoin);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionMemberJoin> updateUnionMemberJoinList) throws Exception {
        if (updateUnionMemberJoinList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = getIdList(updateUnionMemberJoinList);
        List<UnionMemberJoin> unionMemberJoinList = listByIdList(idList);
        removeCache(unionMemberJoinList);
        // (2)update db
        unionMemberJoinDao.updateBatchById(updateUnionMemberJoinList);
    }

    //********************************************* Object As a Service - cache support ********************************

    private void setCache(UnionMemberJoin newUnionMemberJoin, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionMemberJoinCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionMemberJoin);
    }

    private void setCache(List<UnionMemberJoin> newUnionMemberJoinList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionMemberJoinCacheUtil.TYPE_APPLY_MEMBER_ID:
                foreignIdKey = UnionMemberJoinCacheUtil.getApplyMemberIdKey(foreignId);
                break;
            case UnionMemberJoinCacheUtil.TYPE_RECOMMEND_MEMBER_ID:
                foreignIdKey = UnionMemberJoinCacheUtil.getRecommendMemberIdKey(foreignId);
                break;
            case UnionMemberJoinCacheUtil.TYPE_UNION_ID:
                foreignIdKey = UnionMemberJoinCacheUtil.getUnionIdKey(foreignId);
                break;

            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionMemberJoinList);
        }
    }

    private void setValidCache(List<UnionMemberJoin> newUnionMemberJoinList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String validForeignIdKey = null;
        switch (foreignIdType) {
            case UnionMemberJoinCacheUtil.TYPE_APPLY_MEMBER_ID:
                validForeignIdKey = UnionMemberJoinCacheUtil.getValidApplyMemberIdKey(foreignId);
                break;
            case UnionMemberJoinCacheUtil.TYPE_RECOMMEND_MEMBER_ID:
                validForeignIdKey = UnionMemberJoinCacheUtil.getValidRecommendMemberIdKey(foreignId);
                break;
            case UnionMemberJoinCacheUtil.TYPE_UNION_ID:
                validForeignIdKey = UnionMemberJoinCacheUtil.getValidUnionIdKey(foreignId);
                break;

            default:
                break;
        }
        if (validForeignIdKey != null) {
            redisCacheUtil.set(validForeignIdKey, newUnionMemberJoinList);
        }
    }

    private void setInvalidCache(List<UnionMemberJoin> newUnionMemberJoinList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String invalidForeignIdKey = null;
        switch (foreignIdType) {
            case UnionMemberJoinCacheUtil.TYPE_APPLY_MEMBER_ID:
                invalidForeignIdKey = UnionMemberJoinCacheUtil.getInvalidApplyMemberIdKey(foreignId);
                break;
            case UnionMemberJoinCacheUtil.TYPE_RECOMMEND_MEMBER_ID:
                invalidForeignIdKey = UnionMemberJoinCacheUtil.getInvalidRecommendMemberIdKey(foreignId);
                break;
            case UnionMemberJoinCacheUtil.TYPE_UNION_ID:
                invalidForeignIdKey = UnionMemberJoinCacheUtil.getInvalidUnionIdKey(foreignId);
                break;

            default:
                break;
        }
        if (invalidForeignIdKey != null) {
            redisCacheUtil.set(invalidForeignIdKey, newUnionMemberJoinList);
        }
    }

    private void removeCache(UnionMemberJoin unionMemberJoin) {
        if (unionMemberJoin == null) {
            return;
        }
        Integer id = unionMemberJoin.getId();
        String idKey = UnionMemberJoinCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer applyMemberId = unionMemberJoin.getApplyMemberId();
        if (applyMemberId != null) {
            String applyMemberIdKey = UnionMemberJoinCacheUtil.getApplyMemberIdKey(applyMemberId);
            redisCacheUtil.remove(applyMemberIdKey);

            String validApplyMemberIdKey = UnionMemberJoinCacheUtil.getValidApplyMemberIdKey(applyMemberId);
            redisCacheUtil.remove(validApplyMemberIdKey);

            String invalidApplyMemberIdKey = UnionMemberJoinCacheUtil.getInvalidApplyMemberIdKey(applyMemberId);
            redisCacheUtil.remove(invalidApplyMemberIdKey);
        }

        Integer recommendMemberId = unionMemberJoin.getRecommendMemberId();
        if (recommendMemberId != null) {
            String recommendMemberIdKey = UnionMemberJoinCacheUtil.getRecommendMemberIdKey(recommendMemberId);
            redisCacheUtil.remove(recommendMemberIdKey);

            String validRecommendMemberIdKey = UnionMemberJoinCacheUtil.getValidRecommendMemberIdKey(recommendMemberId);
            redisCacheUtil.remove(validRecommendMemberIdKey);

            String invalidRecommendMemberIdKey = UnionMemberJoinCacheUtil.getInvalidRecommendMemberIdKey(recommendMemberId);
            redisCacheUtil.remove(invalidRecommendMemberIdKey);
        }

        Integer unionId = unionMemberJoin.getUnionId();
        if (unionId != null) {
            String unionIdKey = UnionMemberJoinCacheUtil.getUnionIdKey(unionId);
            redisCacheUtil.remove(unionIdKey);

            String validUnionIdKey = UnionMemberJoinCacheUtil.getValidUnionIdKey(unionId);
            redisCacheUtil.remove(validUnionIdKey);

            String invalidUnionIdKey = UnionMemberJoinCacheUtil.getInvalidUnionIdKey(unionId);
            redisCacheUtil.remove(invalidUnionIdKey);
        }

    }

    private void removeCache(List<UnionMemberJoin> unionMemberJoinList) {
        if (ListUtil.isEmpty(unionMemberJoinList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionMemberJoin unionMemberJoin : unionMemberJoinList) {
            idList.add(unionMemberJoin.getId());
        }
        List<String> idKeyList = UnionMemberJoinCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

        List<String> applyMemberIdKeyList = getForeignIdKeyList(unionMemberJoinList, UnionMemberJoinCacheUtil.TYPE_APPLY_MEMBER_ID);
        if (ListUtil.isNotEmpty(applyMemberIdKeyList)) {
            redisCacheUtil.remove(applyMemberIdKeyList);
        }

        List<String> recommendMemberIdKeyList = getForeignIdKeyList(unionMemberJoinList, UnionMemberJoinCacheUtil.TYPE_RECOMMEND_MEMBER_ID);
        if (ListUtil.isNotEmpty(recommendMemberIdKeyList)) {
            redisCacheUtil.remove(recommendMemberIdKeyList);
        }

        List<String> unionIdKeyList = getForeignIdKeyList(unionMemberJoinList, UnionMemberJoinCacheUtil.TYPE_UNION_ID);
        if (ListUtil.isNotEmpty(unionIdKeyList)) {
            redisCacheUtil.remove(unionIdKeyList);
        }

    }

    private List<String> getForeignIdKeyList(List<UnionMemberJoin> unionMemberJoinList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionMemberJoinCacheUtil.TYPE_APPLY_MEMBER_ID:
                for (UnionMemberJoin unionMemberJoin : unionMemberJoinList) {
                    Integer applyMemberId = unionMemberJoin.getApplyMemberId();
                    if (applyMemberId != null) {
                        String applyMemberIdKey = UnionMemberJoinCacheUtil.getApplyMemberIdKey(applyMemberId);
                        result.add(applyMemberIdKey);

                        String validApplyMemberIdKey = UnionMemberJoinCacheUtil.getValidApplyMemberIdKey(applyMemberId);
                        result.add(validApplyMemberIdKey);

                        String invalidApplyMemberIdKey = UnionMemberJoinCacheUtil.getInvalidApplyMemberIdKey(applyMemberId);
                        result.add(invalidApplyMemberIdKey);
                    }
                }
                break;
            case UnionMemberJoinCacheUtil.TYPE_RECOMMEND_MEMBER_ID:
                for (UnionMemberJoin unionMemberJoin : unionMemberJoinList) {
                    Integer recommendMemberId = unionMemberJoin.getRecommendMemberId();
                    if (recommendMemberId != null) {
                        String recommendMemberIdKey = UnionMemberJoinCacheUtil.getRecommendMemberIdKey(recommendMemberId);
                        result.add(recommendMemberIdKey);

                        String validRecommendMemberIdKey = UnionMemberJoinCacheUtil.getValidRecommendMemberIdKey(recommendMemberId);
                        result.add(validRecommendMemberIdKey);

                        String invalidRecommendMemberIdKey = UnionMemberJoinCacheUtil.getInvalidRecommendMemberIdKey(recommendMemberId);
                        result.add(invalidRecommendMemberIdKey);
                    }
                }
                break;
            case UnionMemberJoinCacheUtil.TYPE_UNION_ID:
                for (UnionMemberJoin unionMemberJoin : unionMemberJoinList) {
                    Integer unionId = unionMemberJoin.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = UnionMemberJoinCacheUtil.getUnionIdKey(unionId);
                        result.add(unionIdKey);

                        String validUnionIdKey = UnionMemberJoinCacheUtil.getValidUnionIdKey(unionId);
                        result.add(validUnionIdKey);

                        String invalidUnionIdKey = UnionMemberJoinCacheUtil.getInvalidUnionIdKey(unionId);
                        result.add(invalidUnionIdKey);
                    }
                }
                break;

            default:
                break;
        }
        return result;
    }

    // TODO

    //***************************************** Domain Driven Design - get *********************************************

    @Override
    public UnionMemberJoin getByIdAndUnionId(Integer joinId, Integer unionId) throws Exception {
        if (joinId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        UnionMemberJoin result = getById(joinId);

        return result != null && unionId.equals(result.getUnionId()) ? result : null;
    }

    @Override
    public UnionMemberJoin getByUnionIdAndApplyMemberId(Integer unionId, Integer applyMemberId) throws Exception {
        if (unionId == null || applyMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMemberJoin> result = listByUnionId(unionId);
        result = filterByApplyMemberId(result, applyMemberId);

        return ListUtil.isNotEmpty(result) ? result.get(0) : null;
    }

    //***************************************** Domain Driven Design - list ********************************************

    @Override
    public List<MemberJoinVO> listMemberJoinVOByBusIdAndUnionId(Integer buId, Integer unionId, String optMemberName, String optPhone) throws Exception {
        if (buId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性和member读权限、盟主权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getReadByBusIdAndUnionId(buId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_READ_REJECT);
        }
        if (MemberConstant.IS_UNION_OWNER_YES != member.getIsUnionOwner()) {
            throw new BusinessException(CommonConstant.UNION_NEED_OWNER);
        }
        // （2）	按时间顺序排序
        List<MemberJoinVO> result = new ArrayList<>();
        List<UnionMember> joinMemberList = unionMemberService.listByUnionIdAndStatus(unionId, MemberConstant.STATUS_APPLY_IN);
        if (ListUtil.isNotEmpty(joinMemberList)) {
            for (UnionMember joinMember : joinMemberList) {
                boolean isContinue;
                if (StringUtil.isNotEmpty(optMemberName)) {
                    isContinue = StringUtil.isEmpty(joinMember.getEnterpriseName()) || !joinMember.getEnterpriseName().contains(optMemberName);
                    if (isContinue) {
                        continue;
                    }
                }
                if (StringUtil.isNotEmpty(optPhone)) {
                    isContinue = StringUtil.isEmpty(joinMember.getDirectorPhone()) || !joinMember.getDirectorPhone().contains(optPhone);
                    if (isContinue) {
                        continue;
                    }
                }

                MemberJoinVO vo = new MemberJoinVO();
                vo.setJoinMember(joinMember);

                UnionMemberJoin memberJoin = getByUnionIdAndApplyMemberId(unionId, joinMember.getId());
                vo.setMemberJoin(memberJoin);

                if (MemberConstant.JOIN_TYPE_RECOMMEND == memberJoin.getType() && memberJoin.getRecommendMemberId() != null) {
                    UnionMember recommendMember = unionMemberService.getReadByIdAndUnionId(memberJoin.getRecommendMemberId(), unionId);
                    vo.setRecommendMember(recommendMember);
                }

                result.add(vo);
            }
        }
        Collections.sort(result, new Comparator<MemberJoinVO>() {
            @Override
            public int compare(MemberJoinVO o1, MemberJoinVO o2) {
                return o1.getMemberJoin().getCreateTime().compareTo(o2.getMemberJoin().getCreateTime());
            }
        });

        return result;
    }

    //***************************************** Domain Driven Design - save ********************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveJoinCreateVOByBusIdAndUnionIdAndType(Integer busId, Integer unionId, Integer type, MemberJoinCreateVO vo) throws Exception {
        if (busId == null || unionId == null || type == null || vo == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        // （2）	如果是推荐入盟，则判断member写权限
        UnionMember member = null;
        BusUser busUser = null;
        if (MemberConstant.JOIN_TYPE_RECOMMEND == type) {
            member = unionMemberService.getWriteByBusIdAndUnionId(busId, unionId);
            if (member == null) {
                throw new BusinessException(CommonConstant.UNION_WRITE_REJECT);
            }
            String busUserName = vo.getBusUserName();
            if (StringUtil.isEmpty(busUserName)) {
                throw new BusinessException("被推荐的商家名称(盟员账号)不能为空");
            }
            busUser = busUserService.getBusUserByName(busUserName);
            if (busUser == null) {
                throw new BusinessException("找不到被推荐的商家信息(盟员账号)");
            }
        }
        // （3）判断是否重复加入
        if (MemberConstant.JOIN_TYPE_RECOMMEND == type) {
            if (unionMemberService.existReadByBusIdAndUnionId(busUser.getId(), unionId)) {
                throw new BusinessException("被推荐的商家已入盟");
            }
            if (unionMemberService.existByBusIdAndUnionId(busUser.getId(), unionId)) {
                throw new BusinessException("已被推荐入盟");
            }
        } else {
            if (unionMemberService.existReadByBusIdAndUnionId(busId, unionId)) {
                throw new BusinessException("已经是该联盟的盟员");
            }
            if (unionMemberService.existByBusIdAndUnionId(busId, unionId)) {
                throw new BusinessException("已申请加入联盟");
            }
        }
        // （3）	判断union剩余可加盟数
        Integer unionSurplus = unionMainService.countSurplusByUnionId(unionId);
        if (unionSurplus <= 0) {
            throw new BusinessException("联盟已达成员总数上限，无法加入");
        }
        // （4）	判断商家已加盟数
        Integer busMemberCount = unionMemberService.countReadByBusId(busId);
        if (busMemberCount >= ConfigConstant.MAX_UNION_APPLY) {
            throw new BusinessException("商家加盟数已达上限，无法加入");
        }
        // （5）	校验表单
        List<String> itemKeyList = unionMainDictService.listItemKeyByUnionId(unionId);
        Date currentDate = DateUtil.getCurrentDate();
        UnionMember saveMember = new UnionMember();
        saveMember.setDelStatus(CommonConstant.COMMON_NO);
        saveMember.setCreateTime(currentDate);
        saveMember.setUnionId(unionId);
        saveMember.setIsUnionOwner(MemberConstant.IS_UNION_OWNER_NO);
        // （5-1）企业名称
        String enterpriseName = vo.getEnterpriseName();
        if (StringUtil.isEmpty(enterpriseName)) {
            throw new BusinessException("企业名称不能为空");
        }
        if (StringUtil.getStringLength(enterpriseName) > 10) {
            throw new BusinessException("企业名称字数不能超过10");
        }
        saveMember.setEnterpriseName(enterpriseName);
        // （5-2）负责人名称
        String directorName = vo.getDirectorName();
        if (itemKeyList.contains(UnionConstant.ITEM_KEY_DIRECTOR_NAME) && StringUtil.isEmpty(directorName)) {
            throw new BusinessException("负责人名称不能为空");
        }
        if (StringUtil.isNotEmpty(directorName) && StringUtil.getStringLength(directorName) > 10) {
            throw new BusinessException("负责人名称字数不能超过10");
        }
        saveMember.setDirectorName(directorName);
        // （5-3）负责人电话
        String directorPhone = vo.getDirectorPhone();
        if (itemKeyList.contains(UnionConstant.ITEM_KEY_DIRECTOR_PHONE) && StringUtil.isEmpty(directorPhone)) {
            throw new BusinessException("负责人联系电话不能为空");
        }
        if (StringUtil.isNotEmpty(directorPhone) && !StringUtil.isPhone(directorPhone)) {
            throw new BusinessException("负责人联系电话无效");
        }
        saveMember.setDirectorPhone(directorPhone);
        // （5-4）负责人名称
        String directorEmail = vo.getDirectorEmail();
        if (itemKeyList.contains(UnionConstant.ITEM_KEY_DIRECTOR_EMAIL) && StringUtil.isEmpty(directorEmail)) {
            throw new BusinessException("负责人邮箱不能为空");
        }
        if (StringUtil.isNotEmpty(directorEmail) && !StringUtil.isEmail(directorEmail)) {
            throw new BusinessException("负责人邮箱无效");
        }
        saveMember.setDirectorEmail(directorEmail);
        // （5-5）理由
        UnionMemberJoin saveJoin = new UnionMemberJoin();
        saveJoin.setDelStatus(CommonConstant.COMMON_NO);
        saveJoin.setCreateTime(currentDate);
        saveJoin.setUnionId(unionId);
        saveJoin.setType(type);
        String reason = vo.getReason();
        if (itemKeyList.contains(UnionConstant.ITEM_KEY_REASON) && StringUtil.isEmpty(reason)) {
            throw new BusinessException("理由不能为空");
        }
        if (StringUtil.isNotEmpty(reason) && StringUtil.getStringLength(reason) > 20) {
            throw new BusinessException("理由字数不能超过20");
        }
        saveJoin.setReason(reason);
        // （6）	如果是盟主推荐入盟，则直接入盟成功
        if (MemberConstant.JOIN_TYPE_RECOMMEND == type) {
            saveMember.setBusId(busUser.getId());
            saveJoin.setRecommendMemberId(member.getId());
            if (MemberConstant.IS_UNION_OWNER_YES == member.getIsUnionOwner()) {
                saveMember.setStatus(MemberConstant.STATUS_IN);
            } else {
                saveMember.setStatus(MemberConstant.STATUS_APPLY_IN);
            }
        } else {
            saveMember.setBusId(busId);
            saveMember.setStatus(MemberConstant.STATUS_APPLY_IN);
        }

        // （7）事务操作
        unionMemberService.save(saveMember);

        saveJoin.setApplyMemberId(saveMember.getId());
        save(saveJoin);
    }

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatusByBusIdAndIdAndUnionId(Integer busId, Integer joinId, Integer unionId, Integer isPass) throws Exception {
        if (busId == null || joinId == null || unionId == null || isPass == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性和member写权限、盟主权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getWriteByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_WRITE_REJECT);
        }
        if (MemberConstant.IS_UNION_OWNER_YES != member.getIsUnionOwner()) {
            throw new BusinessException(CommonConstant.UNION_NEED_OWNER);
        }
        // （2）	判断joinId有效性
        UnionMemberJoin join = getByIdAndUnionId(joinId, unionId);
        if (join == null) {
            throw new BusinessException("联盟入盟申请不存在");
        }
        // （3）	判断joinMember状态
        //   （3-1）如果是申请状态，则进入下一步；
        //   （3-2）如果是其他状态，则直接返回成功；
        UnionMember joinMember = unionMemberService.getByIdAndUnionIdAndStatus(join.getApplyMemberId(), unionId, MemberConstant.STATUS_APPLY_IN);
        if (joinMember == null) {
            return;
        }
        // （4）	判断union剩余可加盟数
        Integer unionSurplus = unionMainService.countSurplusByUnionId(unionId);
        if (unionSurplus <= 0) {
            throw new BusinessException("联盟已达成员总数上限，无法通过");
        }
        // （5）判断商家已加盟数
        Integer busMemberCount = unionMemberService.countReadByBusId(joinMember.getBusId());
        if (busMemberCount >= ConfigConstant.MAX_UNION_APPLY) {
            throw new BusinessException("申请者加盟数已达上限，无法通过");
        }

        // （6）事务操作
        UnionMember updateMember = new UnionMember();
        updateMember.setId(joinMember.getId());
        if (CommonConstant.COMMON_YES == isPass) {
            updateMember.setStatus(MemberConstant.STATUS_IN);
        } else {
            updateMember.setDelStatus(CommonConstant.COMMON_YES);
        }
        unionMemberService.update(updateMember);

        removeById(joinId);
    }

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

    //***************************************** Domain Driven Design - filter ******************************************

    @Override
    public List<UnionMemberJoin> filterByApplyMemberId(List<UnionMemberJoin> joinList, Integer applyMemberId) throws Exception {
        if (joinList == null || applyMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMemberJoin> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(joinList)) {
            for (UnionMemberJoin join : joinList) {
                if (applyMemberId.equals(join.getApplyMemberId())) {
                    result.add(join);
                }
            }
        }

        return result;
    }


}