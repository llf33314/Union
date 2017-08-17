package com.gt.union.service.common.impl;

import com.alibaba.fastjson.JSON;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.constant.RedisContant;
import com.gt.union.common.constant.basic.UnionCreateInfoRecordConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.entity.basic.UnionCreateInfoRecord;
import com.gt.union.entity.basic.UnionMain;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.basic.IUnionCreateInfoRecordService;
import com.gt.union.service.basic.IUnionMainService;
import com.gt.union.service.common.IUnionRootService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 商家联盟权限统一管理类
 * Created by Administrator on 2017/7/25 0025.
 */
@Service
public class UnionRootServiceImpl implements IUnionRootService {
    private static final String GET_BUSUSER_BUSID = "UnionRootServiceImpl.getBusUserByBusId()";
    private static final String CHECK_BUSUSER_VALID_BUSID = "UnionRootServiceImpl.checkBusUserValidByBusId()";
    private static final String HAS_UNION_OWNER_AUTHORITY = "UnionRootServiceImpl.hasUnionOwnerAuthority()";
    private static final String HAS_CREATED_UNION = "UnionRootServiceImpl.hasCreatedUnion()";
    private static final String CHECK_UNIONMAIN_VALID = "UnionRootServiceImpl.checkUnionMainValid()";
    private static final String IS_UNION_OWNER_BUSID = "UnionRootServiceImpl.isUnionOwner(busId)";
    private static final String IS_UNION_OWNER_UNIONID_BUSID = "UnionRootServiceImpl.isUnionOwner(unionId, busId)";

	@Autowired
    private RedisCacheUtil redisCacheUtil;

	@Autowired
    private IUnionCreateInfoRecordService unionCreateInfoRecordService;

	@Autowired
    private IUnionMainService unionMainService;

    public BusUser getBusUserByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(GET_BUSUSER_BUSID, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }

        String busIdKey = RedisContant.KEY_BUS_USER + String.valueOf(busId);
        if (this.redisCacheUtil.exists(busIdKey)) {//（1）通过busId获取缓存中的busUser对象，如果存在，则直接返回
            Object obj = this.redisCacheUtil.get(String.valueOf(busId));
            return JSON.parseObject(obj.toString(), BusUser.class);
        }

        //（2）如果缓存中不存在busUser对象，则调用接口获取 //TODO
        BusUser busUser = new BusUser();

        // （3）不为空时重新存入缓存
        if (busUser != null) {
            this.redisCacheUtil.set(String.valueOf(busId), JSON.toJSONString(busUser));
        }
        return busUser;
    }

    @Override
    public boolean checkBusUserValid(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CHECK_BUSUSER_VALID_BUSID, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }

        BusUser busUser = getBusUserByBusId(busId);
        if (busUser == null) {
            throw new ParamException(CHECK_BUSUSER_VALID_BUSID, "无法通过busId获取busUser对象", ExceptionConstant.PARAM_ERROR);
        }
        return (new Date()).compareTo(busUser.getEndTime()) < 0 ? true : false;
    }

    @Override
    public boolean hasUnionOwnerAuthority(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(HAS_UNION_OWNER_AUTHORITY, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }

        //（1）判断商家版本：至尊版、白金版、钻石版直接拥有盟主服务权限
        BusUser busUser = this.getBusUserByBusId(busId);
        busUser.setLevel(BusUserConstant.LEVEL_EXTREME);
        if (busUser == null) {
            throw new ParamException(HAS_UNION_OWNER_AUTHORITY, "无法通过busId获取busUser对象", ExceptionConstant.PARAM_ERROR);
        }
        if (busUser.getLevel() == BusUserConstant.LEVEL_EXTREME || busUser.getLevel() == BusUserConstant.LEVEL_PLATINA || busUser.getLevel() == BusUserConstant.LEVEL_DIAMOND) {
            return true;
        }
        //（2）其他版本，如升级版、高级版需要判断是否购买了盟主服务
        UnionCreateInfoRecord unionCreateInfoRecord = this.unionCreateInfoRecordService.getByBusId(busId);
        return unionCreateInfoRecord != null && (new Date()).compareTo(unionCreateInfoRecord.getPeriodValidity()) < 0 ? true : false;
    }

    @Override
    public boolean hasCreatedUnion(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(HAS_CREATED_UNION, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }

        UnionCreateInfoRecord unionCreateInfoRecord = this.unionCreateInfoRecordService.getByBusId(busId);
        return unionCreateInfoRecord != null && unionCreateInfoRecord.getCreateStatus() == UnionCreateInfoRecordConstant.CREATE_STATUS_YES ? true : false;
    }

    @Override
    public boolean checkUnionMainValid(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CHECK_UNIONMAIN_VALID, " 参数unionId为空", ExceptionConstant.PARAM_ERROR);
        }

        // （1）如果缓存中已存在，说明有效，直接返回
        String unionIdValidKey = RedisContant.KEY_UNION_MAIN_VALID + unionId;
        if (this.redisCacheUtil.exists(unionIdValidKey)) {
            return true;
        }

        // （2）否则，判断联盟是否过期
        UnionMain unionMain = this.unionMainService.getById(unionId);
        if (unionMain == null) {
            throw new ParamException(CHECK_UNIONMAIN_VALID, "无法通过unionId获取unionMain对象", ExceptionConstant.PARAM_ERROR);
        }
        if ((new Date()).compareTo(unionMain.getUnionValidity()) > 0) {
            return false;
        }

        // （3）判断盟主是否过期
        Integer busId = unionMain.getBusId();
        if (!this.checkBusUserValid(busId)) {
            return false;
        }

        // （4）判断盟主是否具有盟主服务
        if (!this.hasUnionOwnerAuthority(busId)) {
            return false;
        }

        // （5）重新存入缓存
        this.redisCacheUtil.set(unionIdValidKey, "1");
        return true;
    }

    @Override
    public boolean isUnionOwner(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(IS_UNION_OWNER_BUSID, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }

        UnionMain unionMain = this.unionMainService.getByBusId(busId);
        return unionMain != null ? true : false;
    }

    @Override
    public boolean isUnionOwner(Integer unionId, Integer busId) throws Exception {
        if (unionId == null) {
            throw new ParamException(IS_UNION_OWNER_UNIONID_BUSID, "参数unionId为空", ExceptionConstant.PARAM_ERROR);
        }
        if (busId == null) {
            throw new ParamException(IS_UNION_OWNER_UNIONID_BUSID, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }

        UnionMain unionMain = this.unionMainService.getById(unionId);
        if (unionMain == null) {
            throw new ParamException(IS_UNION_OWNER_UNIONID_BUSID, "无法通过unionId获取unionMain对象", ExceptionConstant.PARAM_ERROR);
        }

        return busId.equals(unionMain.getBusId());
    }
}
