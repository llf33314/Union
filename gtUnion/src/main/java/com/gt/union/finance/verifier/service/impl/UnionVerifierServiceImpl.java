package com.gt.union.finance.verifier.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.bean.session.TCommonStaff;
import com.gt.union.api.client.shop.ShopService;
import com.gt.union.api.client.sms.SmsService;
import com.gt.union.api.client.staff.ITCommonStaffService;
import com.gt.union.api.client.user.IBusUserService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.SmsCodeConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.DateUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.finance.verifier.dao.IUnionVerifierDao;
import com.gt.union.finance.verifier.entity.UnionVerifier;
import com.gt.union.finance.verifier.service.IUnionVerifierService;
import com.gt.union.finance.verifier.util.UnionVerifierCacheUtil;
import com.gt.util.entity.result.shop.WsWxShopInfoExtend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 平台管理者 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 14:54:27
 */
@Service
public class UnionVerifierServiceImpl implements IUnionVerifierService {
    @Autowired
    private IUnionVerifierDao unionVerifierDao;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IBusUserService busUserService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private ITCommonStaffService itCommonStaffService;

    //********************************************* Base On Business - get *********************************************

    //********************************************* Base On Business - list ********************************************


    //********************************************* Base On Business - save ********************************************

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    //********************************************* Base On Business - other *******************************************

    //********************************************* Base On Business - filter ******************************************

    @Override
    public List<UnionVerifier> filterByDelStatus(List<UnionVerifier> unionVerifierList, Integer delStatus) throws Exception {
        if (delStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionVerifier> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionVerifierList)) {
            for (UnionVerifier unionVerifier : unionVerifierList) {
                if (delStatus.equals(unionVerifier.getDelStatus())) {
                    result.add(unionVerifier);
                }
            }
        }

        return result;
    }

    //********************************************* Object As a Service - get ******************************************

    @Override
    public UnionVerifier getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionVerifier result;
        // (1)cache
        String idKey = UnionVerifierCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionVerifier.class);
            return result;
        }
        // (2)db
        result = unionVerifierDao.selectById(id);
        setCache(result, id);
        return result;
    }

    @Override
    public UnionVerifier getValidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionVerifier result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_NO == result.getDelStatus() ? result : null;
    }

    @Override
    public UnionVerifier getInvalidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionVerifier result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_YES == result.getDelStatus() ? result : null;
    }

    //********************************************* Object As a Service - list *****************************************

    @Override
    public List<Integer> getIdList(List<UnionVerifier> unionVerifierList) throws Exception {
        if (unionVerifierList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<Integer> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionVerifierList)) {
            for (UnionVerifier unionVerifier : unionVerifierList) {
                result.add(unionVerifier.getId());
            }
        }

        return result;
    }

    @Override
    public List<UnionVerifier> listByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionVerifier> result;
        // (1)cache
        String busIdKey = UnionVerifierCacheUtil.getBusIdKey(busId);
        if (redisCacheUtil.exists(busIdKey)) {
            String tempStr = redisCacheUtil.get(busIdKey);
            result = JSONArray.parseArray(tempStr, UnionVerifier.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionVerifier> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("bus_id", busId);
        result = unionVerifierDao.selectList(entityWrapper);
        setCache(result, busId, UnionVerifierCacheUtil.TYPE_BUS_ID);
        return result;
    }

    @Override
    public List<UnionVerifier> listValidByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionVerifier> result;
        // (1)cache
        String validBusIdKey = UnionVerifierCacheUtil.getValidBusIdKey(busId);
        if (redisCacheUtil.exists(validBusIdKey)) {
            String tempStr = redisCacheUtil.get(validBusIdKey);
            result = JSONArray.parseArray(tempStr, UnionVerifier.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionVerifier> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId);
        result = unionVerifierDao.selectList(entityWrapper);
        setValidCache(result, busId, UnionVerifierCacheUtil.TYPE_BUS_ID);
        return result;
    }

    @Override
    public List<UnionVerifier> listInvalidByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionVerifier> result;
        // (1)cache
        String invalidBusIdKey = UnionVerifierCacheUtil.getInvalidBusIdKey(busId);
        if (redisCacheUtil.exists(invalidBusIdKey)) {
            String tempStr = redisCacheUtil.get(invalidBusIdKey);
            result = JSONArray.parseArray(tempStr, UnionVerifier.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionVerifier> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("bus_id", busId);
        result = unionVerifierDao.selectList(entityWrapper);
        setInvalidCache(result, busId, UnionVerifierCacheUtil.TYPE_BUS_ID);
        return result;
    }

    @Override
    public List<UnionVerifier> listByIdList(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionVerifier> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(idList)) {
            for (Integer id : idList) {
                result.add(getById(id));
            }
        }

        return result;
    }

    @Override
    public Page<UnionVerifier> pageSupport(Page page, EntityWrapper<UnionVerifier> entityWrapper) throws Exception {
        if (page == null || entityWrapper == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionVerifierDao.selectPage(page, entityWrapper);
    }
    //********************************************* Object As a Service - save *****************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(UnionVerifier newUnionVerifier) throws Exception {
        if (newUnionVerifier == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        unionVerifierDao.insert(newUnionVerifier);
        removeCache(newUnionVerifier);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionVerifier> newUnionVerifierList) throws Exception {
        if (newUnionVerifierList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        unionVerifierDao.insertBatch(newUnionVerifierList);
        removeCache(newUnionVerifierList);
    }

    //********************************************* Object As a Service - remove ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionVerifier unionVerifier = getById(id);
        removeCache(unionVerifier);
        // (2)remove in db logically
        UnionVerifier removeUnionVerifier = new UnionVerifier();
        removeUnionVerifier.setId(id);
        removeUnionVerifier.setDelStatus(CommonConstant.DEL_STATUS_YES);
        unionVerifierDao.updateById(removeUnionVerifier);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionVerifier> unionVerifierList = listByIdList(idList);
        removeCache(unionVerifierList);
        // (2)remove in db logically
        List<UnionVerifier> removeUnionVerifierList = new ArrayList<>();
        for (Integer id : idList) {
            UnionVerifier removeUnionVerifier = new UnionVerifier();
            removeUnionVerifier.setId(id);
            removeUnionVerifier.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionVerifierList.add(removeUnionVerifier);
        }
        unionVerifierDao.updateBatchById(removeUnionVerifierList);
    }

    //********************************************* Object As a Service - update ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UnionVerifier updateUnionVerifier) throws Exception {
        if (updateUnionVerifier == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionVerifier.getId();
        UnionVerifier unionVerifier = getById(id);
        removeCache(unionVerifier);
        // (2)update db
        unionVerifierDao.updateById(updateUnionVerifier);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionVerifier> updateUnionVerifierList) throws Exception {
        if (updateUnionVerifierList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = getIdList(updateUnionVerifierList);
        List<UnionVerifier> unionVerifierList = listByIdList(idList);
        removeCache(unionVerifierList);
        // (2)update db
        unionVerifierDao.updateBatchById(updateUnionVerifierList);
    }

    //********************************************* Object As a Service - cache support ********************************

    private void setCache(UnionVerifier newUnionVerifier, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionVerifierCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionVerifier);
    }

    private void setCache(List<UnionVerifier> newUnionVerifierList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionVerifierCacheUtil.TYPE_BUS_ID:
                foreignIdKey = UnionVerifierCacheUtil.getBusIdKey(foreignId);
                break;

            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionVerifierList);
        }
    }

    private void setValidCache(List<UnionVerifier> newUnionVerifierList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String validForeignIdKey = null;
        switch (foreignIdType) {
            case UnionVerifierCacheUtil.TYPE_BUS_ID:
                validForeignIdKey = UnionVerifierCacheUtil.getValidBusIdKey(foreignId);
                break;

            default:
                break;
        }
        if (validForeignIdKey != null) {
            redisCacheUtil.set(validForeignIdKey, newUnionVerifierList);
        }
    }

    private void setInvalidCache(List<UnionVerifier> newUnionVerifierList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String invalidForeignIdKey = null;
        switch (foreignIdType) {
            case UnionVerifierCacheUtil.TYPE_BUS_ID:
                invalidForeignIdKey = UnionVerifierCacheUtil.getInvalidBusIdKey(foreignId);
                break;

            default:
                break;
        }
        if (invalidForeignIdKey != null) {
            redisCacheUtil.set(invalidForeignIdKey, newUnionVerifierList);
        }
    }

    private void removeCache(UnionVerifier unionVerifier) {
        if (unionVerifier == null) {
            return;
        }
        Integer id = unionVerifier.getId();
        String idKey = UnionVerifierCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer busId = unionVerifier.getBusId();
        if (busId != null) {
            String busIdKey = UnionVerifierCacheUtil.getBusIdKey(busId);
            redisCacheUtil.remove(busIdKey);

            String validBusIdKey = UnionVerifierCacheUtil.getValidBusIdKey(busId);
            redisCacheUtil.remove(validBusIdKey);

            String invalidBusIdKey = UnionVerifierCacheUtil.getInvalidBusIdKey(busId);
            redisCacheUtil.remove(invalidBusIdKey);
        }

    }

    private void removeCache(List<UnionVerifier> unionVerifierList) {
        if (ListUtil.isEmpty(unionVerifierList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionVerifier unionVerifier : unionVerifierList) {
            idList.add(unionVerifier.getId());
        }
        List<String> idKeyList = UnionVerifierCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

        List<String> busIdKeyList = getForeignIdKeyList(unionVerifierList, UnionVerifierCacheUtil.TYPE_BUS_ID);
        if (ListUtil.isNotEmpty(busIdKeyList)) {
            redisCacheUtil.remove(busIdKeyList);
        }

    }

    private List<String> getForeignIdKeyList(List<UnionVerifier> unionVerifierList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionVerifierCacheUtil.TYPE_BUS_ID:
                for (UnionVerifier unionVerifier : unionVerifierList) {
                    Integer busId = unionVerifier.getBusId();
                    if (busId != null) {
                        String busIdKey = UnionVerifierCacheUtil.getBusIdKey(busId);
                        result.add(busIdKey);

                        String validBusIdKey = UnionVerifierCacheUtil.getValidBusIdKey(busId);
                        result.add(validBusIdKey);

                        String invalidBusIdKey = UnionVerifierCacheUtil.getInvalidBusIdKey(busId);
                        result.add(invalidBusIdKey);
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
    public UnionVerifier getByBusIdAndId(Integer busId, Integer verifierId) throws Exception {
        if (busId == null || verifierId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        UnionVerifier result = getById(verifierId);

        return result != null && busId.equals(result.getBusId()) ? result : null;
    }

    //***************************************** Domain Driven Design - list ********************************************

    @Override
    public List<UnionVerifier> list() throws Exception {
        EntityWrapper<UnionVerifier> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.COMMON_NO);

        return unionVerifierDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionVerifier> listFinanceByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        BusUser busUser = busUserService.getBusUserById(busId);
        if (busUser == null) {
            throw new BusinessException("找不到商家信息");
        }

        List<UnionVerifier> result = new ArrayList<>();
        UnionVerifier adminVerifier = new UnionVerifier();
        adminVerifier.setEmployeeName("管理员");
        adminVerifier.setPhone(busUser.getPhone());
        result.add(adminVerifier);

        result.addAll(listByBusId(busId));

        return result;
    }

    //***************************************** Domain Driven Design - save ********************************************

    @Override
    public void saveByBusId(Integer busId, String code, UnionVerifier verifier) throws Exception {
        if (busId == null || code == null || verifier == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        // （1）	手机号不能重复
        String phone = verifier.getPhone();
        if (StringUtil.isEmpty(phone)) {
            throw new BusinessException("手机号不能为空");
        }
        if (!StringUtil.isPhone(phone)) {
            throw new BusinessException("手机号格式有误");
        }
        if (!smsService.checkPhoneCode(SmsCodeConstant.UNION_VERIFIER_TYPE, code, phone)) {
            throw new BusinessException("验证码错误");
        }
        if (existByBusIdAndPhone(busId, phone)) {
            throw new BusinessException("已存在手机号为" + phone + "的平台管理人员信息");
        }
        UnionVerifier saveVerifier = new UnionVerifier();
        saveVerifier.setCreateTime(DateUtil.getCurrentDate());
        saveVerifier.setDelStatus(CommonConstant.DEL_STATUS_NO);
        saveVerifier.setBusId(busId);
        saveVerifier.setPhone(phone);
        // （2）门店信息
        Integer shopId = verifier.getShopId();
        if (shopId == null) {
            throw new BusinessException("门店信息不能为空");
        }
        WsWxShopInfoExtend shop = shopService.getById(shopId);
        if (shop == null) {
            throw new BusinessException("找不到门店信息");
        }
        saveVerifier.setShopId(shopId);
        saveVerifier.setShopName(shop.getBusinessName());
        // （3）员工信息
        Integer employeeId = verifier.getEmployeeId();
        if (employeeId == null) {
            throw new BusinessException("员工信息不能为空");
        }
        TCommonStaff employee = itCommonStaffService.getTCommonStaffById(employeeId);
        if (employee == null) {
            throw new BusinessException("找不到员工信息");
        }
        saveVerifier.setEmployeeId(employeeId);
        saveVerifier.setEmployeeName(employee.getName());

        save(saveVerifier);
    }

    //***************************************** Domain Driven Design - remove ******************************************

    @Override
    public void removeByBusIdAndId(Integer busId, Integer verifierId) throws Exception {
        if (busId == null || verifierId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        UnionVerifier removeVerifier = getByBusIdAndId(busId, verifierId);
        if (removeVerifier == null) {
            throw new BusinessException("找不到平添管理人员信息");
        }

        removeById(verifierId);
    }

    //***************************************** Domain Driven Design - update ******************************************

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

    @Override
    public boolean existByBusIdAndPhone(Integer busId, String phone) throws Exception {
        if (busId == null || phone == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionVerifier> verifierList = listByBusId(busId);
        if (ListUtil.isNotEmpty(verifierList)) {
            for (UnionVerifier verifier : verifierList) {
                if (phone.equals(verifier.getPhone())) {
                    return true;
                }
            }
        }

        return false;
    }

    //***************************************** Domain Driven Design - filter ******************************************

    @Override
    public List<UnionVerifier> filterByPhone(List<UnionVerifier> verifierList, String phone) throws Exception {
        if (verifierList == null || phone == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionVerifier> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(verifierList)) {
            for (UnionVerifier verifier : verifierList) {
                if (phone.equals(verifier.getPhone())) {
                    result.add(verifier);
                }
            }
        }

        return result;
    }

}