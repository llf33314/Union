package com.gt.union.main.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
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

    @Override
    public UnionMainCharge getByUnionIdAndType(Integer unionId, int redCardType) {
        EntityWrapper entityWrapper = new EntityWrapper<UnionMainCharge>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);
        entityWrapper.eq("union_id",unionId);
        entityWrapper.eq("type",redCardType);
        UnionMainCharge unionMainCharge = this.selectOne(entityWrapper);
        return unionMainCharge;
    }
}
