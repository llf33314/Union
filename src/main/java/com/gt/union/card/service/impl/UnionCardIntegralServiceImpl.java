package com.gt.union.card.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.card.entity.UnionCardIntegral;
import com.gt.union.card.mapper.UnionCardIntegralMapper;
import com.gt.union.card.service.IUnionCardIntegralService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 联盟卡积分 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@Service
public class UnionCardIntegralServiceImpl extends ServiceImpl<UnionCardIntegralMapper, UnionCardIntegral> implements IUnionCardIntegralService {

    @Override
    public Double sumCardIntegralByUnionIdAndStatus(Integer unionId, Integer status) throws Exception {
        if (unionId == null || status == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //TODO

        return null;
    }
}
