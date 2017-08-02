package com.gt.union.service.basic.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.basic.UnionMemberConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.entity.basic.UnionApplyInfo;
import com.gt.union.mapper.basic.UnionApplyInfoMapper;
import com.gt.union.service.basic.IUnionApplyInfoService;
import com.gt.union.service.basic.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 联盟成员申请信息 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
@Service
public class UnionApplyInfoServiceImpl extends ServiceImpl<UnionApplyInfoMapper, UnionApplyInfo> implements IUnionApplyInfoService {

	@Autowired
	private IUnionMemberService unionMemberService;

	@Override
	public void updateUnionApplyInfo(UnionApplyInfo unionApplyInfo, Integer busId, Integer unionId) throws Exception{
		unionMemberService.isMemberValid(busId,unionId);
		this.updateById(unionApplyInfo);
	}
}
