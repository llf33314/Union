import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.plugins.pagination.PageHelper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.gt.union.UnionApplication;
import com.gt.union.api.client.dict.DictService;
import com.gt.union.api.client.sms.SmsService;
import com.gt.union.api.client.sms.impl.SmsServiceImpl;
import com.gt.union.common.util.DateTimeKit;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.entity.basic.UnionCreateInfoRecord;
import com.gt.union.entity.basic.UnionMain;
import com.gt.union.entity.basic.UnionMember;
import com.gt.union.entity.basic.UnionTransferRecord;
import com.gt.union.service.basic.*;
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

	@Test
	public void test1() throws Exception {
		Map param = new HashMap<String,Object>();
		Map obj = new HashMap<String,Object>();
		param.put("mobiles","15986670850");
		param.put("company","aa");
		param.put("busId",33);
		param.put("model",0);
		param.put("content","dfasgfa");
		obj.put("reqdata",param);
		System.out.println(smsService.sendSms(obj));
	}

}
