package com.gt.union.h5.card.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.Member;
import com.gt.union.api.client.member.MemberService;
import com.gt.union.api.client.sms.SmsService;
import com.gt.union.card.activity.constant.ActivityConstant;
import com.gt.union.card.activity.entity.UnionCardActivity;
import com.gt.union.card.activity.service.IUnionCardActivityService;
import com.gt.union.card.consume.service.IUnionConsumeService;
import com.gt.union.card.main.constant.CardConstant;
import com.gt.union.card.main.entity.UnionCard;
import com.gt.union.card.main.entity.UnionCardFan;
import com.gt.union.card.main.service.IUnionCardApplyService;
import com.gt.union.card.main.service.IUnionCardFanService;
import com.gt.union.card.main.service.IUnionCardIntegralService;
import com.gt.union.card.main.service.IUnionCardService;
import com.gt.union.card.project.constant.ProjectConstant;
import com.gt.union.card.project.entity.UnionCardProject;
import com.gt.union.card.project.entity.UnionCardProjectItem;
import com.gt.union.card.project.service.IUnionCardProjectItemService;
import com.gt.union.card.project.service.IUnionCardProjectService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.SmsCodeConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.*;
import com.gt.union.h5.card.service.IH5CardService;
import com.gt.union.h5.card.vo.*;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.main.service.IUnionMainService;
import com.gt.union.union.main.vo.UnionPayVO;
import com.gt.union.union.member.entity.UnionMember;
import com.gt.union.union.member.service.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author hongjiye
 * @time 2017-12-19 11:38
 **/
@Service
public class H5CardServiceImpl implements IH5CardService {

	@Autowired
	private IUnionMainService unionMainService;

	@Autowired
	private IUnionMemberService unionMemberService;

	@Autowired
	private IUnionCardFanService unionCardFanService;

	@Autowired
	private IUnionCardService unionCardService;

	@Autowired
	private IUnionCardActivityService unionCardActivityService;

	@Autowired
	private IUnionCardProjectItemService unionCardProjectItemService;

	@Autowired
	private IUnionCardProjectService unionCardProjectService;

	@Autowired
	private MemberService memberService;

	@Autowired
	private SmsService smsService;

	@Resource(name = "unionPhoneCardApplyService")
	private IUnionCardApplyService unionCardApplyService;

	@Autowired
	private IUnionConsumeService unionConsumeService;
	
	@Autowired
	private IUnionCardIntegralService unionCardIntegralService;

	@Override
	public IndexVO getIndexVO(String phone, Integer busId) throws Exception {
		if (CommonUtil.isEmpty(busId)) {
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}
		IndexVO indexVO = new IndexVO();
		List<UnionMember> members = unionMemberService.listWriteByBusId(busId);
		List indexCardList = new ArrayList<>();
		if(ListUtil.isNotEmpty(members)){
			//入盟时间倒序
			Collections.sort(members, new Comparator<UnionMember>() {
				@Override
				public int compare(UnionMember o1, UnionMember o2) {
					return o2.getCreateTime().compareTo(o1.getCreateTime());
				}
			});
			List discountCardList = new ArrayList<>();
			List activityCardList = new ArrayList<>();
			if(CommonUtil.isEmpty(phone)){
				//没有手机号
				nonTransacted(members, discountCardList, activityCardList);
			}else {
				//有手机号
				UnionCardFan fan = unionCardFanService.getByPhone(phone);
				if(fan != null){
					//办过卡
					for(UnionMember member : members) {
						UnionMain unionMain = unionMainService.getById(member.getUnionId());
						if (unionMainService.isUnionValid(unionMain)) {
							//是否办理过折扣卡
							UnionCard discountCard = unionCardService.getValidDiscountCardByUnionIdAndFanId(unionMain.getId(), fan.getId());
							if(discountCard == null){
								UnionCardVO discountCardVO = getUnionCardVO(unionMain.getName() + "折扣卡", CardConstant.TYPE_DISCOUNT, unionMain.getId(), null, null);
								discountCardList.add(discountCardVO);
							}

							//是否办理过活动卡
							List<UnionCardActivity> list = unionCardActivityService.listByUnionIdAndStatus(unionMain.getId(), ActivityConstant.STATUS_SELLING);
							if(ListUtil.isNotEmpty(list)){
								for(UnionCardActivity activity : list){
									UnionCard card = unionCardService.getValidActivityCardByUnionIdAndFanIdAndActivityId(unionMain.getId(), fan.getId(), activity.getId());
									if(card == null || !DateTimeKit.laterThanNow(card.getValidity())){
										//没有办理过或已过期
										UnionCardVO activityCardVO = getUnionCardVO(activity.getName(), CardConstant.TYPE_ACTIVITY, unionMain.getId(), activity.getId(), activity.getColor());
										activityCardList.add(activityCardVO);
									}
								}
							}
						}
					}
				}else {
					//没办过卡
					nonTransacted(members, discountCardList, activityCardList);
				}
			}
			indexCardList.addAll(discountCardList);
			indexCardList.addAll(activityCardList);
		}
		indexVO.setCardList(indexCardList);
		return indexVO;
	}

	UnionCardVO getUnionCardVO(String cardName, Integer type, Integer unionId, Integer activityId, String color){
		UnionCardVO unionCardVO = new UnionCardVO();
		unionCardVO.setCardName(cardName);
		unionCardVO.setCardType(type);
		unionCardVO.setUnionId(unionId);
		unionCardVO.setActivityId(activityId);
		unionCardVO.setColor(color);
		return unionCardVO;
	}

	/**
	 * 没有办理卡
	 * @param members
	 * @param discountCardList
	 * @param activityCardList
	 * @throws Exception
	 */
	void nonTransacted(List<UnionMember> members, List discountCardList, List activityCardList) throws Exception{
		for(UnionMember member : members){
			UnionMain unionMain = unionMainService.getById(member.getUnionId());
			if (unionMainService.isUnionValid(unionMain)) {
				//折扣卡
				UnionCardVO discountCardVO = getUnionCardVO(unionMain.getName() + "折扣卡", CardConstant.TYPE_DISCOUNT, unionMain.getId(), null, null);
				discountCardList.add(discountCardVO);

				//活动卡
				List<UnionCardActivity> list = unionCardActivityService.listByUnionIdAndStatus(unionMain.getId(), ActivityConstant.STATUS_SELLING);
				if(ListUtil.isNotEmpty(list)){
					for(UnionCardActivity activity : list){
						UnionCardVO activityCardVO = getUnionCardVO(activity.getName(), CardConstant.TYPE_ACTIVITY, unionMain.getId(), activity.getId(), activity.getColor());
						activityCardList.add(activityCardVO);
					}
				}
			}
		}
	}

	@Override
	public CardDetailVO getCardDetail(String phone, Integer busId, Integer unionId, Integer activityId) throws Exception {
		if (CommonUtil.isEmpty(busId) || CommonUtil.isEmpty(unionId)) {
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}
		// （1）判断union有效性和member写权限
		UnionMain union = unionMainService.getById(unionId);
		if (!unionMainService.isUnionValid(union)) {
			throw new BusinessException(CommonConstant.UNION_INVALID);
		}
		final UnionMember unionMember = unionMemberService.getWriteByBusIdAndUnionId(busId, unionId);
		if (unionMember == null) {
			throw new BusinessException(CommonConstant.UNION_WRITE_REJECT);
		}
		CardDetailVO result = new CardDetailVO();
		result.setIsTransacted(CommonConstant.COMMON_YES);
		result.setUnionId(unionId);
		if(CommonUtil.isEmpty(activityId)){
			//折扣卡
			if(CommonUtil.isNotEmpty(phone)){
				UnionCardFan fan = unionCardFanService.getByPhone(phone);
				if(fan != null){
					UnionCard card = unionCardService.getValidDiscountCardByUnionIdAndFanId(unionId, fan.getId());
					if(card != null){
						result.setIsTransacted(CommonConstant.COMMON_NO);
					}
				}
			}
			result.setCardType(CardConstant.TYPE_DISCOUNT);
			result.setCardName(union.getName() + "折扣卡");
			List<UnionMember> members = unionMemberService.listWriteByUnionId(unionId);
			List<CardDetailListVO> list = new ArrayList<CardDetailListVO>(members.size());
			for(UnionMember member : members){
				CardDetailListVO listVO = new CardDetailListVO();
				listVO.setUnionMember(member);
				list.add(listVO);
			}
			result.setCardDetailListVO(list);
			result.setUserCount(members.size());
		}else {
			//活动卡
			UnionCardActivity activity = unionCardActivityService.getByIdAndUnionId(activityId, unionId);
			if(CommonUtil.isEmpty(activity)){
				throw new BusinessException("找不到该卡信息");
			}
			if(DateTimeKit.isBetween(activity.getSellBeginTime(),activity.getSellEndTime())){
				//有效期
				if(CommonUtil.isNotEmpty(phone)){
					UnionCardFan fan = unionCardFanService.getByPhone(phone);
					if(fan != null){
						UnionCard card = unionCardService.getValidActivityCardByUnionIdAndFanIdAndActivityId(unionId, fan.getId(), activityId);
						if(card != null){
							if(DateTimeKit.laterThanNow(card.getValidity())){
								//没过期
								result.setIsTransacted(CommonConstant.COMMON_NO);
								result.setValidityStr(DateTimeKit.format(card.getValidity(), DateTimeKit.DEFAULT_DATE_FORMAT));
								result.setIsOverdue(CommonConstant.COMMON_NO);
							}else {
								//已过期
								result.setIsOverdue(CommonConstant.COMMON_YES);
							}
						}
					}
				}
			}else {
				result.setIsTransacted(CommonConstant.COMMON_NO);
			}
			int itemCount = 0;
			//已通过审核
			List<UnionMember> members = unionMemberService.listWriteByUnionId(unionId);
			if(ListUtil.isNotEmpty(members)){
				//本商家盟员在前面，其他按时间倒序
				Collections.sort(members, new Comparator<UnionMember>() {
					@Override
					public int compare(UnionMember o1, UnionMember o2) {
						if (o1.getId().equals(unionMember.getId())) {
							return -1;
						}
						if (o2.getId().equals(unionMember.getId())) {
							return 1;
						}
						return o2.getCreateTime().compareTo(o1.getCreateTime());
					}
				});
				List<UnionCardProject> projectList = unionCardProjectService.listByUnionIdAndActivityIdAndStatus(unionId, activityId, ProjectConstant.STATUS_ACCEPT);
				List<CardDetailListVO> list = new ArrayList<CardDetailListVO>(members.size());
				for(UnionMember member : members){
					CardDetailListVO listVO = new CardDetailListVO();
					if (ListUtil.isNotEmpty(projectList)) {
						for (UnionCardProject project : projectList) {
							if(member.getId().equals(project.getMemberId())){
								List<UnionCardProjectItem> textItemList = unionCardProjectItemService.listByProjectId(project.getId());
								if (ListUtil.isNotEmpty(textItemList)) {
									listVO.setUnionCardProjectItems(textItemList);
									itemCount += textItemList.size();
								}
								break;
							}

						}
					}
					listVO.setUnionMember(member);
					list.add(listVO);
				}
			}
			result.setCardName(activity.getName());
			result.setCardType(CardConstant.TYPE_ACTIVITY);
			result.setCardPrice(activity.getPrice());
			result.setColor(activity.getColor());
			result.setItemCount(itemCount);
		}
		return result;
	}

	@Override
	public MyCardDetailVO myCardDetail(String phone) throws Exception{
		MyCardDetailVO vo = new MyCardDetailVO();
		List cardList = new ArrayList<>();
		if(CommonUtil.isNotEmpty(phone)){
			UnionCardFan fan = unionCardFanService.getByPhone(phone);
			if(fan != null){
				List<UnionCard> list = unionCardService.listValidByFanId(fan.getId());
				if(ListUtil.isNotEmpty(list)){
					for(UnionCard card : list){
						UnionMain union = unionMainService.getById(card.getUnionId());
						if (unionMainService.isUnionValid(union)) {
							MyUnionCardDetailVO detailVO = new MyUnionCardDetailVO();
							detailVO.setCardType(card.getType());
							detailVO.setUnionId(union.getId());
							if(card.getType().equals(CardConstant.TYPE_DISCOUNT)){
								//折扣卡
								detailVO.setCardName(union.getName() + "折扣卡");
							}else if(card.getType().equals(CardConstant.TYPE_ACTIVITY)){
								//活动卡
								int itemCount = 0;
								UnionCardActivity activity = unionCardActivityService.getByIdAndUnionId(card.getActivityId(), union.getId());
								detailVO.setCardName(activity.getName());
								detailVO.setColor(activity.getColor());
								detailVO.setActivityId(activity.getId());
								detailVO.setValidityStr(DateTimeKit.format(card.getValidity(), DateTimeKit.DEFAULT_DATE_FORMAT));
								//优惠项目
								List<UnionCardProject> projectList = unionCardProjectService.listByUnionIdAndActivityIdAndStatus(union.getId(), activity.getId(), ProjectConstant.STATUS_ACCEPT);
								if (ListUtil.isNotEmpty(projectList)) {
									for (UnionCardProject project : projectList) {
										List<UnionCardProjectItem> textItemList = unionCardProjectItemService.listByProjectId(project.getId());
										if (ListUtil.isNotEmpty(textItemList)) {
											itemCount += textItemList.size();
										}
									}
								}
								detailVO.setItemCount(itemCount);
							}
							cardList.add(detailVO);
						}
					}
					Double integral = unionCardIntegralService.sumIntegralByFanId(fan.getId());
					vo.setIntegral(integral);
					Integer consumeCount = unionConsumeService.countPayByFanId(fan.getId());
					vo.setConsumeCount(consumeCount);
				}
			}
		}
		vo.setCardList(cardList);
		return vo;
	}

	@Override
	public void bindCardPhone(Member member, Integer busId, String phone, String code) throws Exception{
		if(busId == null || StringUtil.isEmpty(phone) || StringUtil.isEmpty(code)){
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}
		if(!smsService.checkPhoneCode(SmsCodeConstant.UNION_CARD_PHONE_BIND_TYPE, code, phone)){
			throw new ParamException(CommonConstant.CODE_ERROR_MSG);
		}
		if(!memberService.bindMemberPhone(busId, member.getId(), phone)){
			throw new BusinessException("绑定失败");
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public String cardTransaction(String phone, Integer busId, Integer activityId, Integer unionId) throws Exception{
		if(busId == null || unionId == null){
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}
		Map<String,Object> data = new HashMap<String,Object>();
		if(StringUtil.isEmpty(phone)){
			//没有手机号
			data.put("phone", 0);
			return GtJsonResult.instanceSuccessMsg(data).toString();
		}
		List list = new ArrayList<>();
		if(CommonUtil.isNotEmpty(activityId)){
			list.add(activityId);
		}
		UnionCardFan fan = unionCardFanService.getOrSaveByPhone(phone);
		UnionPayVO result = unionCardService.saveApplyByBusIdAndUnionIdAndFanId(busId, unionId, fan.getId(), list, unionCardApplyService);
		if(result != null){
			data.put("payUrl", result.getPayUrl());
			data.put("phone", 1);
		}
		return GtJsonResult.instanceSuccessMsg(data).toString();
	}

	@Override
	public List<MyCardConsumeVO> listConsumeByPhone(String phone, Page page) throws Exception {
		List<MyCardConsumeVO> list = null;
		if(StringUtil.isNotEmpty(phone)){
			UnionCardFan fan = unionCardFanService.getByPhone(phone);
			if(fan != null){
				list = unionConsumeService.listConsumeByFanId(fan.getId(), page);
			}
		}
		return list == null ? new ArrayList<MyCardConsumeVO>() : list;
	}
}
