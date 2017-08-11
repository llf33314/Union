package com.gt.union.controller.business;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.constant.business.UnionBusinessRecommendConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.BigDecimalUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.SessionUtils;
import com.gt.union.entity.business.UnionBusinessRecommend;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.business.IUnionBusinessRecommendService;
import com.gt.union.vo.business.UnionBusinessRecommendFormVO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 联盟商家商机推荐 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
@RestController
@RequestMapping("/unionBusinessRecommend")
public class UnionBusinessRecommendController {
    //TODO 方法签名RESTFUL
	private static final String LIST_UNION_BUSINESS_RECOMMEND = "UnionBusinessRecommendController.listUnionBusinessRecommend()";
	private static final String VERIFY_UNION_BUSINESS_RECOMMEND = "UnionBusinessRecommendController.verifyUnionBusinessRecommend()";
	private static final String SAVE_UNION_BUSINESS_RECOMMEND = "UnionBusinessRecommendController.saveUnionBusinessRecommend()";

	private Logger logger = Logger.getLogger(UnionBusinessRecommendController.class);

	@Autowired
	private IUnionBusinessRecommendService unionBusinessRecommendService;

    /**
     * 商机信息查询：
     * （1）当listType=LIST_TYPE_TO_ME时，查询我的商机信息；
     * （2）当listType=LIST_TYPE_FROM_ME时，查询我推荐的商机信息；
	 * （3）当listType=LIST_TYPE_BROKERAGE_TO_ME时，查询佣金结算-我的佣金收入信息；
     * （4）当listType=LIST_TYPE_BROKERAGE_FROM_ME时，查询佣金结算-我需支付的佣金.
     * @param request
     * @param page
     * @param listType
     * @param unionId
     * @return
     */
    @ApiOperation(value = "商机信息及佣金结算查询"
            , notes = "根据listType进行分类查询"
            , produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String listUnionBusinessRecommend(HttpServletRequest request, Page page
            , @ApiParam(name = "listType"
                    , value = "查询类型：值为1时，查询我的商机信息；"
                                + "值为2时，查询我推荐的商机信息；"
                                + "值为3时，查询我的佣金收入信息；"
                                + "值为4时，查询我需支付的佣金信息；"
								+ "值为5时，查询支付明细信息；"
                                + "值为6时，查询支付明细详情信息."
                    , required = true)
             @RequestParam(name = "listType", required = true) Integer listType
            , @ApiParam(name = "unionId", value = "所属联盟id,查询类型为6时必填", required = false)
             @RequestParam(name = "unionId", required = false) Integer unionId
            , @ApiParam(name = "isAcceptance", value = "商机状态，当勾选多个时用英文字符的分号拼接，如=1;2", required = false)
             @RequestParam(name = "isAcceptance", required = false) String isAcceptance
            , @ApiParam(name = "busId", value = "佣金消费盟员id,查询类型为6时必填", required = false)
             @RequestParam(name = "busId", required = false) Integer busId
            , @ApiParam(name = "isConfirm", value = "佣金结算状态，当勾选多个时用英文字符的分号拼接，如=1;2", required = false)
             @RequestParam(name = "isConfirm", required = false) String isConfirm
            , @ApiParam(name = "userName", value = "顾客姓名，模糊查询", required = false)
             @RequestParam(name = "userName", required = false) String userName
            , @ApiParam(name = "userPhone", value = "顾客电话，模糊查询", required = false)
             @RequestParam(name = "userPhone", required = false) String userPhone) {
        try {
            Page result = null;
            BusUser busUser = SessionUtils.getLoginUser(request);
            if (busUser == null) {
                throw new Exception("UnionBusinessRecommendController.listUnionBusinessRecommend()：无法通过session获取用户的信息!");
            }
            Integer busUserId = busUser.getId();
            if (busUser.getPid() != null && busUser.getPid() != 0) {
                busUserId = busUser.getPid();
            }
            switch (listType) {
                case UnionBusinessRecommendConstant.LIST_TYPE_TO_ME:
                    result = this.unionBusinessRecommendService.listUnionBusinessRecommendToMe(page, busUserId, unionId, isAcceptance, userName, userPhone);
                    break;
                case UnionBusinessRecommendConstant.LIST_TYPE_FROM_ME:
                    result = this.unionBusinessRecommendService.listUnionBusinessRecommendFromMe(page, busUserId, unionId, isAcceptance, userName, userPhone);
                    break;
                case UnionBusinessRecommendConstant.LIST_TYPE_BROKERAGE_TO_ME:
                    result = this.unionBusinessRecommendService.listBrokerageToMe(page, busUserId, busId, unionId, isConfirm, userName, userPhone);
                    break;
                case UnionBusinessRecommendConstant.LIST_TYPE_BROKERAGE_FROM_ME:
                    result = this.unionBusinessRecommendService.listBrokerageFromMe(page, busUserId, busId, unionId, isConfirm, userName, userPhone);
                    break;
                case UnionBusinessRecommendConstant.LIST_TYPE_PAY_DETAIL:
                    result = this.unionBusinessRecommendService.listPayDetail(page, busUserId, unionId);
                    break;
                case UnionBusinessRecommendConstant.LIST_TYPE_PAY_PARTICULAR:
                    List<Map<String, Object>> dataList = this.unionBusinessRecommendService.listPayParticular(unionId, busUserId, busId);
                    BigDecimal businessPriceSum = BigDecimal.valueOf(0.0);
                    if (ListUtil.isNotEmpty(dataList)) {
                        for (int i = 0, size = dataList.size(); i < size; i++) {
                            Map<String, Object> map = dataList.get(i);
                            if (map == null) {
                                continue;
                            }
                            Object businessPriceObj = map.get("businessPrice");
                            Double businessPrice = businessPriceObj == null ? Double.valueOf(0.0) : Double.valueOf(businessPriceObj.toString());
                            businessPriceSum = BigDecimalUtil.add(businessPriceSum, businessPrice);
                        }
                    }
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("data", dataList);
                    resultMap.put("businessPriceSum", businessPriceSum);
                    return GTJsonResult.instanceSuccessMsg(resultMap).toString();
                default:
                    throw new Exception("UnionBusinessRecommendController.listUnionBusinessRecommend():不支持的查询类型listType(value=" + listType + ")!");
            }
            return GTJsonResult.instanceSuccessMsg(result).toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(LIST_UNION_BUSINESS_RECOMMEND, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
	}


	/**
	 * 商机审核
	 * @param request
	 * @param id
	 * @return
	 */
	@ApiOperation(value = "商机审核" , notes = "商机审核，审核状态 1：接受 2：拒绝" , produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
	public String verifyUnionBusinessRecommend(HttpServletRequest request
			, @ApiParam(name = "id", value = "商机id", required = true) @PathVariable("id") Integer id
			, @ApiParam(name = "isAcceptance", value = "审核状态 1：接受 2：拒绝", required = true) @RequestParam Integer isAcceptance){
		try{
            BusUser user = SessionUtils.getLoginUser(request);
            UnionBusinessRecommend recommend = new UnionBusinessRecommend();
			recommend.setId(id);
			recommend.setIsAcceptance(isAcceptance);
			unionBusinessRecommendService.updateVerifyRecommend(recommend);
			if(isAcceptance == 1){
				//接受
				return GTJsonResult.instanceSuccessMsg().toString();
			}else if(isAcceptance == 2){
				//拒绝
				return GTJsonResult.instanceSuccessMsg().toString();
			}
			return GTJsonResult.instanceErrorMsg(VERIFY_UNION_BUSINESS_RECOMMEND, "","审核失败").toString();
		}catch (BaseException e){
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
		}catch (Exception e){
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(VERIFY_UNION_BUSINESS_RECOMMEND, e.getMessage(),ExceptionConstant.OPERATE_FAIL).toString();
		}
	}

	/**
	 * 添加商机推荐
	 * @param vo
	 * @return
	 */
	@ApiOperation(value = "添加商机推荐" , notes = "添加商机推荐" , produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String saveUnionBusinessRecommend(HttpServletRequest request
			, @ApiParam(name = "unionBusinessRecommendFormVO", value = "推荐的商机信息", required = true) @RequestBody UnionBusinessRecommendFormVO vo
			, @ApiParam(name = "unionId", value = "联盟id", required = true) @RequestParam Integer unionId){
		try{
            BusUser user = SessionUtils.getLoginUser(request);
            Integer busId = user.getId();
			vo.setBusId(busId);
			unionBusinessRecommendService.saveUnionBusinessRecommend(vo);
            return GTJsonResult.instanceSuccessMsg().toString();
        }catch (BaseException e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        }catch (Exception e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(SAVE_UNION_BUSINESS_RECOMMEND, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
	}

}
