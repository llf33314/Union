package com.gt.union.service.basic.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.common.util.RedisKeyUtil;
import com.gt.union.entity.basic.UnionInfoDict;
import com.gt.union.mapper.basic.UnionInfoDictMapper;
import com.gt.union.service.basic.IUnionInfoDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 联盟设置申请填写信息 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
@Service
public class UnionInfoDictServiceImpl extends ServiceImpl<UnionInfoDictMapper, UnionInfoDict> implements IUnionInfoDictService {
	private static final String GET_UNION_INFO_DICT = "UnionInfoDictServiceImpl.getUnionInfoDict()";

	@Autowired
	private RedisCacheUtil redisCacheUtil;

	@Override
	public List<UnionInfoDict> getUnionInfoDict(Integer unionId) {
		String key = RedisKeyUtil.getUnionInfoDictKey(unionId);
		if(redisCacheUtil.exists(key)){
			Object obj = redisCacheUtil.get(key);
			if(CommonUtil.isNotEmpty(obj)){
				return JSON.parseObject(obj.toString(),List.class);
			}
		}
		EntityWrapper entityWrapper = new EntityWrapper<UnionInfoDict>();
		entityWrapper.eq("union_id", unionId);
		List<UnionInfoDict> list = this.selectList(entityWrapper);
		if(ListUtil.isNotEmpty(list)){
			if(ListUtil.isNotEmpty(list)){
				redisCacheUtil.set(key, JSON.toJSONString(list));
			}
		}
		return list;
	}
}
