package com.gt.union.service.basic.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.entity.basic.UnionNotice;
import com.gt.union.mapper.basic.UnionNoticeMapper;
import com.gt.union.service.basic.IUnionNoticeService;
import com.gt.union.service.common.UnionRootService;
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
	private UnionRootService unionRootService;

	@Override
	public UnionNotice getByUnionId(Integer unionId) {
		EntityWrapper wrapper = new EntityWrapper<UnionNotice>();
		wrapper.eq("union_id", unionId);
		wrapper.eq("del_status", 0);
		return this.selectOne(wrapper);
	}

	@Override
	@Transactional
	public UnionNotice saveByUnionId(Integer unionId, Integer busId, String noticeContent) throws Exception{
		unionRootService.unionRoot(unionId);
		UnionNotice notice = getByUnionId(unionId);
		if(StringUtil.isEmpty(noticeContent)){
			throw new BusinessException(SAVE_UNIONID, "", "公告内容不能为空");
		}
		if(StringUtil.getStringLength(noticeContent) > 50){
			throw new BusinessException(SAVE_UNIONID, "", "公告内容最多可输入50字");
		}
		notice.setModifytime(new Date());
		notice.setNoticeContent(noticeContent);
		if(CommonUtil.isEmpty(notice)){
			notice.setDelStatus(0);
			notice.setUnionId(unionId);
			notice.setCreatetime(new Date());
			this.insert(notice);
		}else{
			this.updateById(notice);
		}
		return notice;
	}
}
