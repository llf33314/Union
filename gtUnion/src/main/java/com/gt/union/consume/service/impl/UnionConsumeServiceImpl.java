package com.gt.union.consume.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.api.bean.session.WxPublicUsers;
import com.gt.union.api.client.dict.IDictService;
import com.gt.union.api.client.shop.ShopService;
import com.gt.union.api.client.user.IBusUserService;
import com.gt.union.api.entity.param.UnionConsumeParam;
import com.gt.union.api.entity.result.UnionConsumeResult;
import com.gt.union.api.entity.result.UnionRefundResult;
import com.gt.union.card.constant.CardConstant;
import com.gt.union.card.entity.UnionCard;
import com.gt.union.card.entity.UnionCardIntegral;
import com.gt.union.card.entity.UnionCardRoot;
import com.gt.union.card.service.IUnionCardIntegralService;
import com.gt.union.card.service.IUnionCardRootService;
import com.gt.union.card.service.IUnionCardService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.*;
import com.gt.union.consume.constant.ConsumeConstant;
import com.gt.union.consume.entity.UnionConsume;
import com.gt.union.consume.entity.UnionConsumeItem;
import com.gt.union.consume.mapper.UnionConsumeMapper;
import com.gt.union.consume.service.IUnionConsumeItemService;
import com.gt.union.consume.service.IUnionConsumeService;
import com.gt.union.consume.vo.UnionConsumeParamVO;
import com.gt.union.consume.vo.UnionConsumeVO;
import com.gt.union.main.entity.UnionMain;
import com.gt.union.main.service.IUnionMainService;
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.service.IUnionMemberService;
import com.gt.union.preferential.entity.UnionPreferentialItem;
import com.gt.util.entity.result.shop.WsWxShopInfoExtend;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.*;

/**
 * <p>
 * 消费 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@Service
public class UnionConsumeServiceImpl extends ServiceImpl<UnionConsumeMapper, UnionConsume> implements IUnionConsumeService {

    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private IDictService dictService;

	@Autowired
	private UnionConsumeMapper unionConsumeMapper;

	@Autowired
	private IUnionCardRootService unionCardRootService;

	@Autowired
	private IUnionCardService unionCardService;

	@Autowired
	private ShopService shopService;

	@Autowired
	private IBusUserService busUserService;

	@Autowired
	private RedisCacheUtil redisCacheUtil;

	@Autowired
	private IUnionConsumeItemService unionConsumeItemService;

	@Autowired
	private IUnionCardIntegralService unionCardIntegralService;

	@Transactional(rollbackFor = Exception.class)
	@Override
	public UnionConsumeResult consumeByUnionCard(UnionConsumeParam unionConsumeParam) throws Exception{
		if(unionConsumeParam.getUnionCardId() == null){
			throw new ParamException("unionCardId为空");
		}
		if(unionConsumeParam.getBusId() == null){
			throw new ParamException("busId为空");
		}
		if(unionConsumeParam.getUnionId() == null){
			throw new ParamException("unionId为空");
		}
		if(unionConsumeParam.getModel() == null){
			throw new ParamException("model为空");
		}
		if(unionConsumeParam.getOrderNo() == null){
			throw new ParamException("orderNo为空");
		}
		if(unionConsumeParam.getPayMoney() == null){
			throw new ParamException("payMoney为空");
		}
		if(unionConsumeParam.getTotalMoney() == null){
			throw new ParamException("totalMoney为空");
		}
		if(unionConsumeParam.getOrderType() == null){
			throw new ParamException("orderType为空");
		}
		UnionConsumeResult result = new UnionConsumeResult();
		UnionCard card = unionCardService.getById(unionConsumeParam.getUnionCardId());
		if(card == null){
			result.setMessage("联盟卡不存在");
			result.setSuccess(false);
			return result;
		}
		if(!DateTimeKit.laterThanNow(card.getValidity())){
			result.setMessage("联盟卡过期");
			result.setSuccess(false);
			return result;
		}
		UnionMember unionMember = unionMemberService.getByBusIdAndUnionId(unionConsumeParam.getBusId(), unionConsumeParam.getUnionId());

        UnionConsume unionConsume = new UnionConsume();
        unionConsume.setCreatetime(new Date());
        unionConsume.setDelStatus(CommonConstant.DEL_STATUS_NO);
        unionConsume.setModel(unionConsumeParam.getModel());//行业模型
        unionConsume.setModelDesc(unionConsumeParam.getModelDesc());//行业描述
        unionConsume.setOrderNo(unionConsumeParam.getOrderNo());//订单号
        unionConsume.setPayType(unionConsumeParam.getOrderType());//支付类型 (0：现金 1：微信 2：支付宝)
        unionConsume.setType(1);//线上支付
        unionConsume.setStatus(unionConsumeParam.getStatus());//支付状态
        unionConsume.setConsumeMoney(unionConsumeParam.getTotalMoney());//联盟折扣打折前价格
        unionConsume.setPayMoney(unionConsumeParam.getPayMoney());//折后支付价格
        unionConsume.setCardId(unionConsumeParam.getUnionCardId());//联盟卡id
        unionConsume.setIsIntegral(0);//是否赠送积分 0未赠送 1赠送
        unionConsume.setMemberId(unionMember.getId());
        this.insert(unionConsume);
        //线上积分赠送积分
        if (CommonUtil.isEmpty(unionConsumeParam.getGiveIntegralNow()) || unionConsumeParam.getGiveIntegralNow()) {//立即赠送
            UnionMain main = unionMainService.getById(unionConsumeParam.getUnionId());
            if (main.getIsIntegral() != null && main.getIsIntegral() == 1) {//开启积分
                double integral = dictService.getGiveIntegral();
                double getIntegral = BigDecimalUtil.multiply(unionConsume.getPayMoney(), integral, 2).doubleValue();//获得的积分
                UnionCardIntegral unionCardIntegral = new UnionCardIntegral();
                unionCardIntegral.setCardId(unionConsumeParam.getUnionCardId());
                unionCardIntegral.setCreatetime(new Date());
                unionCardIntegral.setDelStatus(CommonConstant.DEL_STATUS_NO);
                unionCardIntegral.setIntegral(getIntegral);
                unionCardIntegral.setStatus(CardConstant.INTEGRAL_STATUS_IN);
                unionCardIntegral.setType(CardConstant.INTEGRAL_TYPE_GIVE);
                unionCardIntegralService.insert(unionCardIntegral);

				UnionCardRoot root = unionCardRootService.getById(card.getRootId());


				UnionCardRoot unionCardRoot = new UnionCardRoot();
				unionCardRoot.setId(root.getId());
				unionCardRoot.setIntegral(CommonUtil.isEmpty(root.getIntegral()) ? getIntegral : BigDecimalUtil.add(root.getIntegral(),getIntegral).doubleValue());
				unionCardRootService.updateById(unionCardRoot);
            }
        }
        result.setMessage("核销成功");
        result.setSuccess(true);
        return result;
    }

    @Override
    public UnionRefundResult unionRefund(String orderNo, Integer model) throws Exception {
        UnionConsume unionConsume = this.getByOrderNoAndModel(orderNo, model);
        UnionRefundResult result = new UnionRefundResult();
        if (unionConsume == null) {
			result.setMessage("订单不存在");
			result.setSuccess(false);
			return result;
        }
        if (unionConsume.getStatus() == ConsumeConstant.PAY_STATUS_NON) {
			result.setMessage("未支付");
			result.setSuccess(false);
        } else if (unionConsume.getStatus() == ConsumeConstant.PAY_STATUS_YES) {//已支付
            UnionConsume consume = new UnionConsume();
            consume.setId(unionConsume.getId());
            consume.setStatus(2);
            this.updateById(consume);
			result.setMessage("退款成功");
            result.setSuccess(true);
        } else if (unionConsume.getStatus() == ConsumeConstant.PAY_STATUS_REFUND) {//已退款
			result.setMessage("已退款");
			result.setSuccess(false);
        }
        return result;
    }

    @Override
    public UnionConsume getByOrderNoAndModel(String orderNo, Integer model) {
        EntityWrapper wrapper = new EntityWrapper<>();
        wrapper.eq("order_no", orderNo);
        wrapper.eq("model", model);
        wrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);
        return this.selectOne(wrapper);
    }

	@Override
	public Page listMy(Page page, Integer unionId, Integer busId, Integer memberId, String cardNo, String phone, String beginTime, String endTime) throws Exception{
		if(page == null || busId == null){
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}
		page.setSearchCount(false);
		Integer count = unionConsumeMapper.listMyCount(unionId, busId, memberId, cardNo, phone, beginTime, endTime);
		page.setTotal(count);
		List<UnionConsumeVO> list = unionConsumeMapper.listMy(page, unionId, busId, memberId, cardNo, phone, beginTime, endTime);
		List<Integer> shopIds = new ArrayList<Integer>();
		for(UnionConsumeVO vo : list){
			if(CommonUtil.isNotEmpty(vo.getShopId())){
				shopIds.add(vo.getShopId());
			}
		}
		List<WsWxShopInfoExtend> shops = shopService.listByIds(shopIds);
		if(ListUtil.isNotEmpty(shops)){
			for(UnionConsumeVO vo : list){
				if(CommonUtil.isNotEmpty(vo.getShopId())){
					for(WsWxShopInfoExtend info : shops){
						if(info.getId().equals(vo.getShopId())){
							vo.setShopName(info.getBusinessName());
							break;
						}
					}
				}
			}
		}
		page.setRecords(list);
		return page;
	}

	@Override
	public Page listOther(Page page, Integer unionId, Integer busId, Integer memberId, String cardNo, String phone, String beginTime, String endTime) throws Exception{
		if(page == null || busId == null){
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}
		page.setSearchCount(false);
		Integer count = unionConsumeMapper.listOtherCount(unionId, busId, memberId, cardNo, phone, beginTime, endTime);
		page.setTotal(count);
		List<UnionConsumeVO> list = unionConsumeMapper.listOther(page, unionId, busId, memberId, cardNo, phone, beginTime, endTime);
		List<Integer> shopIds = new ArrayList<Integer>();
		for(UnionConsumeVO vo : list){
			if(CommonUtil.isNotEmpty(vo.getShopId())){
				shopIds.add(vo.getShopId());
			}
		}
		List<WsWxShopInfoExtend> shops = shopService.listByIds(shopIds);
		if(ListUtil.isNotEmpty(shops)){
			for(UnionConsumeVO vo : list){
				if(CommonUtil.isNotEmpty(vo.getShopId())){
					for(WsWxShopInfoExtend info : shops){
						if(info.getId().equals(vo.getShopId())){
							vo.setShopName(info.getBusinessName());
							break;
						}
					}
				}
			}
		}
		page.setRecords(list);
		return page;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void consumeByCard(Integer busId, UnionConsumeParamVO vo)  throws Exception{
		if(vo == null || busId == null){
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}
		String orderNo = ConsumeConstant.ORDER_PREFIX + System.currentTimeMillis();
		UnionCard card = unionCardService.getById(vo.getCardId());
		if(card == null){
			throw new BusinessException("联盟卡不存在");
		}
		if(!DateTimeKit.laterThanNow(card.getValidity())){
			throw new BusinessException("联盟卡已过期");
		}
		consumeSuccess(vo, orderNo);
	}

	/**
	 * 消费支付成功
	 * @param vo
	 * @param orderNo
	 */
	private void consumeSuccess( UnionConsumeParamVO vo, String orderNo){
		//核销优惠项目或者现金支付时调用该接口
		UnionConsume consume = new UnionConsume();
		consume.setStatus(ConsumeConstant.PAY_STATUS_YES);
		consume.setDelStatus(CommonConstant.DEL_STATUS_NO);
		consume.setMemberId(vo.getMemberId());
		consume.setStatus(ConsumeConstant.PAY_STATUS_YES);
		consume.setCreatetime(new Date());
		consume.setCardId(vo.getCardId());
		consume.setConsumeMoney(vo.getConsumeMoney());
		consume.setPayMoney(vo.getPayMoney());
		consume.setShopId(vo.getShopId());
		consume.setPayType(vo.getPayType() == 0 ? 1 : vo.getPayType() == 1 ? 2 : 0);
		consume.setType(ConsumeConstant.CONSUME_TYPE_OFFLINE);
		consume.setModel(ConsumeConstant.MODEL_TYPE_DEFAULT);
		consume.setModelDesc(ConsumeConstant.MODEL_DESC_DEFAULT);
		consume.setOrderNo(orderNo);
		this.insert(consume);

		if(ListUtil.isNotEmpty(vo.getItems())){
			List<Integer> items = vo.getItems();
			List<UnionConsumeItem> consumeItems = new ArrayList<UnionConsumeItem>();
			for(Integer itemId : items){
				UnionConsumeItem item = new UnionConsumeItem();
				item.setConsumeId(consume.getId());
				item.setCreatetime(new Date());
				item.setPreferentialItemId(itemId);
				item.setDelStatus(CommonConstant.DEL_STATUS_NO);
				consumeItems.add(item);
			}
			unionConsumeItemService.insertBatch(consumeItems);
		}

		if(vo.isUseIntegral()){//是否使用了积分
			UnionCard card = unionCardService.getById(vo.getCardId());
			UnionCardRoot root = unionCardRootService.getById(card.getRootId());
			Double giveIntegral = dictService.getGiveIntegral();

			//收入
			UnionCardIntegral incomeIntegral = new UnionCardIntegral();
			incomeIntegral.setCardId(card.getId());
			incomeIntegral.setDelStatus(CommonConstant.DEL_STATUS_NO);
			incomeIntegral.setCreatetime(new Date());
			incomeIntegral.setType(CardConstant.CARD_INTEGRAL_TYPE_GIVE);
			incomeIntegral.setStatus(CardConstant.CARD_INTEGRAL_STATUS_INCOME);
			Double integral = BigDecimalUtil.multiply(vo.getPayMoney(),giveIntegral).doubleValue();//赠送的积分
			incomeIntegral.setIntegral(integral);
			unionCardIntegralService.insert(incomeIntegral);
			//支出
			UnionCardIntegral outcomeIntegral = new UnionCardIntegral();
			outcomeIntegral.setCardId(card.getId());
			outcomeIntegral.setDelStatus(CommonConstant.DEL_STATUS_NO);
			outcomeIntegral.setCreatetime(new Date());
			outcomeIntegral.setType(CardConstant.CARD_INTEGRAL_TYPE_GIVE);
			outcomeIntegral.setStatus(CardConstant.CARD_INTEGRAL_STATUS_INCOME);
			outcomeIntegral.setIntegral(vo.getConsumeIntegral());
			unionCardIntegralService.insert(outcomeIntegral);

			Double subIntegral = BigDecimalUtil.subtract(integral,vo.getConsumeIntegral()).doubleValue();//赠送的积分-消费的积分
			UnionCardRoot cardRoot = new UnionCardRoot();
			cardRoot.setId(root.getId());
			cardRoot.setIntegral(root.getIntegral() == null ? (0 + subIntegral) : (root.getIntegral() + subIntegral));
			unionCardRootService.updateById(cardRoot);
		}

	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void payConsumeSuccess(String orderNo, String only, Integer payType) throws Exception{
		//解密参数
		String paramKey = RedisKeyUtil.getConsumePayParamKey(only);
		String obj = redisCacheUtil.get(paramKey);
		Map<String, Object> result = JSONObject.parseObject(obj, Map.class);
		UnionConsumeParamVO vo = JSONObject.parseObject(result.get("unionConsumeParamVO").toString(),UnionConsumeParamVO.class);
		String statusKey = RedisKeyUtil.getConsumePayStatusKey(only);
		vo.setPayType(payType);
		consumeSuccess(vo, orderNo);

		redisCacheUtil.remove(paramKey);
		redisCacheUtil.set(statusKey, ConfigConstant.USER_ORDER_STATUS_003, 60L);//支付成功
	}

	@Override
	public List<UnionConsumeVO> listMyByUnionId(Integer unionId, Integer busId, Integer memberId, String cardNo, String phone, String beginTime, String endTime) throws Exception{
		if(busId == null){
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}
		List<UnionConsumeVO> list = unionConsumeMapper.listMyByUnionId(unionId, busId, memberId, cardNo, phone, beginTime, endTime);
		return list;
	}


	@Override
	public List<UnionConsumeVO> listOtherByUnionId(Integer unionId, Integer busId, Integer memberId, String cardNo, String phone, String beginTime, String endTime) {
		List<UnionConsumeVO> list = unionConsumeMapper.listOtherByUnionId(unionId, busId, memberId, cardNo, phone, beginTime, endTime);
		return list;
	}



	@Override
	public Map<String, Object> payConsumeQRCode(Integer busId, UnionConsumeParamVO vo) throws Exception{
		UnionCard card = unionCardService.getById(vo.getCardId());
		if(card == null){
			throw new BusinessException("联盟卡不存在");
		}
		if(!DateTimeKit.laterThanNow(card.getValidity())){
			throw new BusinessException("联盟卡已过期");
		}
		Map<String, Object> data = new HashMap<String, Object>();
		String orderNo = ConsumeConstant.ORDER_PREFIX + System.currentTimeMillis();
		String only = DateTimeKit.getDateTime(new Date(), DateTimeKit.yyyyMMddHHmmss);
		data.put("totalFee", vo.getConsumeMoney());
		data.put("busId", PropertiesUtil.getDuofenBusId());
		data.put("sourceType", 1);//是否墨盒支付
		data.put("payWay", 0);//系统判断支付方式
		data.put("isreturn", 0);//0：不需要同步跳转
		data.put("model", ConfigConstant.PAY_MODEL);
		WxPublicUsers publicUser = busUserService.getWxPublicUserByBusId(PropertiesUtil.getDuofenBusId());
		data.put("notifyUrl", PropertiesUtil.getUnionUrl() + "/unionConsume/79B4DE7C/paymentSuccess/" + only);
		data.put("orderNum", orderNo);//订单号
		data.put("payBusId", busId);//支付的商家id
		data.put("isSendMessage", 0);//不推送
		data.put("appid", publicUser.getAppid());//appid
		data.put("desc", "联盟消费核销");
		data.put("appidType", 0);//公众号
		data.put("only", only);

		data.put("unionConsumeParamVO", vo);
		String statusKey = RedisKeyUtil.getConsumePayStatusKey(only);
		String paramKey = RedisKeyUtil.getConsumePayParamKey(only);
		redisCacheUtil.set(paramKey, data, 360L);//5分钟
		redisCacheUtil.set(statusKey, ConfigConstant.USER_ORDER_STATUS_001,300L);
		return data;
	}

	@Override
	public HSSFWorkbook exportConsumeFromDetail(String[] titles, String[] contentName, List<UnionConsumeVO> list) {
		try{
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFCellStyle styleCenter = wb.createCellStyle();
			HSSFSheet sheet = createHSSFSheet(titles, wb, styleCenter);
			sheet.setColumnWidth(5, 100 * 150);
			Class<UnionConsumeVO> clazz = UnionConsumeVO.class;
			Field field1 = clazz.getDeclaredField("memberName");
			Field field2 = clazz.getDeclaredField("items");
			Field field3 = clazz.getDeclaredField("cardNo");
			Field field4 = clazz.getDeclaredField("phone");
			Field field5 = clazz.getDeclaredField("consumeMoney");
			Field field6 = clazz.getDeclaredField("payMoney");
			Field field7 = clazz.getDeclaredField("createtime");
			for (int i=0;i<list.size();i++) {
				UnionConsumeVO item = list.get(i);
				HSSFRow row = sheet.createRow(i + 1);
				for(int j=0;j<titles.length;j++){
					String value = "";
					if(contentName[j].equals(field1.getName())){//加入时间
						value = item.getMemberName();
					}
					if(contentName[j].equals(field2.getName())){//加入时间
						List<UnionPreferentialItem> unionPreferentialItems = item.getItems();
						int size = unionPreferentialItems.size();
						int count = 1;
						for(UnionPreferentialItem unionPreferentialItem : unionPreferentialItems){
							if(count == size){
								value = value + unionPreferentialItem.getName();
							}else {
								value = value + unionPreferentialItem.getName() + "||";
							}
							count++;
						}
					}
					if(contentName[j].equals(field3.getName())){//加入时间
						value = item.getCardNo();
					}
					if(contentName[j].equals(field4.getName())){//加入时间
						value = item.getPhone();
					}
					if(contentName[j].equals(field5.getName())){//加入时间
						value = String.valueOf(CommonUtil.isEmpty(item.getConsumeMoney())? 0 : item.getConsumeMoney());
					}
					if(contentName[j].equals(field6.getName())){//加入时间
						value = String.valueOf(CommonUtil.isEmpty(item.getPayMoney())? 0 : item.getPayMoney());
					}
					if(contentName[j].equals(field7.getName())){//加入时间
						value = DateTimeKit.format(item.getCreatetime(), "yyyy-MM-dd HH:mm");
					}
					HSSFCell cell = row.createCell(j);
					cell.setCellValue(value);
					cell.setCellStyle(styleCenter);
				}

			}
			return wb;
		}catch (Exception e){
			return null;
		}

	}


	@Override
	public HSSFWorkbook exportConsumeToDetail(String[] titles, String[] contentName, List<UnionConsumeVO> list) {
		try{
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFCellStyle styleCenter = wb.createCellStyle();
			HSSFSheet sheet = createHSSFSheet(titles, wb, styleCenter);
			sheet.setColumnWidth(5, 100 * 150);
			Class<UnionConsumeVO> clazz = UnionConsumeVO.class;
			Field field1 = clazz.getDeclaredField("memberName");
			Field field2 = clazz.getDeclaredField("items");
			Field field3 = clazz.getDeclaredField("cardNo");
			Field field4 = clazz.getDeclaredField("phone");
			Field field5 = clazz.getDeclaredField("consumeMoney");
			Field field6 = clazz.getDeclaredField("payMoney");
			Field field7 = clazz.getDeclaredField("createtime");
			for (int i=0;i<list.size();i++) {
				UnionConsumeVO item = list.get(i);
				HSSFRow row = sheet.createRow(i + 1);
				for(int j=0;j<titles.length;j++){
					String value = "";
					if(contentName[j].equals(field1.getName())){//加入时间
						value = item.getMemberName();
					}
					if(contentName[j].equals(field2.getName())){//加入时间
						List<UnionPreferentialItem> unionPreferentialItems = item.getItems();
						int size = unionPreferentialItems.size();
						int count = 1;
						for(UnionPreferentialItem unionPreferentialItem : unionPreferentialItems){
							if(count == size){
								value = value + unionPreferentialItem.getName();
							}else {
								value = value + unionPreferentialItem.getName() + "||";
							}
							count++;
						}
					}
					if(contentName[j].equals(field3.getName())){//加入时间
						value = item.getCardNo();
					}
					if(contentName[j].equals(field4.getName())){//加入时间
						value = item.getPhone();
					}
					if(contentName[j].equals(field5.getName())){//加入时间
						value = String.valueOf(CommonUtil.isEmpty(item.getConsumeMoney())? 0 : item.getConsumeMoney());
					}
					if(contentName[j].equals(field6.getName())){//加入时间
						value = String.valueOf(CommonUtil.isEmpty(item.getPayMoney())? 0 : item.getPayMoney());
					}
					if(contentName[j].equals(field7.getName())){//加入时间
						value = DateTimeKit.format(item.getCreatetime(), "yyyy-MM-dd HH:mm");
					}
					HSSFCell cell = row.createCell(j);
					cell.setCellValue(value);
					cell.setCellStyle(styleCenter);
				}

			}
			return wb;
		}catch (Exception e){
			return null;
		}
	}

	/**
	 * 创建sheet
	 * @param titles
	 * @param wb
	 * @param styleCenter
	 * @return
	 */
	private HSSFSheet createHSSFSheet(String[] titles, HSSFWorkbook wb, HSSFCellStyle styleCenter){
		HSSFSheet sheet = wb.createSheet("sheet1");
		HSSFRow rowTitle = sheet.createRow(0);
		HSSFCellStyle styleTitle = wb.createCellStyle();
		styleTitle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		HSSFFont fontTitle = wb.createFont();
		fontTitle.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		fontTitle.setFontName("宋体");
		fontTitle.setFontHeight((short) 200);
		styleTitle.setFont(fontTitle);

		HSSFCell cellTitle = null;
		for(int i=0;i<titles.length;i++){
			cellTitle = rowTitle.createCell(i);
			cellTitle.setCellValue(titles[i]);
			cellTitle.setCellStyle(styleTitle);
		}
		styleCenter.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		return sheet;
	}
}
