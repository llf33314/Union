package com.gt.union.card.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.card.entity.UnionCardRoot;
import com.gt.union.card.mapper.UnionCardRootMapper;
import com.gt.union.card.service.IUnionCardRootService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.common.util.RedisKeyUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.opportunity.entity.UnionOpportunityRatio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

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

	@Autowired
	private RedisCacheUtil redisCacheUtil;

	@Override
	public UnionCardRoot getByPhone(String phone) throws Exception{
		if(StringUtil.isEmpty(phone)){
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}
		UnionCardRoot unionCardRoot = null;
		String key = RedisKeyUtil.getUnionCardRootByPhoneKey(phone);

		if(redisCacheUtil.exists(key)){
			String tempStr = this.redisCacheUtil.get(key);
			unionCardRoot = JSONArray.parseObject(tempStr, UnionCardRoot.class);
			return unionCardRoot;
		}
		EntityWrapper entityWrapper = new EntityWrapper<>();
		entityWrapper.eq("phone", phone);
		entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);
		unionCardRoot = this.selectOne(entityWrapper);
		if(unionCardRoot != null){
			redisCacheUtil.set(key,unionCardRoot);
		}
		return unionCardRoot;
	}

	@Override
	public UnionCardRoot getByCardNo(String cardNo) throws Exception {
		if(StringUtil.isEmpty(cardNo)){
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}
		UnionCardRoot unionCardRoot = null;
		String key = RedisKeyUtil.getUnionCardRootByCardNoKey(cardNo);

		if(redisCacheUtil.exists(key)){
			String tempStr = this.redisCacheUtil.get(key);
			unionCardRoot = JSONArray.parseObject(tempStr, UnionCardRoot.class);
			return unionCardRoot;
		}
		EntityWrapper entityWrapper = new EntityWrapper<>();
		entityWrapper.eq("number", cardNo);
		entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);
		unionCardRoot = this.selectOne(entityWrapper);
		redisCacheUtil.set(key,unionCardRoot);
		return unionCardRoot;
	}

	@Override
	public UnionCardRoot getById(Integer rootId) {
		EntityWrapper entityWrapper = new EntityWrapper<>();
		entityWrapper.eq("id", rootId);
		entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);
		return this.selectOne(entityWrapper);
	}

	@Override
	public UnionCardRoot createUnionCardRoot(String phone) {
		UnionCardRoot root = new UnionCardRoot();
		root.setPhone(phone);
		root.setDelStatus(CommonConstant.DEL_STATUS_NO);
		root.setCreatetime(new Date());
		root.setIntegral(0d);
		root.setNumber(generateCardNo());
		this.insert(root);
		return root;
	}


	//生成联盟卡号  8位
	private String generateCardNo(){
		int machineId = 10;// 最大支持1-9个集群机器部署
		int hashCodeV = UUID.randomUUID().toString().hashCode();
		if (hashCodeV < 0) {// 有可能是负数
			hashCodeV = -hashCodeV;
		}
		// 0 代表前面补充0
		// 4 代表长度为4
		// d 代表参数为正数型
		return machineId + String.format("%010d", hashCodeV);
	}
}
