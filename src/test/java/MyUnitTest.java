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
import com.gt.union.entity.common.BusUser;
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

	@Autowired
	private AddressService addressService;

	@Autowired
	private BusUserService busUserService;

	@Autowired
	private IUnionTransferRecordService unionTransferRecordService;

	@Test
	public void test1() throws Exception {
		Map<String,Object> data = new HashMap<String,Object>();
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("p1","value1");
		param.put("p2","value2");
		data.put("reqdata",param);
		Map result = HttpClienUtils.reqPostUTF8(JSONObject.toJSONString(data),"http://127.0.0.1:8080/api/8A5DA52E/phoneCode", Map.class, "UNION2017");
		System.out.println(result);
	}

	public static void main(String[] args) {
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("reqdata","15986670850");
		Map result = HttpClienUtils.reqPostUTF8(JSONObject.toJSONString(data),"http://192.168.3.40:8080/api/8A5DA52E/phoneCode", Map.class, "UNION2017");
		System.out.println(result);
	}
}
