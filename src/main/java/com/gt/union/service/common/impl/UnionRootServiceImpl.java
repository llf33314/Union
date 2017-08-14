package com.gt.union.service.common.impl;

import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.DaoUtil;
import com.gt.union.entity.basic.UnionMain;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.basic.IUnionMainService;
import com.gt.union.service.common.UnionRootService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * 商家联盟权限统一管理类
 * Created by Administrator on 2017/7/25 0025.
 */
@Service
public class UnionRootServiceImpl implements UnionRootService{

	@Autowired
	private DaoUtil daoUtil;

	@Autowired
	private IUnionMainService unionMainService;

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

	@Override
	public int isBusUserOverdue(BusUser user) {
		try{
			if(CommonUtil.isEmpty(user)){
				return -1;
			}
			//TODO 根据商家id查询商家账号
		}catch (Exception e){

		}
		return 0;
	}

	@Override
	public int isUnionMainOverdue(UnionMain main) {
		try{
			if(CommonUtil.isEmpty(main)){
				return -1;
			}
			//TODO 判断联盟有效期


		}catch (Exception e){

		}
		return 0;
	}

	@Override
	public int unionRoot(Integer unionId) throws Exception{
		UnionMain unionMain = unionMainService.getUnionMainById(unionId);
		return unionRoot(unionMain);
	}

	@Override
	public int unionRoot(UnionMain main) {
		if(main == null){

		}
		Integer busId = main.getBusId();//盟主商家id
		//TODO 查询盟主信息
		return 1;
	}
}
