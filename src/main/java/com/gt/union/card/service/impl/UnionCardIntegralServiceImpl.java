package com.gt.union.card.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.card.entity.UnionCardIntegral;
import com.gt.union.card.mapper.UnionCardIntegralMapper;
import com.gt.union.card.service.IUnionCardIntegralService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.ListUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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

	@Override
	public List<Map<String,Object>> sumByCardIdsAndStatus(List<Integer> cardIds, Integer status) throws Exception {
    	if(ListUtil.isEmpty(cardIds) || status == null){
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}
		EntityWrapper entityWrapper = new EntityWrapper<>();
    	entityWrapper.in("card_id", cardIds.toArray());
    	entityWrapper.eq("status", status);
    	entityWrapper.setSqlSelect("card_id as cardId, IFNULL(SUM(integral),0)AS integral");
		return this.selectMaps(entityWrapper);
	}
}
