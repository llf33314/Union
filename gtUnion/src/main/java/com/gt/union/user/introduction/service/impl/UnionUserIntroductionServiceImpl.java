package com.gt.union.user.introduction.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.user.introduction.constant.UserIntroductionConstant;
import com.gt.union.user.introduction.dao.IUnionUserIntroductionDao;
import com.gt.union.user.introduction.entity.UnionUserIntroduction;
import com.gt.union.user.introduction.service.IUnionUserIntroductionService;
import com.gt.union.user.introduction.vo.UnionUserIntroductionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 联盟商家简介 服务实现类
 *
 * @author linweicong
 * @version 2018-01-24 16:24:13
 */
@Service
public class UnionUserIntroductionServiceImpl implements IUnionUserIntroductionService {
    @Autowired
    private IUnionUserIntroductionDao unionUserIntroductionDao;


	@Override
	public List<UnionUserIntroduction> listValidByBusId(Integer busId) throws Exception{
		if(busId == null){
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}
		EntityWrapper wrapper = new EntityWrapper<>();
		wrapper.eq("bus_id", busId)
				.eq("del_status", CommonConstant.DEL_STATUS_NO)
				.orderBy("id", true);
		return unionUserIntroductionDao.selectList(wrapper);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveOrUpdate(Integer busId, UnionUserIntroductionVO unionUserIntroductionVO) throws Exception{
		if(busId == null || unionUserIntroductionVO == null){
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}
		List<UnionUserIntroduction> introductionList = unionUserIntroductionVO.getUserIntroductionList();
		if(ListUtil.isEmpty(introductionList)){
			throw new ParamException("请填写商家简介信息");
		}
		List<UnionUserIntroduction> list = listValidByBusId(busId);
		if(ListUtil.isNotEmpty(list)){
			//编辑 删除以前的
			List<UnionUserIntroduction> delList = new ArrayList<UnionUserIntroduction>();
			for(UnionUserIntroduction introduction : list){
				UnionUserIntroduction userIntroduction = new UnionUserIntroduction();
				userIntroduction.setId(introduction.getId());
				userIntroduction.setDelStatus(CommonConstant.DEL_STATUS_YES);
				delList.add(userIntroduction);
			}
			unionUserIntroductionDao.updateBatchById(delList);
		}
		for(UnionUserIntroduction introduction : introductionList){
			if(introduction.getType() == UserIntroductionConstant.INTRODUCTION_TEXT_TYPE){
				if (StringUtil.getStringLength(introduction.getContent()) > 300) {
					throw new BusinessException("商家简介文字描述不可大于300字");
				}
			}
			introduction.setId(null);
			introduction.setBusId(busId);
			introduction.setCreateTime(new Date());
			introduction.setModifyTime(new Date());
			introduction.setDelStatus(CommonConstant.DEL_STATUS_NO);
		}
		unionUserIntroductionDao.insertBatch(introductionList);
	}

}