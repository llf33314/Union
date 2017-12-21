package com.gt.union;

import com.alibaba.fastjson.JSON;
import com.gt.union.api.amqp.sender.PhoneMessageSender;
import com.gt.union.api.client.address.AddressService;
import com.gt.union.api.client.dict.IDictService;
import com.gt.union.api.client.member.MemberService;
import com.gt.union.api.client.shop.ShopService;
import com.gt.union.api.client.user.IBusUserService;
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

    @Test
    public void testUnion() {
//		System.out.println(JSON.toJSONString(shopService.listByBusId(33)));
        List list = new ArrayList<>();
        list.add(23);
        list.add(28);
        list.add(29);
        list.add(31);
        System.out.println(JSON.toJSONString(busUserService.getWxPublicUserByBusId(33)));
    }
}
