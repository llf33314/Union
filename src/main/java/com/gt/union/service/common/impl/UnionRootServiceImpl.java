package com.gt.union.service.common.impl;

import com.alibaba.fastjson.JSON;
import com.gt.union.api.client.dict.DictService;
import com.gt.union.api.client.user.BusUserService;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.constant.basic.UnionCreateInfoRecordConstant;
import com.gt.union.common.constant.basic.UnionMemberConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.*;
import com.gt.union.entity.basic.UnionCreateInfoRecord;
import com.gt.union.entity.basic.UnionMain;
import com.gt.union.entity.basic.UnionMember;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.basic.IUnionCreateInfoRecordService;
import com.gt.union.service.basic.IUnionMainService;
import com.gt.union.service.basic.IUnionMemberService;
import com.gt.union.service.common.IUnionRootService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private static final String HAS_UNION_MEMBER_AUTHORITY = "UnionRootServiceImpl.hasUnionMemberAuthority(unionId, busId)";

	@Autowired
    private RedisCacheUtil redisCacheUtil;

	@Autowired
    private IUnionCreateInfoRecordService unionCreateInfoRecordService;

	@Autowired
    private IUnionMainService unionMainService;

	@Autowired
    private IUnionMemberService unionMemberService;

	@Autowired
	private DictService dictService;

	@Autowired
	private BusUserService busUserService;

    public BusUser getBusUserByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(GET_BUSUSER_BUSID, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }
        String busUserKey = RedisKeyUtil.getBusUserKey(busId);
        if (this.redisCacheUtil.exists(busUserKey)) {//（1）通过busId获取缓存中的busUser对象，如果存在，则直接返回
            Object obj = this.redisCacheUtil.get(busUserKey);
            return JSON.parseObject(obj.toString(), BusUser.class);
        }

        //（2）如果缓存中不存在busUser对象，则调用接口获取
        BusUser busUser = busUserService.getBusUserById(busId);
        // （3）不为空时重新存入缓存
        if (busUser != null) {
            this.redisCacheUtil.set(busUserKey, JSON.toJSONString(busUser));
        }
        return busUser;
    }

    @Override
    public boolean checkBusUserValid(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CHECK_BUSUSER_VALID_BUSID, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }

        BusUser busUser = getBusUserByBusId(busId);
        return checkBusUserValid(busUser);
    }

    @Override
    public boolean checkBusUserValid(BusUser busUser) throws Exception {
        if (busUser == null) {
            throw new ParamException(CHECK_BUSUSER_VALID_BUSID, "无法通过busId获取busUser对象", ExceptionConstant.PARAM_ERROR);
        }
        if(!busUser.getStatus().equals(0)){
            throw new ParamException(CHECK_BUSUSER_VALID_BUSID, "商家账号被冻结", CommonConstant.UNION_USER_FREEZE_MSG);
        }
        return DateTimeKit.getNow().compareTo(busUser.getEndTime()) < 0 ? true : false;
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
        Map<Integer,Object> data = getFreeUnionMainAuthority();
        if(data.containsKey(busUser.getLevel())){
            return true;
        }
        //（2）其他版本，如升级版、高级版需要判断是否购买了盟主服务
        UnionCreateInfoRecord unionCreateInfoRecord = this.unionCreateInfoRecordService.getByBusId(busId);
        return (unionCreateInfoRecord != null && (new Date()).compareTo(unionCreateInfoRecord.getPeriodValidity()) < 0) ? true : false;
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
        String unionMainValidKey = RedisKeyUtil.getUnionMainValidKey(unionId);
        if (this.redisCacheUtil.exists(unionMainValidKey)) {
            return true;
        }

        UnionMain unionMain = this.unionMainService.getById(unionId);
        return checkUnionMainValid(unionMain);
    }

    @Override
    public boolean checkUnionMainValid(UnionMain unionMain) throws Exception {
        if (unionMain == null) {
            throw new ParamException(CHECK_UNIONMAIN_VALID, "无法通过unionId获取unionMain对象", ExceptionConstant.PARAM_ERROR);
        }

        // （2）判断盟主是否过期
        Integer busId = unionMain.getBusId();
        BusUser busUser = this.getBusUserByBusId(busId);
        if (!this.checkBusUserValid(busUser)) {
            return false;
        }

        // （3）判断盟主是否具有盟主服务
        if (!this.hasUnionOwnerAuthority(busId)) {
            // （4）判断联盟是否过期
            if(CommonUtil.isNotEmpty(unionMain.getUnionValidity())){
                if ((new Date()).compareTo(unionMain.getUnionValidity()) > 0) {
                    return false;
                }
            }
        }
        String unionMainValidKey = RedisKeyUtil.getUnionMainValidKey(unionMain.getId());
        // （5）重新存入缓存
        this.redisCacheUtil.set(unionMainValidKey, "1");
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

    @Override
    public boolean hasUnionMemberAuthority(Integer unionId, Integer busId) throws Exception {
        if (unionId == null) {
            throw new ParamException(HAS_UNION_MEMBER_AUTHORITY, "unionId=" + unionId, ExceptionConstant.PARAM_ERROR);
        }
        if (busId == null) {
            throw new ParamException(HAS_UNION_MEMBER_AUTHORITY, "busId=" + busId, ExceptionConstant.PARAM_ERROR);
        }

        UnionMember unionMember = this.unionMemberService.getByUnionIdAndBusId(unionId, busId);
        return hasUnionMemberAuthority(unionMember);
    }

    @Override
    public boolean hasUnionMemberAuthority(UnionMember unionMember) throws Exception {
        return unionMember != null && UnionMemberConstant.OUT_STATUS_PERIOD != unionMember.getOutStaus() ? true : false;
    }

    /**
     * 获取免费的盟主权限
     * @return
     */
    private Map<Integer,Object> getFreeUnionMainAuthority(){
        //TODO 字典数据  缓存起来
        List<Map> list = dictService.getCreateUnionDict();
        Map<Integer,Object> data = new HashMap<Integer,Object>();
        data.put(BusUserConstant.LEVEL_EXTREME, 1);
        return data;
    }

}
