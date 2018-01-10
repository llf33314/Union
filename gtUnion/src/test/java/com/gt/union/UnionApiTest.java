package com.gt.union;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.union.api.amqp.sender.PhoneMessageSender;
import com.gt.union.api.client.address.AddressService;
import com.gt.union.api.client.dict.IDictService;
import com.gt.union.api.client.member.MemberService;
import com.gt.union.api.client.shop.ShopService;
import com.gt.union.api.client.user.IBusUserService;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.util.UnionSessionUtil;
import com.gt.union.finance.verifier.entity.UnionVerifier;
import com.gt.union.h5.brokerage.service.IH5BrokerageService;
import com.gt.union.h5.brokerage.vo.H5BrokerageUser;
import com.gt.union.opportunity.main.constant.OpportunityConstant;
import com.gt.union.opportunity.main.service.IUnionOpportunityService;
import com.gt.union.wxapp.card.service.IWxAppCardService;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/24 0024.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = UnionApplication.class)// 指定spring-boot的启动类
public class UnionApiTest {

    @Autowired
    private ShopService shopService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private IDictService dictService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private PhoneMessageSender sender;

    @Autowired
    private IBusUserService busUserService;

    @Autowired
    private IH5BrokerageService brokerageService;

    @Autowired
    private IUnionOpportunityService unionOpportunityService;

    @Autowired
    private IWxAppCardService wxAppCardService;

    @Test
    public void testUnion() throws Exception {
//		System.out.println(JSON.toJSONString(shopService.listByBusId(33)));
        List list = new ArrayList<>();
        list.add(23);
//        list.add(28);
//        list.add(29);
//        list.add(31);
//        System.out.println(unionOpportunityService.sumValidBrokerageMoneyByFromMemberIdListAndAcceptStatusAndIsClose(list, OpportunityConstant.ACCEPT_STATUS_CONFIRMED, OpportunityConstant.IS_CLOSE_NO));
        Page page = new Page<>();
        System.out.println(JSON.toJSONString(wxAppCardService.pageConsumeByPhone(page,"15986670850")));
    }
}
