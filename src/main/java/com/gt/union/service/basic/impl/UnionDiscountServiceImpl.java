package com.gt.union.service.basic.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.basic.UnionDiscountConstant;
import com.gt.union.common.util.DoubleUtil;
import com.gt.union.entity.basic.UnionDiscount;
import com.gt.union.mapper.basic.UnionDiscountMapper;
import com.gt.union.service.basic.IUnionDiscountService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 联盟商家折扣 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
@Service
public class UnionDiscountServiceImpl extends ServiceImpl<UnionDiscountMapper, UnionDiscount> implements IUnionDiscountService {

    @Override
    public void updateUnionDiscount(Integer unionId, Integer fromBusId, Integer toBusId, Double discount) throws Exception {
        if (unionId == null) {
            throw new Exception("UnionDiscountServiceImpl.updateUnionDiscount():参数unionId不能为空!");
        }
        if (fromBusId == null) {
            throw new Exception("UnionDiscountServiceImpl.updateUnionDiscount():参数fromBusId不能为空!");
        }
        if (toBusId == null) {
            throw new Exception("UnionDiscountServiceImpl.updateUnionDiscount():参数toBusId不能为空!");
        }
        if (discount == null) {
            throw new Exception("UnionDiscountServiceImpl.updateUnionDiscount():参数discount不能为空!");
        } else {
            if (!DoubleUtil.checkDecimalPrecision(discount, 2)) {
                throw new Exception("UnionDiscountServiceImpl.updateUnionDiscount():参数discount的有效小数点长度不能超过2位!");
            }
        }

        UnionDiscount unionDiscount = new UnionDiscount();
        unionDiscount.setDelStatus(UnionDiscountConstant.DEL_STATUS_NO);
        unionDiscount.setUnionId(unionId);
        unionDiscount.setFromBusId(fromBusId);
        unionDiscount.setToBusId(toBusId);
        unionDiscount.setDiscount(discount);

        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", UnionDiscountConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .eq("from_bus_id", fromBusId)
                .eq("to_bus_id", toBusId);
        if (this.isExistUnionDiscount(entityWrapper)) {
            unionDiscount.setModifytime(new Date());
            this.update(unionDiscount, entityWrapper);
        } else {
            unionDiscount.setCreatetime(new Date());
            this.insert(unionDiscount);
        }
    }

    @Override
    public boolean isExistUnionDiscount(Wrapper wrapper) throws Exception {
        if (wrapper == null) {
            throw  new Exception("UnionDiscountServiceImpl.isExistUnionDiscount():参数wrapper不能为空!");
        }
        return this.selectCount(wrapper) > 0 ? true : false;
    }
}
