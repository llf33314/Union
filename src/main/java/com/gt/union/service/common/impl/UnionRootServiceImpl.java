package com.gt.union.service.common.impl;

import com.gt.union.common.exception.ParameterException;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.entity.basic.UnionMain;
import com.gt.union.service.common.UnionRootService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 *
 * 商家联盟权限统一管理类
 * Created by Administrator on 2017/7/25 0025.
 */
@Service
public class UnionRootServiceImpl implements UnionRootService{

	private Logger logger = Logger.getLogger(UnionRootServiceImpl.class);

	@Override
	public int isUnionMainOwner(Integer busId, UnionMain main){
		try{
			if(CommonUtil.isEmpty(busId) || CommonUtil.isEmpty(main)){
				return -1;
			}
			if(busId.equals(main.getBusId())){
				return 1;
			}
		}catch (Exception e){
			logger.error("判断该商家是否是该盟盟主错误：" + e.getMessage());
			return -2;
		}
		return 0;
	}
}
