package com.gt.union.service.card.impl;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.exception.ParameterException;
import com.gt.union.common.util.StringUtil;
import com.gt.union.entity.card.UnionCardDivideRecord;
import com.gt.union.mapper.card.UnionCardDivideRecordMapper;
import com.gt.union.service.card.IUnionCardDivideRecordService;
import com.gt.union.vo.card.UnionCardDivideRecordVo;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商家售卡分成记录 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
@Service
public class UnionCardDivideRecordServiceImpl extends ServiceImpl<UnionCardDivideRecordMapper, UnionCardDivideRecord> implements IUnionCardDivideRecordService {

	@Override
	public Page getUnionCardDivideRecordList(Page page, UnionCardDivideRecordVo vo) {
		return null;
	}
}
