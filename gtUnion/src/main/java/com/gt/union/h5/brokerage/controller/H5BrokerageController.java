package com.gt.union.h5.brokerage.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.Member;
import com.gt.api.util.SessionUtils;
import com.gt.union.api.client.member.MemberService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.MockUtil;
import com.gt.union.common.util.PageUtil;
import com.gt.union.common.util.PropertiesUtil;
import com.gt.union.common.util.UnionSessionUtil;
import com.gt.union.h5.brokerage.service.IH5BrokerageService;
import com.gt.union.h5.brokerage.vo.*;
import com.gt.union.opportunity.brokerage.entity.UnionBrokerageWithdrawal;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.main.vo.UnionPayVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

    //-------------------------------------------------- get ----------------------------------------------------------

    @ApiOperation(value = "获取我的联盟列表", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/myUnion", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listMyUnion(HttpServletRequest request) throws Exception {
        H5BrokerageUser h5BrokerageUser = UnionSessionUtil.getH5BrokerageUser(request);
        // mock
        List<UnionMain> result;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            result = MockUtil.list(UnionMain.class, 3);
        } else {
            result = h5BrokerageService.listMyUnion(h5BrokerageUser);
        }
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "佣金平台-首页", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "index", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getIndexVO(HttpServletRequest request) throws Exception {
        H5BrokerageUser h5BrokerageUser = UnionSessionUtil.getH5BrokerageUser(request);
        // mock
        IndexVO result;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            result = MockUtil.get(IndexVO.class);
        } else {
            result = h5BrokerageService.getIndexVO(h5BrokerageUser);
        }
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "佣金平台-首页-我要提现", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/withdrawal", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getWithdrawalVO(HttpServletRequest request) throws Exception {
        H5BrokerageUser h5BrokerageUser = UnionSessionUtil.getH5BrokerageUser(request);
        // mock
        WithdrawalVO result;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            result = MockUtil.get(WithdrawalVO.class);
        } else {
            result = h5BrokerageService.getWithdrawalVO(h5BrokerageUser);
        }
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "佣金平台-我要提现-提现记录-分页", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/withdrawal/history/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageWithdrawalHistory(HttpServletRequest request, Page page) throws Exception {
        H5BrokerageUser h5BrokerageUser = UnionSessionUtil.getH5BrokerageUser(request);
        // mock
        List<UnionBrokerageWithdrawal> withdrawalList;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            withdrawalList = MockUtil.list(UnionBrokerageWithdrawal.class, page.getSize());
        } else {
            withdrawalList = h5BrokerageService.listWithdrawalHistory(h5BrokerageUser);
        }
        Page<UnionBrokerageWithdrawal> result = (Page<UnionBrokerageWithdrawal>) page;
        result = PageUtil.setRecord(result, withdrawalList);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "佣金平台-首页-我要提现-佣金明细-推荐佣金-总额", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/withdrawal/detail/opportunity/paidSum", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String sumPaidOpportunityBrokerage(HttpServletRequest request,
                                              @ApiParam(value = "联盟id", name = "unionId")
                                              @RequestParam(value = "unionId", required = false) Integer unionId) throws Exception {
        H5BrokerageUser h5BrokerageUser = UnionSessionUtil.getH5BrokerageUser(request);
        // mock
        Double result;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            result = MockUtil.get(Double.class);
        } else {
            result = h5BrokerageService.sumPaidOpportunityBrokerage(h5BrokerageUser, unionId);
        }
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "佣金平台-首页-我要提现-佣金明细-推荐佣金-分页", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/withdrawal/detail/opportunity/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pagePaidOpportunityBrokerageVO(HttpServletRequest request, Page page,
                                                 @ApiParam(value = "联盟id", name = "unionId")
                                                 @RequestParam(value = "unionId", required = false) Integer unionId) throws Exception {
        H5BrokerageUser h5BrokerageUser = UnionSessionUtil.getH5BrokerageUser(request);
        // mock
        List<OpportunityBrokerageVO> voList;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            voList = MockUtil.list(OpportunityBrokerageVO.class, page.getSize());
        } else {
            voList = h5BrokerageService.listOpportunityBrokerageVO(h5BrokerageUser, unionId);
        }
        Page<OpportunityBrokerageVO> result = (Page<OpportunityBrokerageVO>) page;
        result = PageUtil.setRecord(result, voList);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "佣金平台-首页-我要提现-佣金明细-售卡佣金-总额", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/withdrawal/detail/card/paidSum", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String sumPaidCardBrokerage(HttpServletRequest request,
                                       @ApiParam(value = "联盟id", name = "unionId")
                                       @RequestParam(value = "unionId", required = false) Integer unionId) throws Exception {
        H5BrokerageUser h5BrokerageUser = UnionSessionUtil.getH5BrokerageUser(request);
        // mock
        Double result;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            result = MockUtil.get(Double.class);
        } else {
            result = h5BrokerageService.sumPaidCardBrokerage(h5BrokerageUser, unionId);
        }
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "拥挤平台-首页-我要提现-佣金明细-售卡佣金-分页", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/withdrawal/detail/card/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageCardBrokerageVO(HttpServletRequest request, Page page,
                                      @ApiParam(value = "联盟id", name = "unionId")
                                      @RequestParam(value = "unionId", required = false) Integer unionId) throws Exception {
        H5BrokerageUser h5BrokerageUser = UnionSessionUtil.getH5BrokerageUser(request);
        // mock
        List<CardBrokerageVO> voList;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            voList = MockUtil.list(CardBrokerageVO.class, page.getSize());
        } else {
            voList = h5BrokerageService.listCardBrokerageVO(h5BrokerageUser, unionId);
        }
        Page<CardBrokerageVO> result = (Page<CardBrokerageVO>) page;
        result = PageUtil.setRecord(result, voList);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "佣金平台-首页-我需支付-未支付-总额", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/pay/unPaid/sum", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String sumUnPaidOpportunityBrokerage(HttpServletRequest request,
                                                @ApiParam(value = "联盟id", name = "unionId")
                                                @RequestParam(value = "unionId", required = false) Integer unionId) throws Exception {
        H5BrokerageUser h5BrokerageUser = UnionSessionUtil.getH5BrokerageUser(request);
        // mock
        Double result;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            result = MockUtil.get(Double.class);
        } else {
            result = h5BrokerageService.sumUnPaidOpportunityBrokerage(h5BrokerageUser, unionId);
        }
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "佣金平台-首页-我需支付-未支付-分页", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/pay/unPaid/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageUnPaidOpportunityBrokerageVO(HttpServletRequest request, Page page,
                                                   @ApiParam(value = "联盟id", name = "unionId")
                                                   @RequestParam(value = "unionId", required = false) Integer unionId) throws Exception {
        H5BrokerageUser h5BrokerageUser = UnionSessionUtil.getH5BrokerageUser(request);
        // mock
        List<OpportunityBrokerageVO> voList;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            voList = MockUtil.list(OpportunityBrokerageVO.class, page.getSize());
        } else {
            voList = h5BrokerageService.listUnPaidOpportunityBrokerageVO(h5BrokerageUser, unionId);
        }
        Page<OpportunityBrokerageVO> result = (Page<OpportunityBrokerageVO>) page;
        result = PageUtil.setRecord(result, voList);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "佣金平台-首页-我需支付-已支付-总额", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/pay/paid/sum", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String sumPaidOpportunityBrokerage2(HttpServletRequest request,
                                               @ApiParam(value = "联盟id", name = "unionId")
                                               @RequestParam(value = "unionId", required = false) Integer unionId) throws Exception {
        H5BrokerageUser h5BrokerageUser = UnionSessionUtil.getH5BrokerageUser(request);
        // mock
        Double result;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            result = MockUtil.get(Double.class);
        } else {
            result = h5BrokerageService.sumPaidOpportunityBrokerage(h5BrokerageUser, unionId);
        }
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "佣金平台-首页-我需支付-已支付-分页", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/pay/paid/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pagePaidOpportunityBrokerageVO2(HttpServletRequest request, Page page,
                                                  @ApiParam(value = "联盟id", name = "unionId")
                                                  @RequestParam(value = "unionId", required = false) Integer unionId) throws Exception {
        H5BrokerageUser h5BrokerageUser = UnionSessionUtil.getH5BrokerageUser(request);
        // mock
        List<OpportunityBrokerageVO> voList;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            voList = MockUtil.list(OpportunityBrokerageVO.class, page.getSize());
        } else {
            voList = h5BrokerageService.listOpportunityBrokerageVO(h5BrokerageUser, unionId);
        }
        Page<OpportunityBrokerageVO> result = (Page<OpportunityBrokerageVO>) page;
        result = PageUtil.setRecord(result, voList);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "佣金平台-首页-我未收佣金-总额", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unreceived/sum", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String sumUnReceivedOpportunityBrokerage(HttpServletRequest request,
                                                    @ApiParam(value = "联盟id", name = "unionId")
                                                    @RequestParam(value = "unionId", required = false) Integer unionId) throws Exception {
        H5BrokerageUser h5BrokerageUser = UnionSessionUtil.getH5BrokerageUser(request);
        // mock
        Double result;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            result = MockUtil.get(Double.class);
        } else {
            result = h5BrokerageService.sumUnReceivedOpportunityBrokerage(h5BrokerageUser, unionId);
        }
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "佣金平台-首页-我未收佣金-分页", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unreceived/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageUnReceivedOpportunityBrokerageVO(HttpServletRequest request, Page page,
                                                       @ApiParam(value = "联盟id", name = "unionId")
                                                       @RequestParam(value = "unionId", required = false) Integer unionId) throws Exception {
        H5BrokerageUser h5BrokerageUser = UnionSessionUtil.getH5BrokerageUser(request);
        // mock
        List<OpportunityBrokerageVO> voList;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            voList = MockUtil.list(OpportunityBrokerageVO.class, page.getSize());
        } else {
            voList = h5BrokerageService.listUnReceivedOpportunityBrokerageVO(h5BrokerageUser, unionId);
        }
        Page<OpportunityBrokerageVO> result = (Page<OpportunityBrokerageVO>) page;
        result = PageUtil.setRecord(result, voList);
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
        UnionPayVO result;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            result = MockUtil.get(UnionPayVO.class);
        } else {
            result = h5BrokerageService.toPayByUnionIdAndOpportunityId(h5BrokerageUser, unionId, opportunityId);
        }
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "佣金平台-首页-我需支付-未支付-一键支付", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/pay/unpaid/batchPay", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String batchPay(HttpServletRequest request,
                           @ApiParam(value = "联盟id", name = "unionId")
                           @RequestParam(value = "unionId", required = false) Integer unionId) throws Exception {
        H5BrokerageUser h5BrokerageUser = UnionSessionUtil.getH5BrokerageUser(request);
        UnionPayVO result;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            result = MockUtil.get(UnionPayVO.class);
        } else {
            result = h5BrokerageService.batchPayByUnionId(h5BrokerageUser, unionId);
        }
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    //------------------------------------------------- post ----------------------------------------------------------

    @ApiOperation(value = "佣金平台-手机号登录", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/login/phone", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String loginByPhone(HttpServletRequest request,
                               @ApiParam(value = "表单内容", name = "loginPhone", required = true)
                               @RequestBody LoginPhone loginPhone) throws Exception {
        if (CommonConstant.COMMON_YES != ConfigConstant.IS_MOCK) {
            h5BrokerageService.loginByPhone(request, loginPhone);
        }
        return GtJsonResult.instanceSuccessMsg().toString();
    }

    @ApiOperation(value = "佣金平台-首页-我要提现-立即提现", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/withdrawal", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String withdrawal(HttpServletRequest request,
                             @ApiParam(value = "提现金额", name = "money", required = true)
                             @RequestBody Double money) throws Exception {
        H5BrokerageUser h5BrokerageUser = UnionSessionUtil.getH5BrokerageUser(request);
        if (CommonConstant.COMMON_YES != ConfigConstant.IS_MOCK) {
            Member member = SessionUtils.getLoginMember(request, PropertiesUtil.getDuofenBusId());
            if (member == null) {
                return memberService.authorizeMemberWx(request, PropertiesUtil.getUnionUrl() + "/h5Brokerage/#/" + "toExtract").toString();
            } else {
                // （1）	判断是否已微信授权，即session里获取member，如果已授权，则执行下一步；否则，获取授权链接（调接口）并返回
                return h5BrokerageService.withdrawal(h5BrokerageUser, member, money).toString();
            }
        }
        return GtJsonResult.instanceSuccessMsg().toString();
    }

    @ApiOperation(value = "佣金平台-首页-我未收佣金-催促", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unreceived/urge", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String urgeUnreceived(HttpServletRequest request,
                                 @ApiParam(value = "商机id", name = "opportunityId", required = true)
                                 @RequestParam(value = "opportunityId") Integer opportunityId) throws Exception {
        H5BrokerageUser h5BrokerageUser = UnionSessionUtil.getH5BrokerageUser(request);
        if (CommonConstant.COMMON_YES != ConfigConstant.IS_MOCK) {
            h5BrokerageService.urgeUnreceived(h5BrokerageUser, opportunityId);
        }
        return GtJsonResult.instanceSuccessMsg().toString();
    }

}
