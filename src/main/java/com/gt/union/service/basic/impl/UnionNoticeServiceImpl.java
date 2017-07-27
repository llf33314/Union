package com.gt.union.service.basic.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParameterException;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.entity.basic.UnionNotice;
import com.gt.union.mapper.basic.UnionNoticeMapper;
import com.gt.union.service.basic.IUnionNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

	@Autowired
	private UnionNoticeMapper unionNoticeMapper;

	@Override
	public UnionNotice saveNotice(UnionNotice notice) throws Exception {
		//TODO 联盟公告判断是否该联盟盟主，判断联盟是否有效

		if(CommonUtil.isEmpty(notice.getUnionId())){
			throw new ParameterException("参数错误");
		}
		if(StringUtil.isEmpty(notice.getNoticeContent())){
			throw new BusinessException("公告内容不能为空");
		}
		if(StringUtil.getStringLength(notice.getNoticeContent()) > 50){
			throw new BusinessException("公告内容最多可输入50字");
		}
		notice.setModifytime(new Date());
		if(CommonUtil.isEmpty(notice.getId())){
			EntityWrapper wrapper = new EntityWrapper<UnionNotice>();
			wrapper.eq("union_id",notice.getUnionId());
			Integer count = unionNoticeMapper.selectCount(wrapper);
			if(count > 0){
				throw new BusinessException("系统错误，请刷新后重试");
			}
			notice.setCreatetime(new Date());
			unionNoticeMapper.insert(notice);
		}else{
			unionNoticeMapper.updateById(notice);
		}
		return notice;
	}
}
