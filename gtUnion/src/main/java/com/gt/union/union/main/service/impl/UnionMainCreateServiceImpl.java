package com.gt.union.union.main.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.union.api.client.dict.IDictService;
import com.gt.union.api.client.user.IBusUserService;
import com.gt.union.api.client.user.bean.UserUnionAuthority;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.DateUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.union.main.constant.UnionConstant;
import com.gt.union.union.main.dao.IUnionMainCreateDao;
import com.gt.union.union.main.entity.*;
import com.gt.union.union.main.service.*;
import com.gt.union.union.main.util.UnionMainCreateCacheUtil;
import com.gt.union.union.main.vo.UnionCreateVO;
import com.gt.union.union.main.vo.UnionPermitCheckVO;
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
 * 联盟创建 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 15:26:25
 */
@Service
public class UnionMainCreateServiceImpl implements IUnionMainCreateService {
    @Autowired
    private IUnionMainCreateDao unionMainCreateDao;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionMainPermitService unionMainPermitService;

    @Autowired
    private IBusUserService busUserService;

    @Autowired
    private IUnionMainPackageService unionMainPackageService;

    @Autowired
    private IUnionMainDictService unionMainDictService;

    @Autowired
    private IDictService dictService;

    //********************************************* Base On Business - get *********************************************

    //********************************************* Base On Business - list ********************************************


    //********************************************* Base On Business - save ********************************************

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    //********************************************* Base On Business - other *******************************************

    //********************************************* Base On Business - filter ******************************************

    @Override
    public List<UnionMainCreate> filterByDelStatus(List<UnionMainCreate> unionMainCreateList, Integer delStatus) throws Exception {
        if (delStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMainCreate> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionMainCreateList)) {
            for (UnionMainCreate unionMainCreate : unionMainCreateList) {
                if (delStatus.equals(unionMainCreate.getDelStatus())) {
                    result.add(unionMainCreate);
                }
            }
        }

        return result;
    }

    //********************************************* Object As a Service - get ******************************************

    @Override
    public UnionMainCreate getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMainCreate result;
        // (1)cache
        String idKey = UnionMainCreateCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionMainCreate.class);
            return result;
        }
        // (2)db
        result = unionMainCreateDao.selectById(id);
        setCache(result, id);
        return result;
    }

    @Override
    public UnionMainCreate getValidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMainCreate result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_NO == result.getDelStatus() ? result : null;
    }

    @Override
    public UnionMainCreate getInvalidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMainCreate result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_YES == result.getDelStatus() ? result : null;
    }

    //********************************************* Object As a Service - list *****************************************

    @Override
    public List<Integer> getIdList(List<UnionMainCreate> unionMainCreateList) throws Exception {
        if (unionMainCreateList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<Integer> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionMainCreateList)) {
            for (UnionMainCreate unionMainCreate : unionMainCreateList) {
                result.add(unionMainCreate.getId());
            }
        }

        return result;
    }

    @Override
    public List<UnionMainCreate> listByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainCreate> result;
        // (1)cache
        String busIdKey = UnionMainCreateCacheUtil.getBusIdKey(busId);
        if (redisCacheUtil.exists(busIdKey)) {
            String tempStr = redisCacheUtil.get(busIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainCreate.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainCreate> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("bus_id", busId);
        result = unionMainCreateDao.selectList(entityWrapper);
        setCache(result, busId, UnionMainCreateCacheUtil.TYPE_BUS_ID);
        return result;
    }

    @Override
    public List<UnionMainCreate> listValidByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainCreate> result;
        // (1)cache
        String validBusIdKey = UnionMainCreateCacheUtil.getValidBusIdKey(busId);
        if (redisCacheUtil.exists(validBusIdKey)) {
            String tempStr = redisCacheUtil.get(validBusIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainCreate.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainCreate> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId);
        result = unionMainCreateDao.selectList(entityWrapper);
        setValidCache(result, busId, UnionMainCreateCacheUtil.TYPE_BUS_ID);
        return result;
    }

    @Override
    public List<UnionMainCreate> listInvalidByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainCreate> result;
        // (1)cache
        String invalidBusIdKey = UnionMainCreateCacheUtil.getInvalidBusIdKey(busId);
        if (redisCacheUtil.exists(invalidBusIdKey)) {
            String tempStr = redisCacheUtil.get(invalidBusIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainCreate.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainCreate> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("bus_id", busId);
        result = unionMainCreateDao.selectList(entityWrapper);
        setInvalidCache(result, busId, UnionMainCreateCacheUtil.TYPE_BUS_ID);
        return result;
    }

    @Override
    public List<UnionMainCreate> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainCreate> result;
        // (1)cache
        String unionIdKey = UnionMainCreateCacheUtil.getUnionIdKey(unionId);
        if (redisCacheUtil.exists(unionIdKey)) {
            String tempStr = redisCacheUtil.get(unionIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainCreate.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainCreate> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId);
        result = unionMainCreateDao.selectList(entityWrapper);
        setCache(result, unionId, UnionMainCreateCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
    public List<UnionMainCreate> listValidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainCreate> result;
        // (1)cache
        String validUnionIdKey = UnionMainCreateCacheUtil.getValidUnionIdKey(unionId);
        if (redisCacheUtil.exists(validUnionIdKey)) {
            String tempStr = redisCacheUtil.get(validUnionIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainCreate.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainCreate> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId);
        result = unionMainCreateDao.selectList(entityWrapper);
        setValidCache(result, unionId, UnionMainCreateCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
    public List<UnionMainCreate> listInvalidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainCreate> result;
        // (1)cache
        String invalidUnionIdKey = UnionMainCreateCacheUtil.getInvalidUnionIdKey(unionId);
        if (redisCacheUtil.exists(invalidUnionIdKey)) {
            String tempStr = redisCacheUtil.get(invalidUnionIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainCreate.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainCreate> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("union_id", unionId);
        result = unionMainCreateDao.selectList(entityWrapper);
        setInvalidCache(result, unionId, UnionMainCreateCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
    public List<UnionMainCreate> listByPermitId(Integer permitId) throws Exception {
        if (permitId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainCreate> result;
        // (1)cache
        String permitIdKey = UnionMainCreateCacheUtil.getPermitIdKey(permitId);
        if (redisCacheUtil.exists(permitIdKey)) {
            String tempStr = redisCacheUtil.get(permitIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainCreate.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainCreate> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("permit_id", permitId);
        result = unionMainCreateDao.selectList(entityWrapper);
        setCache(result, permitId, UnionMainCreateCacheUtil.TYPE_PERMIT_ID);
        return result;
    }

    @Override
    public List<UnionMainCreate> listValidByPermitId(Integer permitId) throws Exception {
        if (permitId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainCreate> result;
        // (1)cache
        String validPermitIdKey = UnionMainCreateCacheUtil.getValidPermitIdKey(permitId);
        if (redisCacheUtil.exists(validPermitIdKey)) {
            String tempStr = redisCacheUtil.get(validPermitIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainCreate.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainCreate> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("permit_id", permitId);
        result = unionMainCreateDao.selectList(entityWrapper);
        setValidCache(result, permitId, UnionMainCreateCacheUtil.TYPE_PERMIT_ID);
        return result;
    }

    @Override
    public List<UnionMainCreate> listInvalidByPermitId(Integer permitId) throws Exception {
        if (permitId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainCreate> result;
        // (1)cache
        String invalidPermitIdKey = UnionMainCreateCacheUtil.getInvalidPermitIdKey(permitId);
        if (redisCacheUtil.exists(invalidPermitIdKey)) {
            String tempStr = redisCacheUtil.get(invalidPermitIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainCreate.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainCreate> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("permit_id", permitId);
        result = unionMainCreateDao.selectList(entityWrapper);
        setInvalidCache(result, permitId, UnionMainCreateCacheUtil.TYPE_PERMIT_ID);
        return result;
    }

    @Override
    public List<UnionMainCreate> listByIdList(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMainCreate> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(idList)) {
            for (Integer id : idList) {
                result.add(getById(id));
            }
        }

        return result;
    }

    @Override
    public Page<UnionMainCreate> pageSupport(Page page, EntityWrapper<UnionMainCreate> entityWrapper) throws Exception {
        if (page == null || entityWrapper == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionMainCreateDao.selectPage(page, entityWrapper);
    }
    //********************************************* Object As a Service - save *****************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(UnionMainCreate newUnionMainCreate) throws Exception {
        if (newUnionMainCreate == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        unionMainCreateDao.insert(newUnionMainCreate);
        removeCache(newUnionMainCreate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionMainCreate> newUnionMainCreateList) throws Exception {
        if (newUnionMainCreateList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        unionMainCreateDao.insertBatch(newUnionMainCreateList);
        removeCache(newUnionMainCreateList);
    }

    //********************************************* Object As a Service - remove ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionMainCreate unionMainCreate = getById(id);
        removeCache(unionMainCreate);
        // (2)remove in db logically
        UnionMainCreate removeUnionMainCreate = new UnionMainCreate();
        removeUnionMainCreate.setId(id);
        removeUnionMainCreate.setDelStatus(CommonConstant.DEL_STATUS_YES);
        unionMainCreateDao.updateById(removeUnionMainCreate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionMainCreate> unionMainCreateList = listByIdList(idList);
        removeCache(unionMainCreateList);
        // (2)remove in db logically
        List<UnionMainCreate> removeUnionMainCreateList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMainCreate removeUnionMainCreate = new UnionMainCreate();
            removeUnionMainCreate.setId(id);
            removeUnionMainCreate.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionMainCreateList.add(removeUnionMainCreate);
        }
        unionMainCreateDao.updateBatchById(removeUnionMainCreateList);
    }

    //********************************************* Object As a Service - update ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UnionMainCreate updateUnionMainCreate) throws Exception {
        if (updateUnionMainCreate == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionMainCreate.getId();
        UnionMainCreate unionMainCreate = getById(id);
        removeCache(unionMainCreate);
        // (2)update db
        unionMainCreateDao.updateById(updateUnionMainCreate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionMainCreate> updateUnionMainCreateList) throws Exception {
        if (updateUnionMainCreateList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = getIdList(updateUnionMainCreateList);
        List<UnionMainCreate> unionMainCreateList = listByIdList(idList);
        removeCache(unionMainCreateList);
        // (2)update db
        unionMainCreateDao.updateBatchById(updateUnionMainCreateList);
    }

    //********************************************* Object As a Service - cache support ********************************

    private void setCache(UnionMainCreate newUnionMainCreate, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionMainCreateCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionMainCreate);
    }

    private void setCache(List<UnionMainCreate> newUnionMainCreateList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionMainCreateCacheUtil.TYPE_BUS_ID:
                foreignIdKey = UnionMainCreateCacheUtil.getBusIdKey(foreignId);
                break;
            case UnionMainCreateCacheUtil.TYPE_UNION_ID:
                foreignIdKey = UnionMainCreateCacheUtil.getUnionIdKey(foreignId);
                break;
            case UnionMainCreateCacheUtil.TYPE_PERMIT_ID:
                foreignIdKey = UnionMainCreateCacheUtil.getPermitIdKey(foreignId);
                break;

            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionMainCreateList);
        }
    }

    private void setValidCache(List<UnionMainCreate> newUnionMainCreateList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String validForeignIdKey = null;
        switch (foreignIdType) {
            case UnionMainCreateCacheUtil.TYPE_BUS_ID:
                validForeignIdKey = UnionMainCreateCacheUtil.getValidBusIdKey(foreignId);
                break;
            case UnionMainCreateCacheUtil.TYPE_UNION_ID:
                validForeignIdKey = UnionMainCreateCacheUtil.getValidUnionIdKey(foreignId);
                break;
            case UnionMainCreateCacheUtil.TYPE_PERMIT_ID:
                validForeignIdKey = UnionMainCreateCacheUtil.getValidPermitIdKey(foreignId);
                break;

            default:
                break;
        }
        if (validForeignIdKey != null) {
            redisCacheUtil.set(validForeignIdKey, newUnionMainCreateList);
        }
    }

    private void setInvalidCache(List<UnionMainCreate> newUnionMainCreateList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String invalidForeignIdKey = null;
        switch (foreignIdType) {
            case UnionMainCreateCacheUtil.TYPE_BUS_ID:
                invalidForeignIdKey = UnionMainCreateCacheUtil.getInvalidBusIdKey(foreignId);
                break;
            case UnionMainCreateCacheUtil.TYPE_UNION_ID:
                invalidForeignIdKey = UnionMainCreateCacheUtil.getInvalidUnionIdKey(foreignId);
                break;
            case UnionMainCreateCacheUtil.TYPE_PERMIT_ID:
                invalidForeignIdKey = UnionMainCreateCacheUtil.getInvalidPermitIdKey(foreignId);
                break;

            default:
                break;
        }
        if (invalidForeignIdKey != null) {
            redisCacheUtil.set(invalidForeignIdKey, newUnionMainCreateList);
        }
    }

    private void removeCache(UnionMainCreate unionMainCreate) {
        if (unionMainCreate == null) {
            return;
        }
        Integer id = unionMainCreate.getId();
        String idKey = UnionMainCreateCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer busId = unionMainCreate.getBusId();
        if (busId != null) {
            String busIdKey = UnionMainCreateCacheUtil.getBusIdKey(busId);
            redisCacheUtil.remove(busIdKey);

            String validBusIdKey = UnionMainCreateCacheUtil.getValidBusIdKey(busId);
            redisCacheUtil.remove(validBusIdKey);

            String invalidBusIdKey = UnionMainCreateCacheUtil.getInvalidBusIdKey(busId);
            redisCacheUtil.remove(invalidBusIdKey);
        }

        Integer unionId = unionMainCreate.getUnionId();
        if (unionId != null) {
            String unionIdKey = UnionMainCreateCacheUtil.getUnionIdKey(unionId);
            redisCacheUtil.remove(unionIdKey);

            String validUnionIdKey = UnionMainCreateCacheUtil.getValidUnionIdKey(unionId);
            redisCacheUtil.remove(validUnionIdKey);

            String invalidUnionIdKey = UnionMainCreateCacheUtil.getInvalidUnionIdKey(unionId);
            redisCacheUtil.remove(invalidUnionIdKey);
        }

        Integer permitId = unionMainCreate.getPermitId();
        if (permitId != null) {
            String permitIdKey = UnionMainCreateCacheUtil.getPermitIdKey(permitId);
            redisCacheUtil.remove(permitIdKey);

            String validPermitIdKey = UnionMainCreateCacheUtil.getValidPermitIdKey(permitId);
            redisCacheUtil.remove(validPermitIdKey);

            String invalidPermitIdKey = UnionMainCreateCacheUtil.getInvalidPermitIdKey(permitId);
            redisCacheUtil.remove(invalidPermitIdKey);
        }

    }

    private void removeCache(List<UnionMainCreate> unionMainCreateList) {
        if (ListUtil.isEmpty(unionMainCreateList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionMainCreate unionMainCreate : unionMainCreateList) {
            idList.add(unionMainCreate.getId());
        }
        List<String> idKeyList = UnionMainCreateCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

        List<String> busIdKeyList = getForeignIdKeyList(unionMainCreateList, UnionMainCreateCacheUtil.TYPE_BUS_ID);
        if (ListUtil.isNotEmpty(busIdKeyList)) {
            redisCacheUtil.remove(busIdKeyList);
        }

        List<String> unionIdKeyList = getForeignIdKeyList(unionMainCreateList, UnionMainCreateCacheUtil.TYPE_UNION_ID);
        if (ListUtil.isNotEmpty(unionIdKeyList)) {
            redisCacheUtil.remove(unionIdKeyList);
        }

        List<String> permitIdKeyList = getForeignIdKeyList(unionMainCreateList, UnionMainCreateCacheUtil.TYPE_PERMIT_ID);
        if (ListUtil.isNotEmpty(permitIdKeyList)) {
            redisCacheUtil.remove(permitIdKeyList);
        }

    }

    private List<String> getForeignIdKeyList(List<UnionMainCreate> unionMainCreateList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionMainCreateCacheUtil.TYPE_BUS_ID:
                for (UnionMainCreate unionMainCreate : unionMainCreateList) {
                    Integer busId = unionMainCreate.getBusId();
                    if (busId != null) {
                        String busIdKey = UnionMainCreateCacheUtil.getBusIdKey(busId);
                        result.add(busIdKey);

                        String validBusIdKey = UnionMainCreateCacheUtil.getValidBusIdKey(busId);
                        result.add(validBusIdKey);

                        String invalidBusIdKey = UnionMainCreateCacheUtil.getInvalidBusIdKey(busId);
                        result.add(invalidBusIdKey);
                    }
                }
                break;
            case UnionMainCreateCacheUtil.TYPE_UNION_ID:
                for (UnionMainCreate unionMainCreate : unionMainCreateList) {
                    Integer unionId = unionMainCreate.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = UnionMainCreateCacheUtil.getUnionIdKey(unionId);
                        result.add(unionIdKey);

                        String validUnionIdKey = UnionMainCreateCacheUtil.getValidUnionIdKey(unionId);
                        result.add(validUnionIdKey);

                        String invalidUnionIdKey = UnionMainCreateCacheUtil.getInvalidUnionIdKey(unionId);
                        result.add(invalidUnionIdKey);
                    }
                }
                break;
            case UnionMainCreateCacheUtil.TYPE_PERMIT_ID:
                for (UnionMainCreate unionMainCreate : unionMainCreateList) {
                    Integer permitId = unionMainCreate.getPermitId();
                    if (permitId != null) {
                        String permitIdKey = UnionMainCreateCacheUtil.getPermitIdKey(permitId);
                        result.add(permitIdKey);

                        String validPermitIdKey = UnionMainCreateCacheUtil.getValidPermitIdKey(permitId);
                        result.add(validPermitIdKey);

                        String invalidPermitIdKey = UnionMainCreateCacheUtil.getInvalidPermitIdKey(permitId);
                        result.add(invalidPermitIdKey);
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
    public UnionPermitCheckVO getPermitCheckVOByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        // （1）	判断是否已经是盟主：
        //   （1-1）如果是，则报错；
        //   （1-2）如果不是，则进行下一步；
        UnionMember busOwnerMember = unionMemberService.getOwnerByBusId(busId);
        if (busOwnerMember != null) {
            throw new BusinessException("已经具有盟主身份，最多可成为一个联盟的盟主");
        }

        // （2）	判断是否具有联盟基础服务（调接口）：
        //   （2-1）如果不是，则报错；
        //   （2-2）如果是，则进行下一步；
        UserUnionAuthority authority = busUserService.getUserUnionAuthority(busId);
        if (authority == null || !authority.getAuthority()) {
            throw new BusinessException("不具有联盟基础服务");
        }

        // （3）	判断是否需要付费：
        //   （3-1）如果是，则判断是否已购买联盟许可：
        //     （3-1-1）如果是，则返回许可id；
        //     （3-1-2）如果不是，则提示进入购买页面；
        //   （3-2）如果不是，则判断是否已有免费的联盟许可：
        //     （3-2-1）如果是，则返回许可id；
        //     （3-2-2）如果不是，则新增免费的联盟许可，并返回许可id
        UnionPermitCheckVO result = new UnionPermitCheckVO();
        UnionMainPermit permit = unionMainPermitService.getValidByBusId(busId);
        if (authority.getPay()) {
            if (permit != null) {
                result.setIsPay(CommonConstant.COMMON_NO);
                result.setPermitId(permit.getId());
            } else {
                result.setIsPay(CommonConstant.COMMON_YES);
            }
        } else {
            if (permit != null) {
                result.setIsPay(CommonConstant.COMMON_NO);
                result.setPermitId(permit.getId());
            } else {
                BusUser busUser = busUserService.getBusUserById(busId);
                if (busUser == null) {
                    throw new BusinessException(CommonConstant.UNION_BUS_NOT_FOUND);
                }
                UnionMainPackage unionPackage = unionMainPackageService.getByLevel(busUser.getLevel());
                if (unionPackage == null) {
                    throw new BusinessException("找不到商家等级为" + busUser.getLevel() + "的联盟套餐");
                }
                UnionMainPermit savePermit = new UnionMainPermit();
                savePermit.setDelStatus(CommonConstant.COMMON_NO);
                savePermit.setCreateTime(DateUtil.getCurrentDate());
                savePermit.setBusId(busId);
                savePermit.setValidity(busUser.getEndTime());
                savePermit.setPackageId(unionPackage.getId());
                unionMainPermitService.save(savePermit);

                result.setIsPay(CommonConstant.COMMON_NO);
                result.setPermitId(savePermit.getId());
            }
        }

        return result;
    }

    //***************************************** Domain Driven Design - list ********************************************

    //***************************************** Domain Driven Design - save ********************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveUnionCreateVOByBusId(Integer busId, UnionCreateVO vo) throws Exception {
        if (busId == null || vo == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        // （1）	判断是否已经是盟主：
        //   （1-1）如果是，则报错；
        //   （1-2）如果不是，则进行下一步；
        UnionMember busOwnerMember = unionMemberService.getOwnerByBusId(busId);
        if (busOwnerMember != null) {
            throw new BusinessException("已经具有盟主身份，最多可成为一个联盟的盟主");
        }
        // （2）	判断是否具有联盟基础服务（调接口）：
        //   （2-1）如果不是，则报错；
        //   （2-2）如果是，则进行下一步；
        UserUnionAuthority authority = busUserService.getUserUnionAuthority(busId);
        if (authority == null || !authority.getAuthority()) {
            throw new BusinessException("不具有联盟基础服务");
        }
        // （3）	判断联盟许可有效性
        Integer permitId = vo.getPermitId();
        if (permitId == null) {
            throw new BusinessException("不具有联盟许可");
        }
        UnionMainPermit permit = unionMainPermitService.getValidByBusId(busId);
        if (permit == null || !permitId.equals(permit.getId())) {
            throw new BusinessException("找不到联盟许可信息");
        }
        UnionMainPackage unionPackage = unionMainPackageService.getById(permit.getPackageId());
        if (unionPackage == null) {
            throw new BusinessException("找不到许可对应的套餐信息");
        }
        Date currentDate = DateUtil.getCurrentDate();
        // （4）	校验表单
        UnionMain saveUnion = new UnionMain();
        saveUnion.setDelStatus(CommonConstant.COMMON_NO);
        saveUnion.setCreateTime(currentDate);
        saveUnion.setValidity(permit.getValidity());
        saveUnion.setMemberLimit(unionPackage.getNumber());
        UnionMain voUnion = vo.getUnion();
        if (voUnion == null) {
            throw new BusinessException("请填写联盟设置信息");
        }
        // （4-1）联盟名称
        String unionName = voUnion.getName();
        if (StringUtil.isEmpty(unionName)) {
            throw new BusinessException("联盟名称不能为空");
        }
        if (StringUtil.getStringLength(unionName) > 5) {
            throw new BusinessException("联盟名称字数不能大于5");
        }
        saveUnion.setName(unionName);
        // （4-2）联盟图标
        String unionImg = voUnion.getImg();
        if (StringUtil.isEmpty(unionImg)) {
            throw new BusinessException("联盟图标不能为空");
        }
        saveUnion.setImg(unionImg);
        // （4-3）联盟说明
        String unionIllustration = voUnion.getIllustration();
        if (StringUtil.isEmpty(unionIllustration)) {
            throw new BusinessException("联盟说明不能为空");
        }
        if (StringUtil.getStringLength(unionIllustration) > 20) {
            throw new BusinessException("联盟说明字数不能大于20");
        }
        saveUnion.setIllustration(unionIllustration);
        // （4-4）加盟方式
        Integer unionJoinType = voUnion.getJoinType();
        if (unionJoinType == null) {
            throw new BusinessException("加盟方式不能为空");
        }
        if (UnionConstant.JOIN_TYPE_RECOMMEND != unionJoinType && UnionConstant.JOIN_TYPE_APPLY_RECOMMEND != unionJoinType) {
            throw new BusinessException("加盟方式参数值有误");
        }
        saveUnion.setJoinType(unionJoinType);
        // （4-5）是否开启积分
        Integer unionIsIntegral = voUnion.getIsIntegral();
        if (unionIsIntegral == null) {
            throw new BusinessException("是否开启积分不能为空");
        }
        if (CommonConstant.COMMON_NO != unionIsIntegral && CommonConstant.COMMON_YES != unionIsIntegral) {
            throw new BusinessException("是否开启积分参数值有误");
        }
        saveUnion.setIsIntegral(unionIsIntegral);

        UnionMember saveMember = new UnionMember();
        saveMember.setDelStatus(CommonConstant.COMMON_NO);
        saveMember.setCreateTime(currentDate);
        saveMember.setBusId(busId);
        saveMember.setStatus(MemberConstant.STATUS_IN);
        saveMember.setIsUnionOwner(MemberConstant.IS_UNION_OWNER_YES);
        UnionMember voMember = vo.getMember();
        // （4-6）企业名称
        String memberEnterpriseName = voMember.getEnterpriseName();
        if (StringUtil.isEmpty(memberEnterpriseName)) {
            throw new BusinessException("企业名称不能为空");
        }
        if (StringUtil.getStringLength(memberEnterpriseName) > 10) {
            throw new BusinessException("企业名称字数不能大于10");
        }
        saveMember.setEnterpriseName(memberEnterpriseName);
        // （4-7）企业地址
        String memberEnterpriseAddress = voMember.getEnterpriseAddress();
        if (StringUtil.isEmpty(memberEnterpriseAddress)) {
            throw new BusinessException("企业地址不能为空");
        }
        saveMember.setEnterpriseAddress(memberEnterpriseAddress);
        // （4-8）负责人名称
        String memberDirectorName = voMember.getDirectorName();
        if (StringUtil.isEmpty(memberDirectorName)) {
            throw new BusinessException("负责人名称不能为空");
        }
        if (StringUtil.getStringLength(memberDirectorName) > 10) {
            throw new BusinessException("负责人名称字数不能大于10");
        }
        saveMember.setDirectorName(memberDirectorName);
        // （4-9）负责人联系电话
        String memberDirectorPhone = voMember.getDirectorPhone();
        if (StringUtil.isEmpty(memberDirectorPhone)) {
            throw new BusinessException("负责人联系电话不能为空");
        }
        if (!StringUtil.isPhone(memberDirectorPhone)) {
            throw new BusinessException("负责人联系电话参数值有误");
        }
        saveMember.setDirectorPhone(memberDirectorPhone);
        // （4-10）负责人邮箱
        String memberDirectorEmail = voMember.getDirectorEmail();
        if (StringUtil.isEmpty(memberDirectorEmail)) {
            throw new BusinessException("负责人联系邮箱不能为空");
        }
        if (!StringUtil.isEmail(memberDirectorEmail)) {
            throw new BusinessException("负责人联系邮箱参数值有误");
        }
        saveMember.setDirectorEmail(memberDirectorEmail);
        // （4-11）地址经纬度
        String memberAddressLongitude = voMember.getAddressLongitude();
        String memberAddressLatitude = voMember.getAddressLatitude();
        if (StringUtil.isEmpty(memberAddressLongitude) || StringUtil.isEmpty(memberAddressLatitude)) {
            throw new BusinessException("地址经纬度不能为空");
        }
        saveMember.setAddressLongitude(memberAddressLongitude);
        saveMember.setAddressLatitude(memberAddressLatitude);
        // （4-12）短信通知手机
        String memberNotifyPhone = voMember.getNotifyPhone();
        if (StringUtil.isEmpty(memberNotifyPhone)) {
            throw new BusinessException("短信通知手机不能为空");
        }
        if (!StringUtil.isPhone(memberNotifyPhone)) {
            throw new BusinessException("短信通知手机参数值有误");
        }
        saveMember.setNotifyPhone(memberNotifyPhone);
        // （4-13）入盟收集信息
        List<UnionMainDict> saveDictList = vo.getItemList();
        if (ListUtil.isEmpty(saveDictList)) {
            throw new BusinessException("入盟收集信息不能为空");
        }

        UnionMainCreate saveCreate = new UnionMainCreate();
        saveCreate.setDelStatus(CommonConstant.COMMON_NO);
        saveCreate.setCreateTime(currentDate);
        saveCreate.setBusId(busId);
        saveCreate.setPermitId(permitId);
        // （5）事务操作
        unionMainService.save(saveUnion);

        saveMember.setUnionId(saveUnion.getId());
        unionMemberService.save(saveMember);

        for (UnionMainDict dict : saveDictList) {
            dict.setUnionId(saveUnion.getId());
        }
        unionMainDictService.saveBatch(saveDictList);

        saveCreate.setUnionId(saveUnion.getId());
        save(saveCreate);
    }

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************


}