package com.gt.union.main.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.main.entity.UnionMainCharge;
import com.gt.union.main.mapper.UnionMainChargeMapper;
import com.gt.union.main.service.IUnionMainChargeService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 联盟升级收费 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@Service
public class UnionMainChargeServiceImpl extends ServiceImpl<UnionMainChargeMapper, UnionMainCharge> implements IUnionMainChargeService {
    //-------------------------------------------------- get ----------------------------------------------------------

    /**
     * 根据联盟id和联盟卡类型获取联盟卡信息
     *
     * @param unionId {not null} 联盟卡
     * @param type    {not null} 联盟卡类型
     * @return
     */
    @Override
    public UnionMainCharge getByUnionIdAndType(Integer unionId, int type) {
        EntityWrapper entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .eq("type", type);

        return this.selectOne(entityWrapper);
    }

    /**
     * 根据联盟id、红黑卡类型和是否启用，获取联盟升级收费信息
     *
     * @param unionId     {not null} 联盟id
     * @param type        {not null} 红黑卡类型
     * @param isAvailable {not null} 是否启用
     * @return
     * @throws Exception
     */
    @Override
    public UnionMainCharge getByUnionIdAndTypeAndIsAvailable(Integer unionId, Integer type, Integer isAvailable) throws Exception {
        if (unionId == null || type == null || isAvailable == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .eq("type", type)
                .eq("is_available", isAvailable);
        return this.selectOne(entityWrapper);
    }

    //------------------------------------------ list(include page) ---------------------------------------------------
    //------------------------------------------------- update --------------------------------------------------------
    //------------------------------------------------- save ----------------------------------------------------------
    //------------------------------------------------- count ---------------------------------------------------------
    //------------------------------------------------ boolean --------------------------------------------------------
}
