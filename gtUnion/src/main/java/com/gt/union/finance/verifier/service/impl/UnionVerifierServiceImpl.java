package com.gt.union.finance.verifier.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
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
import com.gt.union.finance.verifier.entity.UnionVerifier;
import com.gt.union.finance.verifier.mapper.UnionVerifierMapper;
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
public class UnionVerifierServiceImpl extends ServiceImpl<UnionVerifierMapper, UnionVerifier> implements IUnionVerifierService {
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

        return selectList(entityWrapper);
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

    //***************************************** Object As a Service - get **********************************************

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
        EntityWrapper<UnionVerifier> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

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
        entityWrapper.eq("bus_id", busId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, busId, UnionVerifierCacheUtil.TYPE_BUS_ID);
        return result;
    }

    //***************************************** Object As a Service - save *********************************************

    @Transactional(rollbackFor = Exception.class)
    public void save(UnionVerifier newUnionVerifier) throws Exception {
        if (newUnionVerifier == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionVerifier);
        removeCache(newUnionVerifier);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionVerifier> newUnionVerifierList) throws Exception {
        if (newUnionVerifierList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionVerifierList);
        removeCache(newUnionVerifierList);
    }

    //***************************************** Object As a Service - remove *******************************************

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
        updateById(removeUnionVerifier);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionVerifier> unionVerifierList = new ArrayList<>();
        for (Integer id : idList) {
            UnionVerifier unionVerifier = getById(id);
            unionVerifierList.add(unionVerifier);
        }
        removeCache(unionVerifierList);
        // (2)remove in db logically
        List<UnionVerifier> removeUnionVerifierList = new ArrayList<>();
        for (Integer id : idList) {
            UnionVerifier removeUnionVerifier = new UnionVerifier();
            removeUnionVerifier.setId(id);
            removeUnionVerifier.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionVerifierList.add(removeUnionVerifier);
        }
        updateBatchById(removeUnionVerifierList);
    }

    //***************************************** Object As a Service - update *******************************************

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
        updateById(updateUnionVerifier);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionVerifier> updateUnionVerifierList) throws Exception {
        if (updateUnionVerifierList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionVerifier updateUnionVerifier : updateUnionVerifierList) {
            idList.add(updateUnionVerifier.getId());
        }
        List<UnionVerifier> unionVerifierList = new ArrayList<>();
        for (Integer id : idList) {
            UnionVerifier unionVerifier = getById(id);
            unionVerifierList.add(unionVerifier);
        }

        removeCache(unionVerifierList);

        // (2)update db
        updateBatchById(updateUnionVerifierList);

    }

    //***************************************** Object As a Service - cache support ************************************

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
                    }
                }
                break;
            default:
                break;
        }
        return result;
    }

}