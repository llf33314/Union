import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.UnionApplication;
import com.gt.union.api.client.address.AddressService;
import com.gt.union.api.client.sms.SmsService;
import com.gt.union.card.service.IUnionCardService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.util.RandomKit;
import com.gt.union.common.util.RedisCacheUtil;
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

	@Autowired
	private RedisCacheUtil redisCacheUtil;

	@Test
	public void test1() throws Exception {
		redisCacheUtil.set("aafa","hsadfas",50l);
		System.out.println(redisCacheUtil.get("aafa"));

	}

	public static void main(String[] args) {
		System.out.println(Double.valueOf("1.20"));
	}
}
