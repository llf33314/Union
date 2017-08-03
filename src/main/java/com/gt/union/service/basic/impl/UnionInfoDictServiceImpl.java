package com.gt.union.service.basic.impl;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.util.DaoUtil;
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

	@Autowired
	private DaoUtil daoUtil;

	@Override
	public List<Map<String, Object>> getUnionInfoDict(final Integer unionId) {
		List<Map<String, Object>> list = daoUtil.queryForList("SELECT t2.* from t_union_info_dict t1 LEFT JOIN t_man_dict_items t2 on t1.dict_item_id = t2.id  where union_id = ? order by t1.id asc",unionId);
		return list;
	}
}
