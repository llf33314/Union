package com.gt.union.main.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.api.bean.session.BusUser;
import com.gt.union.api.client.dict.IDictService;
import com.gt.union.api.client.user.IBusUserService;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.DateUtil;
import com.gt.union.main.entity.UnionMainPermit;
import com.gt.union.main.mapper.UnionMainPermitMapper;
import com.gt.union.main.service.IUnionMainPermitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 联盟许可，盟主服务 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@Service
public class UnionMainPermitServiceImpl extends ServiceImpl<UnionMainPermitMapper, UnionMainPermit> implements IUnionMainPermitService {
    @Autowired
    private IBusUserService busUserService;

    @Autowired
    private IDictService dictService;

    //-------------------------------------------------- get ----------------------------------------------------------

    /**
     * 根据商家id获取联盟服务许可
     *
     * @param busId {not null} 商家id
     * @return
     * @throws Exception
     */
    @Override
    public UnionMainPermit getByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId);
        return this.selectOne(entityWrapper);
    }

    /**
     * 根据商家id和许可id获取联盟服务许可信息
     *
     * @param busId {not null} 商家id
     * @param id    {not null} 联盟许可id
     * @return
     * @throws Exception
     */
    @Override
    public UnionMainPermit getByBusIdAndId(Integer busId, Integer id) throws Exception {
        if (busId == null || id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId)
                .eq("id", id);
        return this.selectOne(entityWrapper);
    }

    /**
     * 获取免费的盟主权限
     *
     * @return
     */
    private Map<Integer, Object> getFreeUnionMainAuthority() {
        List<Map> list = dictService.getCreateUnionDict();
        Map<Integer, Object> data = new HashMap<Integer, Object>();
        for (Map map : list) {
            String payment = map.get("item_value").toString().split(",")[0];
            if (payment.equals("")) {
                data.put(CommonUtil.toInteger(map.get("item_key")), 1);
            }
        }
        return data;
    }

    //------------------------------------------ list(include page) ---------------------------------------------------
    //------------------------------------------------- update --------------------------------------------------------
    //------------------------------------------------- save ----------------------------------------------------------
    //------------------------------------------------- count ---------------------------------------------------------
    //------------------------------------------------ boolean --------------------------------------------------------

    @Override
    public boolean hasUnionMainPermit(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        /*BusUser busUser = this.busUserService.getBusUserById(busId);
        if (busUser == null) {
            throw new ParamException("商家帐号不存在");
        }*/
        //TODO 开发用，待删
        BusUser busUser = new BusUser();
        busUser.setId(busId);
        busUser.setLevel(BusUserConstant.LEVEL_EXTREME);
        //（1）判断商家版本：至尊版、白金版、钻石版直接拥有盟主服务许可
        Map<Integer, Object> data = getFreeUnionMainAuthority();
        if (data.containsKey(busUser.getLevel())) {
            return true;
        }
        //（2）其他版本，如升级版、高级版需要判断是否购买了盟主服务
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId);
        UnionMainPermit unionMainPermit = this.selectOne(entityWrapper);
        if (unionMainPermit != null && DateUtil.getCurrentDate().compareTo(unionMainPermit.getValidity()) < 0) {
            return true;
        }
        return false;
    }

}
