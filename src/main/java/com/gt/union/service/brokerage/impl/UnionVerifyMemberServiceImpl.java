package com.gt.union.service.brokerage.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.constant.brokerage.UnionVerifyMemberConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.entity.brokerage.UnionBrokerageWithdrawalsRecord;
import com.gt.union.entity.brokerage.UnionVerifyMember;
import com.gt.union.mapper.brokerage.UnionVerifyMemberMapper;
import com.gt.union.service.brokerage.IUnionBrokerageWithdrawalsRecordService;
import com.gt.union.service.brokerage.IUnionVerifyMemberService;
import com.gt.union.service.business.IUnionBrokeragePayRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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
	private static final String DELETE_ID = "UnionVerifyMemberServiceImpl.deleteById()";
	private static final String SAVE = "UnionVerifyMemberServiceImpl.save()";
	private static final String LIST = "UnionVerifyMemberServiceImpl.list()";

	@Autowired
	private IUnionBrokerageWithdrawalsRecordService unionBrokerageWithdrawalsRecordService;

	@Autowired
	private IUnionBrokeragePayRecordService unionBrokeragePayRecordService;

	@Autowired
	private RedisCacheUtil redisCacheUtil;


	@Override
	public void deleteById(Integer id) throws Exception{
		if(id == null){
			throw new ParamException(DELETE_ID, "参数id为空", ExceptionConstant.PARAM_ERROR);
		}
		UnionVerifyMember unionVerifyMember = new UnionVerifyMember();
		unionVerifyMember.setId(id);
		unionVerifyMember.setDelStatus(1);
		this.updateById(unionVerifyMember);
	}

	@Override
	public void save(UnionVerifyMember unionVerifyMember) throws Exception {
		EntityWrapper<UnionVerifyMember> memberEntityWrapper = new EntityWrapper<UnionVerifyMember>();
		memberEntityWrapper.eq("del_status",0);
		memberEntityWrapper.eq("phone",unionVerifyMember.getPhone());
		int count = this.selectCount(memberEntityWrapper);
		if(count > 0){
			throw new ParamException(SAVE, "", "您输入的手机号码已存在，请重新输入");
		}

		if (StringUtil.getStringLength(unionVerifyMember.getMemberName()) > 10) {
		    throw new BusinessException(SAVE, "", "姓名字数不能超过10个字");
        }
		EntityWrapper<UnionVerifyMember> memberWrapper = new EntityWrapper<UnionVerifyMember>();
		memberWrapper.eq("del_status",0);
		memberWrapper.eq("bus_id",unionVerifyMember.getBusId());
		memberWrapper.eq("member_name",unionVerifyMember.getMemberName());
		count = this.selectCount(memberEntityWrapper);
		if(count > 0){
			throw new ParamException(SAVE, "", "您输入的姓名已存在，请重新输入");
		}
		Object obj = redisCacheUtil.get("verifyMember:"+unionVerifyMember.getPhone());
		if(CommonUtil.isEmpty(obj)){
			throw new BusinessException(SAVE, "", "验证码已失效");
		}
		if(!unionVerifyMember.getCode().equals(obj)){
			throw new BusinessException(SAVE, "", "验证码有误");
		}
		UnionVerifyMember member = new UnionVerifyMember();
		member.setDelStatus(0);
		member.setCreatetime(new Date());
		member.setBusId(unionVerifyMember.getBusId());
		member.setMemberName(unionVerifyMember.getMemberName());
		member.setPhone(unionVerifyMember.getPhone());
		this.insert(member);
		redisCacheUtil.remove("verifyMember:"+unionVerifyMember.getPhone());
	}

	@Override
	public Page list(Page<UnionVerifyMember> page, Integer busId) throws Exception{
		if(busId == null){
			throw new ParamException(LIST, "参数busId为空", ExceptionConstant.PARAM_ERROR);
		}
		EntityWrapper entityWrapper = new EntityWrapper<>();
		entityWrapper.eq("bus_id",busId);
		entityWrapper.eq("del_status", UnionVerifyMemberConstant.DEL_STATUS_NO);
		return this.selectPage(page,entityWrapper);
	}
}
