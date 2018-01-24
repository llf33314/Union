package com.gt.union.user.introduction.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.user.introduction.entity.UnionUserIntroduction;
import com.gt.union.user.introduction.vo.UnionUserIntroductionVO;

import java.util.List;

/**
 * 联盟商家简介 服务接口
 *
 * @author linweicong
 * @version 2018-01-24 16:24:13
 */
public interface IUnionUserIntroductionService {

	/**
	 *	查询有效商家简介
	 * @param busId
	 * @throws Exception 统一处理异常
	 * @return
	 */
	List<UnionUserIntroduction> listValidByBusId(Integer busId) throws Exception;

	/**
	 * 保存商家简介信息
	 * @param busId	商家id
	 * @param unionUserIntroductionVO	简介信息
	 * @throws Exception 统一处理异常
	 */
	void saveOrUpdate(Integer busId, UnionUserIntroductionVO unionUserIntroductionVO) throws Exception;

}