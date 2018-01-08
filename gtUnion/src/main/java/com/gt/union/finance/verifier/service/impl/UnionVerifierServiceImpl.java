package com.gt.union.finance.verifier.service.impl;

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

    @Override
    public UnionVerifier getValidByBusIdAndId(Integer busId, Integer verifierId) throws Exception {
        if (busId == null || verifierId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionVerifier> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("id", verifierId)
                .eq("bus_id", busId);

        return unionVerifierDao.selectOne(entityWrapper);
    }

    //********************************************* Base On Business - list ********************************************

    @Override
    public List<UnionVerifier> listValid() throws Exception {
        EntityWrapper<UnionVerifier> entityWrapper = new EntityWrapper<>();

        entityWrapper.eq("del_status", CommonConstant.COMMON_NO);

        return unionVerifierDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionVerifier> listValidFinanceByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        BusUser busUser = busUserService.getBusUserById(busId);
        if (busUser == null) {
            throw new BusinessException(CommonConstant.BUS_NOT_FOUND);
        }

        List<UnionVerifier> result = new ArrayList<>();
        UnionVerifier adminVerifier = new UnionVerifier();
        adminVerifier.setEmployeeName("管理员");
        adminVerifier.setPhone(busUser.getPhone());
        result.add(adminVerifier);

        result.addAll(listValidByBusId(busId));

        return result;
    }

    //********************************************* Base On Business - save ********************************************

    @Override
    public void saveByBusId(Integer busId, String code, UnionVerifier verifier) throws Exception {
        if (busId == null || code == null || verifier == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        // 手机号不能重复
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
        if (existValidByBusIdAndPhone(busId, phone)) {
            throw new BusinessException("已存在手机号为" + phone + "的平台管理人员信息");
        }
        UnionVerifier saveVerifier = new UnionVerifier();
        saveVerifier.setCreateTime(DateUtil.getCurrentDate());
        saveVerifier.setDelStatus(CommonConstant.DEL_STATUS_NO);
        saveVerifier.setBusId(busId);
        saveVerifier.setPhone(phone);
        // 门店信息
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
        // 员工信息
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

    //********************************************* Base On Business - remove ******************************************

    @Override
    public void removeByBusIdAndId(Integer busId, Integer verifierId) throws Exception {
        if (busId == null || verifierId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        UnionVerifier removeVerifier = getValidByBusIdAndId(busId, verifierId);
        if (removeVerifier == null) {
            throw new BusinessException("找不到平台管理人员信息");
        }

        removeById(verifierId);
    }

    //********************************************* Base On Business - update ******************************************

    //********************************************* Base On Business - other *******************************************

    @Override
    public boolean existValidByBusIdAndPhone(Integer busId, String phone) throws Exception {
        if (busId == null || phone == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionVerifier> verifierList = listValidByBusId(busId);
        if (ListUtil.isNotEmpty(verifierList)) {
            for (UnionVerifier verifier : verifierList) {
                if (phone.equals(verifier.getPhone())) {
                    return true;
                }
            }
        }

        return false;
    }

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

    //********************************************* Object As a Service - get ******************************************

    @Override
    public UnionVerifier getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionVerifierDao.selectById(id);
    }

    @Override
    public UnionVerifier getValidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionVerifier> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("id", id);

        return unionVerifierDao.selectOne(entityWrapper);
    }

    @Override
    public UnionVerifier getInvalidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionVerifier> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("id", id);

        return unionVerifierDao.selectOne(entityWrapper);
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

        EntityWrapper<UnionVerifier> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("bus_id", busId);

        return unionVerifierDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionVerifier> listValidByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionVerifier> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId);

        return unionVerifierDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionVerifier> listInvalidByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionVerifier> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("bus_id", busId);

        return unionVerifierDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionVerifier> listByIdList(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionVerifier> entityWrapper = new EntityWrapper<>();
        entityWrapper.in("id", idList).eq(ListUtil.isEmpty(idList), "id", null);

        return unionVerifierDao.selectList(entityWrapper);
    }

    @Override
    public Page pageSupport(Page page, EntityWrapper<UnionVerifier> entityWrapper) throws Exception {
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
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionVerifier> newUnionVerifierList) throws Exception {
        if (newUnionVerifierList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionVerifierDao.insertBatch(newUnionVerifierList);
    }

    //********************************************* Object As a Service - remove ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

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

        unionVerifierDao.updateById(updateUnionVerifier);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionVerifier> updateUnionVerifierList) throws Exception {
        if (updateUnionVerifierList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionVerifierDao.updateBatchById(updateUnionVerifierList);
    }

}