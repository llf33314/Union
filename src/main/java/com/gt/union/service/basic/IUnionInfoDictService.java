package com.gt.union.service.basic;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.entity.basic.UnionInfoDict;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 联盟设置申请填写信息 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
public interface IUnionInfoDictService extends IService<UnionInfoDict> {

	/**
	 * 根据联盟id获取设置的推荐申请信息
	 * @param unionId
	 * @return
	 */
	List<Map<String,Object>> getUnionInfoDict(Integer unionId);
}
