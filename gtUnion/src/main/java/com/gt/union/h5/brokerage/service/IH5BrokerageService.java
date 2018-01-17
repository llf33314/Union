package com.gt.union.h5.brokerage.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.Member;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.h5.brokerage.vo.*;
import com.gt.union.opportunity.brokerage.service.IUnionBrokeragePayStrategyService;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.main.vo.UnionPayVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 佣金平台 服务接口
 *
 * @author linweicong
 * @version 2017-12-18 11:45:45
 */
public interface IH5BrokerageService {

    //***************************************** Domain Driven Design - get ********************************************

    /**
     * 佣金平台-首页
     *
     * @param h5BrokerageUser 登录信息
     * @return IndexVO
     * @throws Exception 统一处理异常
     */
    IndexVO getIndexVO(H5BrokerageUser h5BrokerageUser) throws Exception;

    /**
     * 佣金平台-首页-我要提现
     *
     * @param h5BrokerageUser 登录信息
     * @return WithdrawalVO
     * @throws Exception 统一处理异常
     */
    WithdrawalVO getWithdrawalVO(H5BrokerageUser h5BrokerageUser) throws Exception;

    //***************************************** Domain Driven Design - list ********************************************

    /**
     * 获取我的联盟列表
     *
     * @param h5BrokerageUser 登录信息
     * @return List<UnionMain>
     * @throws Exception 统一处理异常
     */
    List<UnionMain> listMyUnion(H5BrokerageUser h5BrokerageUser) throws Exception;

    /**
     * 佣金平台-我要提现-提现记录-分页
     *
     * @param h5BrokerageUser 登录信息
     * @param page
     * @return List<UnionBrokerageWithdrawal>
     * @throws Exception 统一处理异常
     */
    Page pageWithdrawalHistory(H5BrokerageUser h5BrokerageUser, Page page) throws Exception;

    /**
     * 佣金平台-首页-我需支付-已支付-分页
     *
     * @param h5BrokerageUser 登录信息
     * @param optUnionId      联盟id
     * @param page
     * @return List<OpportunityBrokerageVO>
     * @throws Exception 统一处理异常
     */
    Page pageToOpportunityBrokerageVO(H5BrokerageUser h5BrokerageUser, Integer optUnionId, Page page) throws Exception;

    /**
     * 佣金平台-首页-我要提现-佣金明细-推荐佣金-分页
     * @param h5BrokerageUser
     * @param unionId
     * @param page
     * @return
     */
    Page pageFromOpportunityBrokerageVO(H5BrokerageUser h5BrokerageUser, Integer unionId, Page page) throws Exception;

    /**
     * 拥挤平台-首页-我要提现-佣金明细-售卡佣金-分页
     *
     * @param h5BrokerageUser 登录信息
     * @param optUnionId      联盟id
     * @param page
     * @return
     * @throws Exception 统一处理异常
     */
    Page pageCardBrokerageVO(H5BrokerageUser h5BrokerageUser, Integer optUnionId, Page page) throws Exception;

    /**
     * 佣金平台-首页-我需支付-未支付-分页
     *
     * @param h5BrokerageUser 登录信息
     * @param optUnionId      联盟id
     * @param page
     * @return List<OpportunityBrokerageVO>
     * @throws Exception 统一处理异常
     */
    Page pageUnPaidOpportunityBrokerageVO(H5BrokerageUser h5BrokerageUser, Integer optUnionId, Page page) throws Exception;

    /**
     * 佣金平台-首页-我未收佣金-分页
     *
     * @param h5BrokerageUser 登录信息
     * @param optUnionId      联盟id
     * @param page
     * @return List<OpportunityBrokerageVO>
     * @throws Exception 统一处理异常
     */
    Page pageUnReceivedOpportunityBrokerageVO(H5BrokerageUser h5BrokerageUser, Integer optUnionId, Page page) throws Exception;

    //***************************************** Domain Driven Design - save ********************************************

    /**
     * 佣金平台-首页-我要提现-立即提现
     *
     * @param h5BrokerageUser 登录信息
     * @param member          会员
     * @param money           提现金额
     * @return GtJsonResult
     * @throws Exception 统一处理异常
     */
    GtJsonResult withdrawal(H5BrokerageUser h5BrokerageUser, Member member, Double money) throws Exception;

    /**
     * 佣金平台-首页-我需支付-未支付-去支付
     *
     * @param h5BrokerageUser 登录信息
     * @param unionId         联盟id
     * @param opportunityId   商机id
     * @param memberId
     * @return UnionPayVO
     * @throws Exception 统一处理异常
     */
    UnionPayVO toPayByUnionIdAndOpportunityId(H5BrokerageUser h5BrokerageUser, Integer unionId, Integer opportunityId, Integer memberId) throws Exception;

    /**
     * 佣金平台-首页-我需支付-未支付-一键支付
     *
     * @param h5BrokerageUser 登录信息
     * @param unionId         联盟id
     * @param unionBrokeragePayStrategyService
	 * @param memberId
     * @return UnionPayVO
     * @throws Exception 统一处理异常
     */
    UnionPayVO batchPayByUnionId(H5BrokerageUser h5BrokerageUser, Integer unionId, IUnionBrokeragePayStrategyService unionBrokeragePayStrategyService, Integer memberId) throws Exception;

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    /**
     * 佣金平台-手机号登录
     *
     * @param request    请求对象
     * @param loginPhone 表单内容
     * @throws Exception 统一处理异常
     */
    void loginByPhone(HttpServletRequest request, LoginPhone loginPhone) throws Exception;

    /**
     * 佣金平台-首页-我未收佣金-催促
     *
     * @param h5BrokerageUser 登录信息
     * @param opportunityId   商机id
     * @throws Exception 统一处理异常
     */
    void urgeUnreceived(H5BrokerageUser h5BrokerageUser, Integer opportunityId) throws Exception;

    //***************************************** Domain Driven Design - count *******************************************

    /**
     * 佣金平台-首页-我需支付-已支付-总额
     *
     * @param h5BrokerageUser 登录信息
     * @param optUnionId      联盟id
     * @return Double
     * @throws Exception 统一处理异常
     */
    Double sumToPaidOpportunityBrokerage(H5BrokerageUser h5BrokerageUser, Integer optUnionId) throws Exception;

    /**
     * 佣金平台-首页-我要提现-佣金明细-推荐佣金-总额
     *
     * @param h5BrokerageUser 登录信息
     * @param optUnionId      联盟id
     * @return Double
     * @throws Exception 统一处理异常
     */
    Double sumPaidFromOpportunityBrokerage(H5BrokerageUser h5BrokerageUser, Integer optUnionId) throws Exception;

    /**
     * 佣金平台-首页-我要提现-佣金明细-售卡佣金-总额
     *
     * @param h5BrokerageUser 登录信息
     * @param optUnionId      联盟id
     * @return Double
     * @throws Exception 统一处理异常
     */
    Double sumPaidCardBrokerage(H5BrokerageUser h5BrokerageUser, Integer optUnionId) throws Exception;

    /**
     * 佣金平台-首页-我需支付-未支付-总额
     *
     * @param h5BrokerageUser 登录信息
     * @param optUnionId      联盟id
     * @return Double
     * @throws Exception 统一处理异常
     */
    Double sumUnPaidOpportunityBrokerage(H5BrokerageUser h5BrokerageUser, Integer optUnionId) throws Exception;

    /**
     * 佣金平台-首页-我未收佣金-总额
     *
     * @param h5BrokerageUser 登录信息
     * @param optUnionId      联盟id
     * @return Double
     * @throws Exception 统一处理异常
     */
    Double sumUnReceivedOpportunityBrokerage(H5BrokerageUser h5BrokerageUser, Integer optUnionId) throws Exception;


    //***************************************** Domain Driven Design - boolean *****************************************

    //***************************************** Domain Driven Design - filter ******************************************
}
