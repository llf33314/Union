package com.gt.union.service.basic.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.constant.basic.UnionNoticeConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.entity.basic.UnionNotice;
import com.gt.union.mapper.basic.UnionNoticeMapper;
import com.gt.union.service.basic.IUnionNoticeService;
import com.gt.union.service.common.IUnionRootService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * <p>
 * 联盟公告 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
@Service
public class UnionNoticeServiceImpl extends ServiceImpl<UnionNoticeMapper, UnionNotice> implements IUnionNoticeService {
	private static final String SAVE_UNIONID = "UnionNoticeServiceImpl.saveByUnionId()";

	@Autowired
	private IUnionRootService unionRootService;

	@Override
	public UnionNotice getByUnionId(Integer unionId) {
		EntityWrapper wrapper = new EntityWrapper<UnionNotice>();
		wrapper.eq("union_id", unionId);
		wrapper.eq("del_status", UnionNoticeConstant.DEL_STATUS_NO);
		return this.selectOne(wrapper);
	}

	@Override
	@Transactional
	public UnionNotice saveByUnionId(Integer unionId, Integer busId, String noticeContent) throws Exception{
		if(unionId == null){
			throw new ParamException(SAVE_UNIONID, "参数unionId为空", ExceptionConstant.PARAM_ERROR);
		}
		if(busId == null){
			throw new ParamException(SAVE_UNIONID, "参数busId为空", ExceptionConstant.PARAM_ERROR);
		}
		if(StringUtil.isEmpty(noticeContent)){
			throw new ParamException(SAVE_UNIONID, "参数noticeContent为空", ExceptionConstant.PARAM_ERROR);
		}
		if(!unionRootService.checkUnionMainValid(unionId)){
			throw new BusinessException(SAVE_UNIONID, "", CommonConstant.UNION_OVERDUE_MSG);
		}
		if(!unionRootService.isUnionOwner(unionId,busId)){
			throw new BusinessException(SAVE_UNIONID, "", CommonConstant.UNION_OWNER_NON_AUTHORITY_MSG);
		}
		UnionNotice notice = getByUnionId(unionId);
		if(StringUtil.getStringLength(noticeContent) > UnionNoticeConstant.NOTICE_MAX_LENGTH){
			throw new BusinessException(SAVE_UNIONID, "", "公告内容不可超过50字");
		}
		notice.setModifytime(new Date());
		notice.setNoticeContent(noticeContent);
		if(CommonUtil.isEmpty(notice)){
			notice.setDelStatus(UnionNoticeConstant.DEL_STATUS_NO);
			notice.setUnionId(unionId);
			notice.setCreatetime(new Date());
			this.insert(notice);
		}else{
			this.updateById(notice);
		}
		return notice;
	}
}
