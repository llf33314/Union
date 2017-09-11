package com.gt.union.card.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.card.entity.UnionCardRoot;
import com.gt.union.card.mapper.UnionCardRootMapper;
import com.gt.union.card.service.IUnionCardRootService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.StringUtil;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 联盟卡主信息 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@Service
public class UnionCardRootServiceImpl extends ServiceImpl<UnionCardRootMapper, UnionCardRoot> implements IUnionCardRootService {

	@Override
	public UnionCardRoot getByPhone(String phone) throws Exception{
		if(StringUtil.isEmpty(phone)){
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}
		EntityWrapper entityWrapper = new EntityWrapper<>();
		entityWrapper.eq("phone", phone);
		entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);
		return this.selectOne(entityWrapper);
	}

	@Override
	public UnionCardRoot getByCardNo(String cardNo) throws Exception {
		if(StringUtil.isEmpty(cardNo)){
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}
		EntityWrapper entityWrapper = new EntityWrapper<>();
		entityWrapper.eq("number", cardNo);
		entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);
		return this.selectOne(entityWrapper);
	}

}
