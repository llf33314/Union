import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.gt.union.UnionApplication;
import com.gt.union.common.util.DateTimeKit;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.entity.basic.UnionCreateInfoRecord;
import com.gt.union.entity.basic.UnionTransferRecord;
import com.gt.union.service.basic.IUnionCreateInfoRecordService;
import com.gt.union.service.basic.IUnionTransferRecordService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

/**
 *
 * 测试类
 * Created by Administrator on 2017/8/11 0011.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes=UnionApplication.class)// 指定spring-boot的启动类
public class MyUnitTest {

	@Autowired
	RedisCacheUtil redisCacheUtil;

	@Autowired
	IUnionTransferRecordService unionTransferRecordService;

	@Test
	public void test1(){
		UnionTransferRecord record = unionTransferRecordService.get(null,4);
		System.out.println(record);
	}

}
