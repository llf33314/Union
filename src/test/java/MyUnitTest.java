import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.plugins.pagination.PageHelper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.gt.api.util.HttpClienUtils;
import com.gt.union.UnionApplication;
import com.gt.union.api.client.address.AddressService;
import com.gt.union.api.client.dict.DictService;
import com.gt.union.api.client.sms.SmsService;
import com.gt.union.api.client.sms.impl.SmsServiceImpl;
import com.gt.union.api.client.user.BusUserService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.DateTimeKit;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.entity.basic.UnionCreateInfoRecord;
import com.gt.union.entity.basic.UnionMain;
import com.gt.union.entity.basic.UnionMember;
import com.gt.union.entity.basic.UnionTransferRecord;
import com.gt.union.entity.business.UnionBusinessRecommend;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.basic.*;
import com.gt.union.service.business.IUnionBusinessRecommendService;
import com.gt.union.service.card.IUnionBusMemberCardService;
import com.gt.union.service.common.IUnionRootService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

/**
 *
 * 测试类
 * Created by Administrator on 2017/8/11 0011.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes=UnionApplication.class)// 指定spring-boot的启动类
public class MyUnitTest {

	@Autowired
	private IUnionBusMemberCardService unionBusMemberCardService;

	@Autowired
	private IUnionRootService unionRootService;

	@Autowired
	private SmsService smsService;

	@Autowired
	private DictService dictService;

	@Autowired
	private IUnionMemberService unionMemberService;

	@Autowired
	private AddressService addressService;

	@Autowired
	private BusUserService busUserService;

	@Autowired
	private IUnionTransferRecordService unionTransferRecordService;

	@Autowired
	private IUnionBusinessRecommendService unionBusinessRecommendService;

	@Test
	public void test1() throws Exception {
		unionBusinessRecommendService.payUnionBusinessRecommendSuccess("ao4D6a4Buwsy4aM9zjvGiD/NBI64kVkU", "20170905163736");
	}

	public static void main(String[] args) {
		System.out.println(Double.valueOf("1.20"));
	}
}
