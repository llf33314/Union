import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.UnionApplication;
import com.gt.union.api.client.address.AddressService;
import com.gt.union.api.client.sms.SmsService;
import com.gt.union.card.service.IUnionCardService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.util.RandomKit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * 测试类
 * Created by Administrator on 2017/8/11 0011.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes=UnionApplication.class)// 指定spring-boot的启动类
public class MyUnitTest {

	@Autowired
	private IUnionCardService unionCardService;

	@Autowired
	private SmsService smsService;

	@Test
	public void test1() throws Exception {
		HashMap<String, Object> smsParams = new HashMap<String,Object>();
		smsParams.put("mobiles", "15986670850");
		smsParams.put("content", "绑定联盟卡，验证码:" + 123);
		smsParams.put("company", "谷通");
		smsParams.put("busId", 33);
		smsParams.put("model", 13);
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("reqdata",smsParams);
		smsService.sendSms(param);

	}

	public static void main(String[] args) {
		System.out.println(Double.valueOf("1.20"));
	}
}
