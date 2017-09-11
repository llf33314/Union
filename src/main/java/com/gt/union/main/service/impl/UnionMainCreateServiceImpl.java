package com.gt.union.main.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.api.client.dict.IDictService;
import com.gt.union.api.client.user.IBusUserService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.entity.BusUser;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.DateTimeKit;
import com.gt.union.main.entity.UnionMainCreate;
import com.gt.union.main.entity.UnionMainPermit;
import com.gt.union.main.mapper.UnionMainCreateMapper;
import com.gt.union.main.service.IUnionMainCreateService;
import com.gt.union.main.service.IUnionMainPermitService;
import com.gt.union.member.service.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 创建联盟 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@Service
public class UnionMainCreateServiceImpl extends ServiceImpl<UnionMainCreateMapper, UnionMainCreate> implements IUnionMainCreateService {

    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IBusUserService busUserService;

    @Autowired
    private IDictService dictService;

    @Autowired
    private IUnionMainPermitService unionMainPermitService;

    @Override
    public boolean hasCreateUnionMain(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId);
        UnionMainCreate unionMainCreate = this.selectOne(entityWrapper);
        return unionMainCreate != null ? true : false;
    }

    /**
     * 根据商家id创建联盟
     *
     * @param busId {not null} 商家id
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> instanceByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        Map<String, Object> data = new HashMap<String, Object>();
        // 1、判断商家是否已是盟主
        if (this.unionMemberService.isUnionOwner(busId)) {
            throw new BusinessException("您已是联盟盟主，不可创建");
        }
        //2、判断是否有创建联盟的权限
        BusUser busUser = this.busUserService.getBusUserById(busId);
        if (busUser == null) {
            throw new BusinessException("账号不存在");
        }
        if (this.busUserService.isBusUserValid(busUser)) {
            throw new BusinessException(CommonConstant.UNION_BUS_OVERDUE_MSG);
        }
        List<Map> createDict = dictService.getCreateUnionDict();//创建联盟的权限
        boolean flag = false;
        Map info = null;
        for (Map map : createDict) {
            if (CommonUtil.toInteger(map.get("item_key")).equals(busUser.getLevel())) {
                info = map;
                flag = true;
                break;
            }
        }
        if (!flag) {
            throw new BusinessException("您没有创建联盟的权限");
        }
        //3、根据等级判断是否需要付费
        String itemValue = info.get("item_value").toString();//根据等级获取创建联盟的权限
        String[] arrs = itemValue.split(",");
        String isPay = arrs[0];
        if (isPay.equals("0")) {//不需要付费
            data.put("save", 1);//去创建联盟
        } else {
            //4、需要付费，判断是否已经付费
            UnionMainPermit unionMainPermit = this.unionMainPermitService.getByBusId(busId);
            if (unionMainPermit == null) {
                data.put("pay", 1);//去支付
                List<Map> list = this.dictService.getUnionCreatePackage();
                data.put("payItems", list);
                return data;
            }
            if (DateTimeKit.laterThanNow(unionMainPermit.getValidity())) {//未过期
                UnionMainCreate unionMainCreate = this.getByBusIdAndPermitId(busId, unionMainPermit.getId());
                if (unionMainCreate != null) {
                    throw new BusinessException("联盟未过期，不可创建");
                } else {
                    data.put("save", 1);//去创建联盟
                }
            } else {//过期了
                data.put("pay", 1);//去支付
                List<Map> list = dictService.getUnionCreatePackage();
                data.put("payItems", list);
            }
        }
        return data;
    }

    /**
     * 根据商家id和服务许可id，获取联盟创建信息
     *
     * @param busId    {not null} 商家id
     * @param permitId {not null} 联盟服务许可
     * @return
     * @throws Exception
     */
    @Override
    public UnionMainCreate getByBusIdAndPermitId(Integer busId, Integer permitId) throws Exception {
        if (busId == null || permitId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId)
                .eq("permit_id", permitId);
        return this.selectOne(entityWrapper);
    }
}
