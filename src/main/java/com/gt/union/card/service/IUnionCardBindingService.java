package com.gt.union.card.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.api.entity.result.UnionBindCardResult;
import com.gt.union.card.entity.UnionCard;
import com.gt.union.card.entity.UnionCardBinding;

import java.util.List;

/**
 * <p>
 * 联盟绑定表 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-11
 */
public interface IUnionCardBindingService extends IService<UnionCardBinding> {

	UnionBindCardResult bindUnionCard(Integer busid, Integer id, String phone, String code) throws Exception;

}
