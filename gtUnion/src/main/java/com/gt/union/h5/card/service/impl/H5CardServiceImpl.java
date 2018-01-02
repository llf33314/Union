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
import com.gt.union.union.member.vo.MemberJoinVO;
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
		List<UnionMember> members = unionMemberService.listValidWriteByBusId(busId);
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
				UnionCardFan fan = unionCardFanService.getValidByPhone(phone);
				if(fan != null){
					//办过卡
					for(UnionMember member : members) {
						UnionMain unionMain = unionMainService.getById(member.getUnionId());
						if (unionMainService.isUnionValid(unionMain)) {
							//是否办理过折扣卡
							UnionCard discountCard = unionCardService.getValidUnexpiredByUnionIdAndFanIdAndType(unionMain.getId(), fan.getId(), CardConstant.TYPE_DISCOUNT);
							if(discountCard == null){
								UnionCardVO discountCardVO = getUnionCardVO(unionMain.getName() + "折扣卡", CardConstant.TYPE_DISCOUNT, unionMain.getId(), null, null);
								discountCardList.add(discountCardVO);
							}

							//是否办理过活动卡
							List<UnionCardActivity> list = unionCardActivityService.listValidByUnionIdAndStatus(unionMain.getId(), ActivityConstant.STATUS_SELLING);
							if(ListUtil.isNotEmpty(list)){
								for(UnionCardActivity activity : list){
									UnionCard card = unionCardService.getValidUnexpiredByUnionIdAndFanIdAndActivityId(unionMain.getId(), fan.getId(), activity.getId());
									if(card == null){
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
		if(CommonUtil.isNotEmpty(color)){
			String[] c = color.split(",");
			unionCardVO.setColor1(c[0]);
			unionCardVO.setColor2(c[1]);
		}
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
				List<UnionCardActivity> list = unionCardActivityService.listValidByUnionIdAndStatus(unionMain.getId(), ActivityConstant.STATUS_SELLING);
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
		final UnionMember unionMember = unionMemberService.getValidWriteByBusIdAndUnionId(busId, unionId);
		if (unionMember == null) {
			throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
		}
		CardDetailVO result = new CardDetailVO();
		result.setIsTransacted(CommonConstant.COMMON_YES);
		result.setUnionId(unionId);
		if(CommonUtil.isEmpty(activityId)){
			//折扣卡
			if(CommonUtil.isNotEmpty(phone)){
				UnionCardFan fan = unionCardFanService.getValidByPhone(phone);
				if(fan != null){
					UnionCard card = unionCardService.getValidUnexpiredByUnionIdAndFanIdAndType(unionId, fan.getId(), CardConstant.TYPE_DISCOUNT);
					if(card != null){
						result.setIsTransacted(CommonConstant.COMMON_NO);
					}
				}
			}
			result.setCardType(CardConstant.TYPE_DISCOUNT);
			result.setCardName(union.getName() + "折扣卡");
			List<UnionMember> members = unionMemberService.listValidWriteByUnionId(unionId);
			List<CardDetailListVO> list = new ArrayList<CardDetailListVO>(members.size());
			if(ListUtil.isNotEmpty(members)){
				for(UnionMember member : members){
					CardDetailListVO listVO = new CardDetailListVO();
					listVO.setUnionMember(member);
					list.add(listVO);
				}
			}
			result.setCardDetailListVO(list);
			result.setUserCount(members.size());
		}else {
			//活动卡
			UnionCardActivity activity = unionCardActivityService.getValidByIdAndUnionId(activityId, unionId);
			if(CommonUtil.isEmpty(activity)){
				throw new BusinessException("找不到该卡信息");
			}
			if(DateTimeKit.isBetween(activity.getSellBeginTime(),activity.getSellEndTime())){
				//有效期 可以卖
				result.setValidityDay(activity.getValidityDay());
				if(CommonUtil.isNotEmpty(phone)){
					UnionCardFan fan = unionCardFanService.getValidByPhone(phone);
					if(fan != null){
						UnionCard card = unionCardService.getValidUnexpiredByUnionIdAndFanIdAndActivityId(unionId, fan.getId(), activityId);
						if(card != null){
							//存在且没过期
							result.setIsTransacted(CommonConstant.COMMON_NO);
							result.setValidityStr(DateTimeKit.format(card.getValidity(), DateTimeKit.DEFAULT_DATE_FORMAT));
							result.setIsOverdue(CommonConstant.COMMON_NO);
						}else{
							//不存在或已过期
							result.setIsOverdue(CommonConstant.COMMON_YES);
						}
					}
				}
			}else {
				throw new BusinessException("该联盟卡不可购买");
			}
			int itemCount = 0;
			List<CardDetailListVO> list = new ArrayList<CardDetailListVO>();
			//已通过审核
			List<UnionMember> members = unionMemberService.listValidWriteByUnionId(unionId);
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
				List<UnionCardProject> projectList = unionCardProjectService.listValidByUnionIdAndActivityIdAndStatus(unionId, activityId, ProjectConstant.STATUS_ACCEPT);
				for(UnionMember member : members){
					CardDetailListVO listVO = new CardDetailListVO();
					if (ListUtil.isNotEmpty(projectList)) {
						for (UnionCardProject project : projectList) {
							if(member.getId().equals(project.getMemberId())){
								List<UnionCardProjectItem> textItemList = unionCardProjectItemService.listValidByProjectId(project.getId());
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
			result.setActivityIllustration(activity.getIllustration());
			result.setCardName(activity.getName());
			result.setCardType(CardConstant.TYPE_ACTIVITY);
			result.setCardPrice(activity.getPrice());
			result.setCardDetailListVO(list);
			if(CommonUtil.isNotEmpty(activity.getColor())){
				String[] c = activity.getColor().split(",");
				result.setColor1(c[0]);
				result.setColor2(c[1]);
			}
			result.setItemCount(itemCount);
		}
		return result;
	}

	@Override
	public MyCardDetailVO myCardDetail(String phone) throws Exception{
		MyCardDetailVO vo = new MyCardDetailVO();
		List cardList = new ArrayList<>();
		if(CommonUtil.isNotEmpty(phone)){
			UnionCardFan fan = unionCardFanService.getValidByPhone(phone);
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
								UnionCardActivity activity = unionCardActivityService.getValidByIdAndUnionId(card.getActivityId(), union.getId());
								detailVO.setCardName(activity.getName());
								if(CommonUtil.isNotEmpty(activity.getColor())){
									String[] c = activity.getColor().split(",");
									detailVO.setColor1(c[0]);
									detailVO.setColor2(c[1]);
								}
								detailVO.setActivityId(activity.getId());
								detailVO.setValidityStr(DateTimeKit.format(card.getValidity(), DateTimeKit.DEFAULT_DATE_FORMAT));
								detailVO.setIsOverdue(DateTimeKit.laterThanNow(card.getValidity()) ? 0 : 1);
								//优惠项目
								List<UnionCardProject> projectList = unionCardProjectService.listValidByUnionIdAndActivityIdAndStatus(union.getId(), activity.getId(), ProjectConstant.STATUS_ACCEPT);
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
							detailVO.setCreatetime(card.getCreateTime());
							cardList.add(detailVO);
						}
					}
					Double integral = unionCardIntegralService.sumValidIntegralByFanId(fan.getId());
					vo.setIntegral(integral);
					Integer consumeCount = unionConsumeService.countValidPayByFanId(fan.getId());
					vo.setConsumeCount(consumeCount);
				}
				vo.setCardNo(fan.getNumber());
				vo.setCardImg(PropertiesUtil.getUnionUrl() + "/h5Card/79B4DE7C/qr/cardNo?cardNo="+fan.getNumber());
			}
		}
		if(ListUtil.isNotEmpty(cardList)){
			Collections.sort(cardList, new Comparator<MyUnionCardDetailVO>() {
				@Override
				public int compare(MyUnionCardDetailVO o1, MyUnionCardDetailVO o2) {
					if(o1.getCardType() > o2.getCardType()){
						return 1;
					}else if(o1.getCardType().equals(o2.getCardType())){
						return -o1.getCreatetime().compareTo(o2.getCreatetime());
					}else {
						return -1;
					}
				}
			});
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
		data.put("phone", 1);
		if(result != null){
			data.put("payUrl", result.getPayUrl());
		}
		return GtJsonResult.instanceSuccessMsg(data).toString();
	}

	@Override
	public Page pageConsumeByPhone(Page page, String phone) throws Exception {
        if(StringUtil.isNotEmpty(phone)){
            UnionCardFan fan = unionCardFanService.getValidByPhone(phone);
            if(fan != null){
                return  unionConsumeService.pageConsumeByFanId(page, fan.getId());
			}
		}
		return page;
	}
}
