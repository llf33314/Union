package com.gt.union.service.brokerage.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
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
			throw new ParameterException("姓名内容不能为空，请重新输入");
		}else {
			if(StringUtil.getStringLength(unionVerifyMember.getMemberName()) > 5){
				throw new ParameterException("姓名内容长度不可超过5个字，请重新输入");
			}
		}
		if(CommonUtil.isEmpty(unionVerifyMember.getPhone())){
			throw new ParameterException("手机号码内容不能为空，请重新输入");
		}else {
			//TODO 正则校验手机号
		}
		EntityWrapper<UnionVerifyMember> memberEntityWrapper = new EntityWrapper<UnionVerifyMember>();
		memberEntityWrapper.eq("bus_id",unionVerifyMember.getBusId());
		memberEntityWrapper.eq("del_status",0);
		//member.or()
		UnionVerifyMember member = this.selectOne(memberEntityWrapper);
		if(CommonUtil.isEmpty(member)){
			this.insert(unionVerifyMember);
		}else{
			if(member.getMemberName().equals(unionVerifyMember.getMemberName())){
				throw new ParameterException("您输入的姓名已存在，请重新输入");
			}
			if(member.getPhone().equals(unionVerifyMember.getPhone())){
				throw new ParameterException("您输入的手机号码已存在，请重新输入");
			}
		}

	}
}
