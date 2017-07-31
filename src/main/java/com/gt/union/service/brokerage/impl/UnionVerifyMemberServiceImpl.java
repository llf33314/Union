package com.gt.union.service.brokerage.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.brokerage.UnionVerifyMemberConstant;
import com.gt.union.common.exception.ParameterException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.entity.brokerage.UnionBrokerageWithdrawalsRecord;
import com.gt.union.entity.brokerage.UnionVerifyMember;
import com.gt.union.mapper.brokerage.UnionVerifyMemberMapper;
import com.gt.union.service.brokerage.IUnionBrokerageWithdrawalsRecordService;
import com.gt.union.service.brokerage.IUnionVerifyMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 联盟佣金平台管理人员 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
@Service
public class UnionVerifyMemberServiceImpl extends ServiceImpl<UnionVerifyMemberMapper, UnionVerifyMember> implements IUnionVerifyMemberService {

	@Autowired
	private IUnionBrokerageWithdrawalsRecordService unionBrokerageWithdrawalsRecordService;


	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delUnionVerifyMember(Integer id) {
		EntityWrapper<UnionBrokerageWithdrawalsRecord> wrapper = new EntityWrapper<UnionBrokerageWithdrawalsRecord>();
		wrapper.eq("union_verify_member", id);//提现的人员id
		int count = unionBrokerageWithdrawalsRecordService.selectCount(wrapper);
		if(count > 0){
			UnionVerifyMember unionVerifyMember = new UnionVerifyMember();
			unionVerifyMember.setId(id);
			unionVerifyMember.setDelStatus(1);
			this.updateById(unionVerifyMember);
		}else{
			this.deleteById(id);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void saveUnionVerifyMember(UnionVerifyMember unionVerifyMember) throws Exception {
		if(CommonUtil.isEmpty(unionVerifyMember.getMemberName())){
			throw new ParameterException(UnionVerifyMemberConstant.UNION_VERIFY_MEMBER_NAME_NULL);
		}else {
			if(StringUtil.getStringLength(unionVerifyMember.getMemberName()) > 5){
				throw new ParameterException(UnionVerifyMemberConstant.UNION_VERIFY_MEMBER_NAME_LENGTH_ERROR);
			}
		}
		if(CommonUtil.isEmpty(unionVerifyMember.getPhone())){
			throw new ParameterException(UnionVerifyMemberConstant.UNION_VERIFY_MEMBER_PHONE_NULL);
		}else {
			//TODO 正则校验手机号
		}
		EntityWrapper<UnionVerifyMember> memberEntityWrapper = new EntityWrapper<UnionVerifyMember>();
		memberEntityWrapper.eq("del_status",0);
		memberEntityWrapper.eq("phone",unionVerifyMember.getPhone());
		int count = this.selectCount(memberEntityWrapper);
		if(count > 0){
			throw new ParameterException(UnionVerifyMemberConstant.UNION_VERIFY_MEMBER_PHONE_EXITS);
		}
		EntityWrapper<UnionVerifyMember> memberWrapper = new EntityWrapper<UnionVerifyMember>();
		memberWrapper.eq("del_status",0);
		memberWrapper.eq("bus_id",unionVerifyMember.getBusId());
		memberWrapper.eq("member_name",unionVerifyMember.getMemberName());
		count = this.selectCount(memberEntityWrapper);
		if(count > 0){
			throw new ParameterException(UnionVerifyMemberConstant.UNION_VERIFY_MEMBER_NAME_EXITS);
		}
		this.insert(unionVerifyMember);
	}
}
