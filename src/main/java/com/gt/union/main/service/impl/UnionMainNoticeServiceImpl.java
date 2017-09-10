package com.gt.union.main.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.main.constant.NoticeConstant;
import com.gt.union.main.entity.UnionMainNotice;
import com.gt.union.main.mapper.UnionMainNoticeMapper;
import com.gt.union.main.service.IUnionMainNoticeService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 联盟公告 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@Service
public class UnionMainNoticeServiceImpl extends ServiceImpl<UnionMainNoticeMapper, UnionMainNotice> implements IUnionMainNoticeService {

	@Override
	public UnionMainNotice getByUnionId(Integer unionId) {
		EntityWrapper wrapper = new EntityWrapper<UnionMainNotice>();
		wrapper.eq("union_id", unionId);
		wrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);
		return this.selectOne(wrapper);
	}

	@Override
	public UnionMainNotice saveByUnionId(Integer unionId, Integer busId, String content) throws Exception {
		//TODO  添加联盟公告
		if(unionId == null){
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}
		if(busId == null){
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}
		if(StringUtil.isEmpty(content)){
			throw new ParamException("联盟公告不能为空");
		}
		if(StringUtil.getStringLength(content) > NoticeConstant.NOTICE_MAX_LENGTH){
			throw new BusinessException("公告内容不可超过50字");
		}
		UnionMainNotice notice = getByUnionId(unionId);
		notice.setModifytime(new Date());
		notice.setContent(content);
		if(CommonUtil.isEmpty(notice)){
			notice.setDelStatus(CommonConstant.DEL_STATUS_NO);
			notice.setUnionId(unionId);
			notice.setCreatetime(new Date());
			this.insert(notice);
		}else{
			this.updateById(notice);
		}
		return notice;
	}
}
