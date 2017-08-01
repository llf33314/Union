package com.gt.union.service.business.impl;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.business.UnionBusinessRecommendConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParameterException;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.entity.business.UnionBusinessRecommend;
import com.gt.union.entity.business.UnionBusinessRecommendInfo;
import com.gt.union.mapper.business.UnionBusinessRecommendMapper;
import com.gt.union.service.business.IUnionBusinessRecommendInfoService;
import com.gt.union.service.business.IUnionBusinessRecommendService;
import com.gt.union.vo.business.UnionBusinessRecommendFormVo;
import com.gt.union.vo.business.UnionBusinessRecommendVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * <p>
 * 联盟商家商机推荐 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
@Service
public class UnionBusinessRecommendServiceImpl extends ServiceImpl<UnionBusinessRecommendMapper, UnionBusinessRecommend> implements IUnionBusinessRecommendService {


	@Autowired
	private IUnionBusinessRecommendInfoService unionBusinessRecommendInfoService;

	@Override
	public Page selectUnionBusinessRecommendList(Page page,final UnionBusinessRecommendVo vo) throws Exception{
		if (vo.getUnionId() == null) {
			throw new ParameterException("参数错误");
		}
		Wrapper wrapper = new Wrapper() {
			@Override
			public String getSqlSegment() {
				StringBuilder sbSqlSegment = new StringBuilder();
				sbSqlSegment.append(" t1 LEFT JOIN t_union_business_recommend_info t2 ON t1.id = t2.t_union_business_recommend_info")
						.append(" WHERE")
						.append(" t1.to_bus_id = ").append(vo.getBusId())
						.append(" AND t2.union_id = ").append(vo.getUnionId())
						.append(" AND t1.del_status = ").append(0);
				if (StringUtil.isNotEmpty(vo.getPhone())) {
					sbSqlSegment.append(" AND t1.phone LIKE '%").append(vo.getPhone().trim()).append("%' ");
				}
				sbSqlSegment.append(" ORDER BY t1.id DESC ");
				return sbSqlSegment.toString();
			};

		};
		StringBuilder sbSqlSelect = new StringBuilder("");
		sbSqlSelect.append(" t1.id, t1.tcardNo, t1.phone, t1.integral, DATE_FORMAT(t1.createtime, '%Y-%m-%d %T') createtime, t2.card_type, DATE_FORMAT(t2.card_term_time, '%Y-%m-%d %T') cardTermTime");
		wrapper.setSqlSelect(sbSqlSelect.toString());
		return this.selectMapsPage(page, wrapper);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void updateVerifyRecommend(UnionBusinessRecommend recommend) throws Exception{
		//TODO 判断推荐的商家是否有效
		if(CommonUtil.isEmpty(recommend.getId())){
			throw new ParameterException("参数错误");
		}
		if(CommonUtil.isEmpty(recommend.getIsAcceptance()) || !(recommend.getIsAcceptance() == 1 || recommend.getIsAcceptance() == 2 )){
			throw new ParameterException("参数错误");
		}
		UnionBusinessRecommend businessRecommend = this.selectById(recommend.getId());
		if(CommonUtil.isEmpty(businessRecommend) || businessRecommend.getDelStatus() == 1){
			throw new BusinessException("该商机不存在，请刷新后重试");
		}
		if(businessRecommend.getIsAcceptance() != 0){
			throw new BusinessException("该商机已处理，请刷新后重试");
		}
		this.updateById(recommend);
	}

	@Override
	public void saveUnionBusinessRecommend(UnionBusinessRecommendFormVo vo) throws Exception{
		//TODO 判断推荐的商家是否有效
		if(CommonUtil.isEmpty(vo.getUnionId())){
			throw new ParameterException("请选择联盟");
		}
		if(CommonUtil.isEmpty(vo.getToMemberId())){
			throw new ParameterException("请选择推荐的商家");
		}
		if(CommonUtil.isEmpty(vo.getUnionBusinessRecommendInfo().getUserName())){
			throw new ParameterException("意向客户姓名不能为空，请重新输入");
		}
		if(StringUtil.getStringLength(vo.getUnionBusinessRecommendInfo().getUserName()) > 5){
			throw new ParameterException("意向客户姓名不可超过5个字，请重新输入");
		}
		if(CommonUtil.isEmpty(vo.getUnionBusinessRecommendInfo().getUserPhone())){
			throw new ParameterException("意向客户电话不能为空，请重新输入");
		}
		if(StringUtil.getStringLength(vo.getUnionBusinessRecommendInfo().getBusinessMsg()) > 20){
			throw new ParameterException("业务备注不可超过20个字，请重新输入");
		}
		//TODO 判断商机推荐的手机号是否正确   您输入的意向客户电话有误，请重新输入
		UnionBusinessRecommend recommend = new UnionBusinessRecommend();
		recommend.setCreatetime(new Date());
		recommend.setDelStatus(0);
		recommend.setIsAcceptance(0);
		recommend.setIsConfirm(0);
		recommend.setFromBusId(vo.getBusId());
		recommend.setRecommendType(2);
		recommend.setUnionId(vo.getUnionId());
		recommend.setToBusId(1);
		recommend.setFromMemberId(2);
		recommend.setToMemberId(vo.getToMemberId());
		this.insert(recommend);
		UnionBusinessRecommendInfo info = new UnionBusinessRecommendInfo();
		info.setBusinessMsg(vo.getUnionBusinessRecommendInfo().getBusinessMsg());
		info.setRecommendId(recommend.getId());
		info.setUserName(vo.getUnionBusinessRecommendInfo().getUserName());
		info.setUserPhone(vo.getUnionBusinessRecommendInfo().getUserPhone());
		unionBusinessRecommendInfoService.insert(info);
	}
}
