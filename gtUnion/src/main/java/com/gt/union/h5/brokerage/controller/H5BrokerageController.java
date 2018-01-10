package com.gt.union.h5.brokerage.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.Member;
import com.gt.api.bean.sign.SignBean;
import com.gt.api.util.SessionUtils;
import com.gt.api.util.sign.SignUtils;
import com.gt.union.api.client.member.MemberService;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.*;
import com.gt.union.h5.brokerage.service.IH5BrokerageService;
import com.gt.union.h5.brokerage.vo.*;
import com.gt.union.opportunity.brokerage.service.IUnionBrokeragePayStrategyService;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.main.vo.UnionPayVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * H5佣金平台 前端管理器
 *
 * @author linweicong
 * @version 2017-12-01 11:27:08
 */
@Api(description = "H5佣金平台")
@RestController
@RequestMapping("/h5Brokerage")
public class H5BrokerageController {

    @Autowired
    private IH5BrokerageService h5BrokerageService;

    @Autowired
    private MemberService memberService;

    @Resource(name = "unionPhoneBrokeragePayService")
    private IUnionBrokeragePayStrategyService unionBrokeragePayStrategyService;

    //-------------------------------------------------- get ----------------------------------------------------------

    @ApiOperation(value = "获取我的联盟列表", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/myUnion", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listMyUnion(HttpServletRequest request) throws Exception {
        H5BrokerageUser h5BrokerageUser = UnionSessionUtil.getH5BrokerageUser(request);
        List<UnionMain> result = h5BrokerageService.listMyUnion(h5BrokerageUser);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "佣金平台-首页", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/index", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getIndexVO(HttpServletRequest request) throws Exception {
        H5BrokerageUser h5BrokerageUser = UnionSessionUtil.getH5BrokerageUser(request);
        IndexVO result = h5BrokerageService.getIndexVO(h5BrokerageUser);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "佣金平台-首页-我要提现", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/withdrawal", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getWithdrawalVO(HttpServletRequest request) throws Exception {
        H5BrokerageUser h5BrokerageUser = UnionSessionUtil.getH5BrokerageUser(request);
        WithdrawalVO result = h5BrokerageService.getWithdrawalVO(h5BrokerageUser);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "佣金平台-我要提现-提现记录-分页", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/withdrawal/history/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageWithdrawalHistory(HttpServletRequest request, Page page) throws Exception {
        H5BrokerageUser h5BrokerageUser = UnionSessionUtil.getH5BrokerageUser(request);
        Page result = h5BrokerageService.listPageWithdrawalHistory(h5BrokerageUser, page);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "佣金平台-首页-我要提现-佣金明细-推荐佣金-总额", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/withdrawal/detail/opportunity/paidSum", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String sumPaidOpportunityBrokerage(HttpServletRequest request,
                                              @ApiParam(value = "联盟id", name = "unionId")
                                              @RequestParam(value = "unionId", required = false) Integer unionId) throws Exception {
        H5BrokerageUser h5BrokerageUser = UnionSessionUtil.getH5BrokerageUser(request);
        Double result = h5BrokerageService.sumPaidOpportunityBrokerage(h5BrokerageUser, unionId);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "佣金平台-首页-我要提现-佣金明细-推荐佣金-分页", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/withdrawal/detail/opportunity/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pagePaidOpportunityBrokerageVO(HttpServletRequest request, Page page,
                                                 @ApiParam(value = "联盟id", name = "unionId")
                                                 @RequestParam(value = "unionId", required = false) Integer unionId) throws Exception {
        H5BrokerageUser h5BrokerageUser = UnionSessionUtil.getH5BrokerageUser(request);
        Page result = h5BrokerageService.listPageOpportunityBrokerageVO(h5BrokerageUser, unionId, page);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "佣金平台-首页-我要提现-佣金明细-售卡佣金-总额", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/withdrawal/detail/card/paidSum", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String sumPaidCardBrokerage(HttpServletRequest request,
                                       @ApiParam(value = "联盟id", name = "unionId")
                                       @RequestParam(value = "unionId", required = false) Integer unionId) throws Exception {
        H5BrokerageUser h5BrokerageUser = UnionSessionUtil.getH5BrokerageUser(request);
        Double result = h5BrokerageService.sumPaidCardBrokerage(h5BrokerageUser, unionId);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "拥挤平台-首页-我要提现-佣金明细-售卡佣金-分页", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/withdrawal/detail/card/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageCardBrokerageVO(HttpServletRequest request, Page page,
                                      @ApiParam(value = "联盟id", name = "unionId")
                                      @RequestParam(value = "unionId", required = false) Integer unionId) throws Exception {
        H5BrokerageUser h5BrokerageUser = UnionSessionUtil.getH5BrokerageUser(request);
        Page result = h5BrokerageService.listPageCardBrokerageVO(h5BrokerageUser, unionId, page);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "佣金平台-首页-我需支付-未支付-总额", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/pay/unPaid/sum", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String sumUnPaidOpportunityBrokerage(HttpServletRequest request,
                                                @ApiParam(value = "联盟id", name = "unionId")
                                                @RequestParam(value = "unionId", required = false) Integer unionId) throws Exception {
        H5BrokerageUser h5BrokerageUser = UnionSessionUtil.getH5BrokerageUser(request);
        Double result = h5BrokerageService.sumUnPaidOpportunityBrokerage(h5BrokerageUser, unionId);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "佣金平台-首页-我需支付-未支付-分页", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/pay/unPaid/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageUnPaidOpportunityBrokerageVO(HttpServletRequest request, Page page,
                                                   @ApiParam(value = "联盟id", name = "unionId")
                                                   @RequestParam(value = "unionId", required = false) Integer unionId) throws Exception {
        H5BrokerageUser h5BrokerageUser = UnionSessionUtil.getH5BrokerageUser(request);
        Page result = h5BrokerageService.listPageUnPaidOpportunityBrokerageVO(h5BrokerageUser, unionId, page);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "佣金平台-首页-我需支付-已支付-总额", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/pay/paid/sum", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String sumPaidOpportunityBrokerage2(HttpServletRequest request,
                                               @ApiParam(value = "联盟id", name = "unionId")
                                               @RequestParam(value = "unionId", required = false) Integer unionId) throws Exception {
        H5BrokerageUser h5BrokerageUser = UnionSessionUtil.getH5BrokerageUser(request);
        Double result = h5BrokerageService.sumPaidOpportunityBrokerage(h5BrokerageUser, unionId);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "佣金平台-首页-我需支付-已支付-分页", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/pay/paid/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pagePaidOpportunityBrokerageVO2(HttpServletRequest request, Page page,
                                                  @ApiParam(value = "联盟id", name = "unionId")
                                                  @RequestParam(value = "unionId", required = false) Integer unionId) throws Exception {
        H5BrokerageUser h5BrokerageUser = UnionSessionUtil.getH5BrokerageUser(request);
        Page result = h5BrokerageService.listPageOpportunityBrokerageVO(h5BrokerageUser, unionId, page);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "佣金平台-首页-我未收佣金-总额", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unreceived/sum", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String sumUnReceivedOpportunityBrokerage(HttpServletRequest request,
                                                    @ApiParam(value = "联盟id", name = "unionId")
                                                    @RequestParam(value = "unionId", required = false) Integer unionId) throws Exception {
        H5BrokerageUser h5BrokerageUser = UnionSessionUtil.getH5BrokerageUser(request);
        Double result = h5BrokerageService.sumUnReceivedOpportunityBrokerage(h5BrokerageUser, unionId);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "佣金平台-首页-我未收佣金-分页", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unreceived/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageUnReceivedOpportunityBrokerageVO(HttpServletRequest request, Page page,
                                                       @ApiParam(value = "联盟id", name = "unionId")
                                                       @RequestParam(value = "unionId", required = false) Integer unionId) throws Exception {
        H5BrokerageUser h5BrokerageUser = UnionSessionUtil.getH5BrokerageUser(request);
        Page result = h5BrokerageService.listPageUnReceivedOpportunityBrokerageVO(h5BrokerageUser, unionId, page);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }


    //-------------------------------------------------- put ----------------------------------------------------------

    @ApiOperation(value = "佣金平台-首页-我需支付-未支付-去支付", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/pay/unpaid/toPay", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String toPay(HttpServletRequest request,
                        @ApiParam(value = "联盟id", name = "unionId", required = true)
                        @RequestParam(value = "unionId") Integer unionId,
                        @ApiParam(value = "商机id", name = "opportunityId", required = true)
                        @RequestParam(value = "opportunityId") Integer opportunityId) throws Exception {
        H5BrokerageUser h5BrokerageUser = UnionSessionUtil.getH5BrokerageUser(request);
        Member member = SessionUtils.getLoginMember(request, PropertiesUtil.getDuofenBusId());
        if (member == null) {
            return memberService.authorizeMemberWx(request, PropertiesUtil.getUnionUrl() + "/brokeragePhone/#/" + "toPayList").toString();
        }
        UnionPayVO result = h5BrokerageService.toPayByUnionIdAndOpportunityId(h5BrokerageUser, unionId, opportunityId, member.getId());
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "佣金平台-首页-我需支付-未支付-一键支付", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/pay/unpaid/batchPay", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String batchPay(HttpServletRequest request,
                           @ApiParam(value = "联盟id", name = "unionId")
                           @RequestParam(value = "unionId", required = false) Integer unionId) throws Exception {
        H5BrokerageUser h5BrokerageUser = UnionSessionUtil.getH5BrokerageUser(request);
        Member member = SessionUtils.getLoginMember(request, PropertiesUtil.getDuofenBusId());
        if (member == null) {
            return memberService.authorizeMemberWx(request, PropertiesUtil.getUnionUrl() + "/brokeragePhone/#/" + "toPayList").toString();
        }
        UnionPayVO result = h5BrokerageService.batchPayByUnionId(h5BrokerageUser, unionId, unionBrokeragePayStrategyService, member.getId());
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    //------------------------------------------------- post ----------------------------------------------------------

    @ApiOperation(value = "获取佣金平台账号登录秘钥", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/loginSign", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String loginSign(HttpServletRequest request, HttpServletResponse response
            ,@ApiParam(name="username", value = "商家账号", required = true) @RequestParam(name = "username") String username
            ,@ApiParam(name="userpwd", value = "商家账号密码", required = true) @RequestParam(name = "userpwd") String userpwd
    ) throws Exception{
        Map<String,Object> param = new HashMap<String,Object>();
        param.put("login_name",username);
        param.put("password",userpwd);
        SignBean sign = SignUtils.sign(PropertiesUtil.getWxmpSignKey() , JSONObject.toJSONString(param));
        if(CommonUtil.isEmpty(sign)){
            throw new BusinessException("登录错误");
        }
        String url = PropertiesUtil.getWxmpUrl() + "/ErpMenus/79B4DE7C/UnionErplogin.do";
        Map<String,Object> data = new HashMap<String,Object>();
        data.put("sign",JSONObject.parseObject(JSON.toJSONString(sign)));
        data.put("url",url);
        return GtJsonResult.instanceSuccessMsg(data).toString();
    }

    @ApiOperation(value = "佣金平台-手机号登录", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/login/phone", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String loginByPhone(HttpServletRequest request,
                               @ApiParam(value = "表单内容", name = "loginPhone", required = true)
                               @RequestBody LoginPhone loginPhone) throws Exception {
        h5BrokerageService.loginByPhone(request, loginPhone);
        return GtJsonResult.instanceSuccessMsg().toString();
    }

    @ApiOperation(value = "佣金平台-首页-我要提现-立即提现", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/withdrawal", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String withdrawal(HttpServletRequest request,
                             @ApiParam(value = "提现金额", name = "money", required = true)
                             @RequestBody Double money) throws Exception {
        H5BrokerageUser h5BrokerageUser = UnionSessionUtil.getH5BrokerageUser(request);
        Member member = SessionUtils.getLoginMember(request, PropertiesUtil.getDuofenBusId());
        if (member == null) {
            return memberService.authorizeMemberWx(request, PropertiesUtil.getUnionUrl() + "/brokeragePhone/#/" + "toExtract").toString();
        } else {
            // （1）	判断是否已微信授权，即session里获取member，如果已授权，则执行下一步；否则，获取授权链接（调接口）并返回
            return h5BrokerageService.withdrawal(h5BrokerageUser, member, money).toString();
        }
    }

    @ApiOperation(value = "佣金平台-首页-我未收佣金-催促", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unreceived/urge", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String urgeUnreceived(HttpServletRequest request,
                                 @ApiParam(value = "商机id", name = "opportunityId", required = true)
                                 @RequestParam(value = "opportunityId") Integer opportunityId) throws Exception {
        H5BrokerageUser h5BrokerageUser = UnionSessionUtil.getH5BrokerageUser(request);
        h5BrokerageService.urgeUnreceived(h5BrokerageUser, opportunityId);
        return GtJsonResult.instanceSuccessMsg().toString();
    }

}
