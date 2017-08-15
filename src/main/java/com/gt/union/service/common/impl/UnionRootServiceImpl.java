package com.gt.union.service.common.impl;

import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.DaoUtil;
import com.gt.union.common.util.DateTimeKit;
import com.gt.union.entity.basic.UnionCreateInfoRecord;
import com.gt.union.entity.basic.UnionMain;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.basic.IUnionCreateInfoRecordService;
import com.gt.union.service.basic.IUnionMainService;
import com.gt.union.service.common.UnionRootService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 商家联盟权限统一管理类
 * Created by Administrator on 2017/7/25 0025.
 */
@Service
public class UnionRootServiceImpl implements UnionRootService {

	private static final String BUSUSER_OVERDUE = "UnionRootServiceImpl.isUnionOverdue()";
	private static final String UNION_ROOT = "UnionRootServiceImpl.unionRoot()";

	@Autowired
	private IUnionMainService unionMainService;

	private Logger logger = Logger.getLogger(UnionRootServiceImpl.class);


	@Override
	public int unionRoot(Integer unionId) throws Exception {
		UnionMain unionMain = unionMainService.getUnionMainById(unionId);
		return unionRoot(unionMain);
	}

	@Override
	public int unionRoot(UnionMain main) throws Exception {
		if(CommonUtil.isEmpty(main)){
			throw new BusinessException(UNION_ROOT,"unionMain == null", CommonConstant.UNION_OVERDUE_MSG);
		}
		return isUnionOverdue(main);
	}


	private int isUnionOverdue(UnionMain unionMain) throws Exception {
		BusUser user = new BusUser();//TODO 获取商家账号
		if(CommonUtil.isEmpty(user)){
			throw new BusinessException(BUSUSER_OVERDUE,"user == null", CommonConstant.UNION_OVERDUE_MSG);
		}
		if(false){//TODO 判断盟主账号是否过期
			throw new BusinessException(BUSUSER_OVERDUE,"user == null", CommonConstant.UNION_OVERDUE_MSG);
		}
		if(true){//TODO 根据字典属性，判断商家等级是否需要付费
			return 1;//不需要收费
		}
		if(CommonUtil.isEmpty(unionMain.getUnionValidity())){
			throw new BusinessException(BUSUSER_OVERDUE,"unionMain.unionValidity == null", CommonConstant.UNION_OVERDUE_MSG);
		}
		if(!DateTimeKit.laterThanNow(unionMain.getUnionValidity())){
			throw new BusinessException(BUSUSER_OVERDUE,"unionValidity laterThanNow false", CommonConstant.UNION_OVERDUE_MSG);
		}
		return 1;
	}



}
